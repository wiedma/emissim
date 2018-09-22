package org.Streckennetz;

import java.util.ArrayList;
import org.Graphen.*;
import org.Verhalten.*;
import org.Verkehr.*;
import org.main.Simulation;
/**Quellen sind Fahrspuren ohne r�umliche Ausdehnung, welche Fahrzeuge erzeugen*/
public class Quelle extends Fahrspur {
	
	/**Zeit zu der das letzte Fahrzeug aufgesetzt wurde*/
	private double[] letzteZeit;
	/**Die Zeitl�cke, welche zwischen dem Aufsetzen zweier Fahrzeuge liegen soll*/
	private double[] zeitluecke;
	/**Anzahl der Fahrzeuge, die zur�ckgehalten werden, da der Abstand zum n�chsten Fahrzeug zu
	*gering ist*/
	private int[] rueckstau;
	/**Die Vorlauf-Strecken, die an diese Quelle anschlie�en*/
	private Vorlauf[] vorlaeufe;
	/**Wahrscheinlichkeit, mit der ein neu aufgesetztes Fahrzeug ein LKW ist*/
	private double lkwAnteil;
	/**Liste aller Strecken, die an dieser Quelle beginnen*/
	private ArrayList<Strecke> strecken;
	/**Quellen k�nnen aktiviert/deaktiviert werden*/
	private boolean aktiv = true;
	/**Die Gesamtanzahl erzeugter Fahrzeuge*/
	private static int fahrzeugeErzeugt;
	
	public Quelle(Fahrspur einfahrt, double lkwAnteil, double tempolimit){
		//Rufe den Konstruktor der Oberklasse auf
		super(0,0,tempolimit,false);
		
		this.lkwAnteil = lkwAnteil;
		
		//Initialisiere die Felder mit L�nge 1
		letzteZeit = new double[1];
		zeitluecke = new double[1];
		rueckstau = new int[1];
		vorlaeufe = new Vorlauf[1];
		//Erstelle den Vorlauf mit L�nge 1000m (vgl. [ER07] S.37f)
		vorlaeufe[0] = new Vorlauf(1000, tempolimit);
		//Verbinde den Vorlauf mit seinem Nachfolger
		Fahrspur.verbinde(vorlaeufe[0], einfahrt);
		//Verbinde den Verlauf hinten mit der Quelle
		vorlaeufe[0].vorherigeFahrspur = this;
		//Berechne die Zeitl�cke, die dieser Spur zugeordnet ist nach [ER07] S. 36
		zeitluecke[0] = 3600.0/verkehrsstaerke;
		//Intialisiere die Strecken-Liste
		strecken = new ArrayList<Strecke>();
		
	}
	
	public Quelle(Mehrspurbereich einfahrten, double lkwAnteil, double tempolimit) {
		
		//Rufe den Konstruktor der Oberklasse auf
		super(0,0,tempolimit,false);
		
		this.lkwAnteil = lkwAnteil;
		
		//Initialisiere die Felder mit der L�nge der Anzahl an Fahrspuren im Mehrspurbereich
		int anzahl = einfahrten.anzahlGeben();
		letzteZeit = new double[anzahl];
		zeitluecke = new double[anzahl];
		rueckstau = new int[anzahl];
		vorlaeufe = new Vorlauf[anzahl];
		
		
		/*Erstelle die Vorl�ufe mit einer L�nge proportional zur Anzahl der angeschlossenen
		* Fahrspuren */
		double vorlaufLaenge = Math.max(1000, anzahl * 500);
		for(int i = 0; i < anzahl; i++) {
			vorlaeufe[i] = new Vorlauf(vorlaufLaenge, tempolimit);
			//Verbinde die Vorl�ufe mit ihren Einfahrten
			Fahrspur.verbinde(vorlaeufe[i], einfahrten.fahrspurGeben(i));
			//Verbinde den Verlauf hinten mit der Quelle
			vorlaeufe[i].vorherigeFahrspur = this;
			//Ordne dem Vorlauf eine Zeitl�cke nach [ER07] S.36 zu
			zeitluecke[i] = 3600.0/(verkehrsstaerke/anzahl);
		}
		
		//Fasse die Vorl�ufe in einem Mehrspurbereich zusammen
		Mehrspurbereich vorlaufBereich = new Mehrspurbereich(vorlaeufe[0]);
		for(int i = 1; i < anzahl; i++) {
			vorlaufBereich.fahrspurHinzufuegen(vorlaeufe[i], vorlaeufe[i-1], true);
		}
		
		//Intialisiere die Strecken-Liste
		strecken = new ArrayList<Strecke>();
	}
	
	public void aktivSetzen(boolean aktiv) {
		this.aktiv = aktiv;
	}
	
	/**F�gt der Quelle eine neue Strecke hinzu*/
	public void streckeHinzufuegen(Strecke strecke) {
		strecken.add(strecke);
		verkehrsstaerkeAendern(strecke.verkehrsstaerkeGeben());
	}
	
	public Strecke streckeGeben(int index) {
		return strecken.get(index);
	}
	
	/**Gibt die Gesamtanzahl aller an dieser Quelle erzeugten Fahrzeuge*/
	public static int fahrzeugeErzeugt() {
		return fahrzeugeErzeugt;
	}
	
	/**Entscheide, ob ein Fahrzeug aufgesetzt werden soll und erzeuge ggf. eines*/
	@Override
	public void zeitschritt() {
		
		//F�r jede Fahrspur
		for(int i = 0; i < vorlaeufe.length; i++) {
			//Bewege ihre Fahrzeuge
			vorlaeufe[i].zeitschritt();
			//Wenn die Zeitl�cke dieser Fahrspur �berschritten wurde und die Quelle aktiv ist
			if((Simulation.zeitGeben() - letzteZeit[i]) >= zeitluecke[i] && aktiv) {
				//Setze ein Fahrzeug in den R�ckstau
				rueckstau[i] += 1;
				letzteZeit[i] = Simulation.zeitGeben();
			}
		}
		
		//F�r jedes Fahrzeug im R�ckstau
		for(int i = 0; i < rueckstau.length; i++) {
			if(rueckstau[i] > 0 && aktiv) {
				//Generiere ein Fahrzeug
				generiereFahrzeug(i);
			}
		}
	}
	
	
	/**Versuche ein Fahrzeug zu generieren und es auf den gew�nschten Vorlauf zu setzen
	 * @return Gibt an, ob die Generierung erfolgreich war. Die Generierung scheitert, wenn nicht ausreichend Sicherheitsabstand zum n�chsten Fahrzeug besteht
	 */
	private boolean generiereFahrzeug(int vorlauf) {
		Fahrzeug fahrzeug;
		
		//Wenn das Fahrzeug auf der rechten Spur generiert werden soll
		if(vorlauf == 0) {
			//Bestimme die Wahrscheinlichkeit mit der es sich um einen LKW handelt
			//LKWs werden nur auf der rechten Spur aufgesetzt
			double lkwWahrscheinlichkeit = vorlaeufe.length * lkwAnteil;
			//Generiere Zufallszahl
			if(Math.random() < lkwWahrscheinlichkeit) {
				//Erzeuge neuen LKW
				fahrzeug = new LKW();
			}
			else {
				//Erzeuge neuen PKW
				fahrzeug = new PKW();
			}	
		}
		//Wenn das Fahrzeug auf einer der anderen Spuren aufgesetzt werden soll
		else {
			//Setze einen neuen PKW auf
			fahrzeug = new PKW();
		}
		
		//Bestimme eine Strecke f�r das Fahrzeug
		//Generiere eine Zufallszahl zwischen 0 und der Verkehrsst�rke der Quelle
		double zufall = Math.random() * verkehrsstaerke;
		//Zwischenspeicher
		double pointer = 0;
		//Iteriere durch alle Strecken
		for(Strecke strecke : strecken) {
			//Addiere die Verkehrsst�rke der Strecke zum Zwischenspeicher
			pointer += strecke.verkehrsstaerkeGeben();
			//Pr�fe, ob die Zufallszahl mittlerweile �berschritten wurde
			if(pointer >= zufall) {
				//Weise dem Fahrzeug seine Strecke zu
				fahrzeug.streckeSetzen(strecke);
				//Verlasse die Schleife
				break;
			}
		}
		
		vorlaeufe[vorlauf].fahrzeugHinzufuegen(fahrzeug);
		fahrzeug.spurSetzen(vorlaeufe[vorlauf]);
		fahrzeug.geschwindigkeitSetzen(vorlaeufe[vorlauf].maxGeschwindigkeitGeben()/3.6);
		
		Hindernis h = fahrzeug.hindernisSuchen(HindernisRichtung.VORNE);
		
		if(h != null) {
			fahrzeug.geschwindigkeitSetzen(h.geschwindigkeitGeben());
			double sicherheitsbeduerfnis = fahrzeug.sicherheitsbeduerfnisGeben();
			double ax = (fahrzeug.laengeGeben()/2.0) + (h.zielFahrzeug().laengeGeben()/2.0) + 1 + 2 * sicherheitsbeduerfnis;
			double bx = ax + (1 + 7 * sicherheitsbeduerfnis) * Math.sqrt(h.geschwindigkeitGeben());
			//Wenn der Abstand zu gering ist
			if(h.entfernungGeben() < bx) {
				//Erzeuge das Fahrzeug noch nicht
				vorlaeufe[vorlauf].fahrzeugEntfernen(fahrzeug);
				fahrzeug.spurSetzen(null);
				return false;
			}
		}
		
		//Weise dem Fahrzeug eine Verhaltensklasse zu
		FahrverhaltenAbsicht verhalten = new FahrverhaltenAbsicht(fahrzeug);
		verhalten.absichtAnmelden(new AbstandHalten(fahrzeug));
//		fahrzeug.verhaltenSetzen(verhalten);
		fahrzeug.verhaltenSetzen(new FahrverhaltenWiedemann(fahrzeug));
		
		//Nur ausf�hren, wenn Erzeugung erfolgreich ist
		rueckstau[vorlauf] -= 1;
		Simulation.fahrzeugHinzufuegen(fahrzeug);
		fahrzeugeErzeugt++;
		return true;
	}
	
	@Override
	public void verkehrsstaerkeAendern(double aenderung) {
		//Aktualisiere die Verkehrsst�rke
		verkehrsstaerke = verkehrsstaerke += aenderung;
		//Aktualisiere die Zeitl�cken zum Aufsetzen neuer Fahrzeuge
		for(int i = 0; i < zeitluecke.length; i++) {
			zeitluecke[i] = 3600.0/(verkehrsstaerke/zeitluecke.length);
		}
	}
	
	@Override
	public void eintragen(Graph graph) {
		
		//Stelle sicher, dass kein Argument null ist
		if(graph == null) {
			return;
		}
		
		//Erzeuge neuen Knoten
		Knoten knoten = new Knoten(this);
		this.knoten = knoten;
		
		//F�ge den neuen Knoten dem Graphen hinzu
		graph.knotenHinzufuegen(knoten);
		
		//Markiere diese Fahrspur als eingetragen
		eingetragen = true;
		
		//Trage die Vorl�ufe ein und erstelle die Kanten
		for(Vorlauf vorlauf : vorlaeufe) {
			if(!vorlauf.istEingetragen()) {
				vorlauf.eintragen(graph);
			}
			
			knoten.kanteHinzufuegen(new Kante(vorlauf.knotenGeben(), 0));
		}
	}
	
	//Quellen melden keine Hindernisse
	@Override
	public Hindernis hindernisVorne(Fahrzeug sucher, double entfernung) {
		return null;
	}
	
	@Override
	public Hindernis hindernisHinten(Fahrzeug sucher, double entfernung) {
		return null;
	}
	
	@Override
	public Hindernis hindernisLinks(Fahrzeug sucher, double entfernung, boolean vorne) {
		return null;
	}
	
	@Override
	public Hindernis hindernisRechts(Fahrzeug sucher, double entfernung, boolean vorne) {
		return null;
	}

	@Override
	public void maxGeschwindigkeitSetzen(double maxGeschwindigkeit) {
		this.maxGeschwindigkeit = maxGeschwindigkeit;
		for(Vorlauf vor : vorlaeufe) {
			vor.maxGeschwindigkeitSetzen(maxGeschwindigkeit);
		}
	}
}

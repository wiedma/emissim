package org.Streckennetz;

import org.Graphen.*;
import org.Verkehr.*;
import org.main.Simulation;

public class Quelle extends Fahrspur {
	
	private double[] letzteZeit;
	private double[] zeitluecke;
	private int[] rueckstau;
	private Vorlauf[] vorlaeufe;
	private double lkwAnteil;
	
	public Quelle(Fahrspur einfahrt, double lkwAnteil){
		//Rufe den Konstruktor der Oberklasse auf
		super(0,0,0,false);
		
		this.lkwAnteil = lkwAnteil;
		
		//Initialisiere die Felder mit L�nge 1
		letzteZeit = new double[1];
		zeitluecke = new double[1];
		rueckstau = new int[1];
		vorlaeufe = new Vorlauf[1];
		//Erstelle den Vorlauf mit L�nge 1000m (vgl. [ER07] S.37f)
		vorlaeufe[0] = new Vorlauf(1000);
		//Verbinde den Vorlauf mit seinem Nachfolger
		Fahrspur.verbinde(vorlaeufe[0], einfahrt);
		//Verbinde den Verlauf hinten mit der Quelle
		vorlaeufe[0].vorherigeFahrspur = this;
		//Berechne die Zeitl�cke, die dieser Spur zugeordnet ist nach [ER07] S. 36
		zeitluecke[0] = 3600.0/verkehrsstaerke;
		
	}
	
	public Quelle(Mehrspurbereich einfahrten, double lkwAnteil) {
		
		//Rufe den Konstruktor der Oberklasse auf
		super(0,0,0,false);
		
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
			vorlaeufe[i] = new Vorlauf(vorlaufLaenge);
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
		
		
	}
	
	public void zeitschritt() {
		
		//F�r jede Fahrspur
		for(int i = 0; i < vorlaeufe.length; i++) {
			//Wenn die Zeitl�cke dieser Fahrspur �berschritten wurde
			if((Simulation.zeitGeben() - letzteZeit[i]) >= zeitluecke[i]) {
				//Setze ein Fahrzeug in den R�ckstau
				rueckstau[i] += 1;
			}
		}
		
		//F�r jedes Fahrzeug im R�ckstau
		for(int i = 0; i < rueckstau.length; i++) {
			if(rueckstau[i] > 0) {
				//Generiere ein Fahrzeug
				generiereFahrzeug(i);
			}
		}
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
	
	//Versuche ein Fahrzeug zu generieren und es auf den gew�nschten Vorlauf zu setzen
	private boolean generiereFahrzeug(int vorlauf) {
		Fahrzeug fahrzeug;
		//Generiere Zufallszahl zur Bestimmung von LKW/PKW
		if(Math.random() < lkwAnteil) {
			//Erzeuge neuen LKW
			fahrzeug = new LKW();
		}
		else {
			//Erzeuge neuen PKW
			fahrzeug = new PKW();
		}
		
		/*TODO Erst pr�fen, ob Sicherheitsabstand zum n�chsten Fahrzeug gr��er ist,
		* als BX des Fahrers*/
		vorlaeufe[vorlauf].fahrzeugHinzufuegen(fahrzeug);
		//Nur ausf�hren, wenn Erzeugung erfolgreich ist
		rueckstau[vorlauf] -= 1;
		return true;
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

}

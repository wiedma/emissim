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
		
		//Initialisiere die Felder mit Länge 1
		letzteZeit = new double[1];
		zeitluecke = new double[1];
		rueckstau = new int[1];
		vorlaeufe = new Vorlauf[1];
		//Erstelle den Vorlauf mit Länge 1000m (vgl. [ER07] S.37f)
		vorlaeufe[0] = new Vorlauf(1000);
		//Verbinde den Vorlauf mit seinem Nachfolger
		Fahrspur.verbinde(vorlaeufe[0], einfahrt);
		//Verbinde den Verlauf hinten mit der Quelle
		vorlaeufe[0].vorherigeFahrspur = this;
		//Berechne die Zeitlücke, die dieser Spur zugeordnet ist nach [ER07] S. 36
		zeitluecke[0] = 3600.0/verkehrsstaerke;
		
	}
	
	public Quelle(Mehrspurbereich einfahrten, double lkwAnteil) {
		
		//Rufe den Konstruktor der Oberklasse auf
		super(0,0,0,false);
		
		this.lkwAnteil = lkwAnteil;
		
		//Initialisiere die Felder mit der Länge der Anzahl an Fahrspuren im Mehrspurbereich
		int anzahl = einfahrten.anzahlGeben();
		letzteZeit = new double[anzahl];
		zeitluecke = new double[anzahl];
		rueckstau = new int[anzahl];
		vorlaeufe = new Vorlauf[anzahl];
		
		
		/*Erstelle die Vorläufe mit einer Länge proportional zur Anzahl der angeschlossenen
		* Fahrspuren */
		double vorlaufLaenge = Math.max(1000, anzahl * 500);
		for(int i = 0; i < anzahl; i++) {
			vorlaeufe[i] = new Vorlauf(vorlaufLaenge);
			//Verbinde die Vorläufe mit ihren Einfahrten
			Fahrspur.verbinde(vorlaeufe[i], einfahrten.fahrspurGeben(i));
			//Verbinde den Verlauf hinten mit der Quelle
			vorlaeufe[i].vorherigeFahrspur = this;
			//Ordne dem Vorlauf eine Zeitlücke nach [ER07] S.36 zu
			zeitluecke[i] = 3600.0/(verkehrsstaerke/anzahl);
		}
		
		//Fasse die Vorläufe in einem Mehrspurbereich zusammen
		Mehrspurbereich vorlaufBereich = new Mehrspurbereich(vorlaeufe[0]);
		for(int i = 1; i < anzahl; i++) {
			vorlaufBereich.fahrspurHinzufuegen(vorlaeufe[i], vorlaeufe[i-1], true);
		}
		
		
	}
	
	public void zeitschritt() {
		
		//Für jede Fahrspur
		for(int i = 0; i < vorlaeufe.length; i++) {
			//Wenn die Zeitlücke dieser Fahrspur überschritten wurde
			if((Simulation.zeitGeben() - letzteZeit[i]) >= zeitluecke[i]) {
				//Setze ein Fahrzeug in den Rückstau
				rueckstau[i] += 1;
			}
		}
		
		//Für jedes Fahrzeug im Rückstau
		for(int i = 0; i < rueckstau.length; i++) {
			if(rueckstau[i] > 0) {
				//Generiere ein Fahrzeug
				generiereFahrzeug(i);
			}
		}
	}
	
	@Override
	public void verkehrsstaerkeAendern(double aenderung) {
		//Aktualisiere die Verkehrsstärke
		verkehrsstaerke = verkehrsstaerke += aenderung;
		//Aktualisiere die Zeitlücken zum Aufsetzen neuer Fahrzeuge
		for(int i = 0; i < zeitluecke.length; i++) {
			zeitluecke[i] = 3600.0/(verkehrsstaerke/zeitluecke.length);
		}
	}
	
	//Versuche ein Fahrzeug zu generieren und es auf den gewünschten Vorlauf zu setzen
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
		
		/*TODO Erst prüfen, ob Sicherheitsabstand zum nächsten Fahrzeug größer ist,
		* als BX des Fahrers*/
		vorlaeufe[vorlauf].fahrzeugHinzufuegen(fahrzeug);
		//Nur ausführen, wenn Erzeugung erfolgreich ist
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
		
		//Füge den neuen Knoten dem Graphen hinzu
		graph.knotenHinzufuegen(knoten);
		
		//Markiere diese Fahrspur als eingetragen
		eingetragen = true;
		
		//Trage die Vorläufe ein und erstelle die Kanten
		for(Vorlauf vorlauf : vorlaeufe) {
			if(!vorlauf.istEingetragen()) {
				vorlauf.eintragen(graph);
			}
			
			knoten.kanteHinzufuegen(new Kante(vorlauf.knotenGeben(), 0));
		}
	}

}

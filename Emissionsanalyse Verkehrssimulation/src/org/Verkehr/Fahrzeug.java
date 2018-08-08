package org.Verkehr;
import org.PhysicEngine.*;
import org.Streckennetz.Fahrspur;
import org.Streckennetz.Strecke;
import org.Verhalten.Fahrverhalten;
import org.main.Main;

public abstract class Fahrzeug {
	
	/**Räumliche Dimensionen des Fahrzeugs in m bzw. m²*/
	protected double laenge, breite, hoehe, frontflaeche;
	
	/**Masse des Fahrzeugs in kg*/
	protected double masse;
	
	/**Verwendeter Kraftstoff*/
	protected Kraftstoffe kraftstoff;
	
	/**Wirkungsgrad des Motors*/
	protected double wirkungsgrad;
	
	/**Kennzahlen des Fahrzeugs ohne Einheit*/
	protected double rollreibungszahl, luftreibungszahl;
	
	/**Position als Koordinate im eindimensionalen System*/
	protected double pos;
	/**Die Fahrspur auf der sich das Fahrzeug befindet*/
	protected Fahrspur spur;
	
	/**Momentane Geschwindigkeit in m/s*/
	protected double geschwindigkeit;
	
	/**Momentan gewählter Gang*/
	protected int gang;
	
	/**Momentane Beschleunigung*/
	protected double beschleunigung;
	
	/**CO2-Sensorobjekt des Fahrzeugs*/
	protected CO2Sensor co2sensor;
	
	/**Strecke, die diesem Fahrzeug zugewiesen wurde*/
	protected Strecke strecke;
	
	/**Die Hindernisse in allen 6 Richtungen*/
	protected Hindernis hinVorne, hinHinten, hinVorneLinks, hinVorneRechts, hinHintenLinks, hinHintenRechts;
	/**Das Konkrete Fahrverhalten dieses Fahrzeuges*/
	protected Fahrverhalten verhalten;
	
	public Fahrzeug() {
		double[] specs = generiereFahrzeugSpecs();
		
		//Räumliche Ausdehnung
		laenge = specs[0];
		breite = specs[1];
		hoehe = specs[2];
		frontflaeche = breite * hoehe;
		
		//Masse des Fahrzeugs
		masse = specs[3];
		
		//Verwendeter Kraftstoff
		switch((int) specs[4]) {
		case 0: kraftstoff = Kraftstoffe.BENZIN;
				wirkungsgrad = Kraftstoffe.BENZIN.generiereWirkungsgrad(); break;
		case 1: kraftstoff = Kraftstoffe.DIESEL;
				wirkungsgrad = Kraftstoffe.DIESEL.generiereWirkungsgrad(); break;
		default: kraftstoff = Kraftstoffe.BENZIN;
				wirkungsgrad = Kraftstoffe.BENZIN.generiereWirkungsgrad(); break;
		}
		
		//Reibungskonstanten
		rollreibungszahl = specs[5];
		luftreibungszahl = specs[6];
		
		//Positionsstuff
		pos = 0;
		geschwindigkeit = 0;
		gang = 0;
		beschleunigung = 0;
		
		//CO2-Sensorobjekt
		if(!(this instanceof DummyFahrzeug)) {
			co2sensor = new CO2Sensor();
		}
		
	}
	
	/**Berechne die neue Position und Geschwindigkeit aus den momentanen Attributen*/
	public void zeitschritt() {
		//Bestimme alle Hindernisse in der Umgebung
		alleHindernisseSuchen();
		
		//Bestimme die neue Beschleunigung des Fahrzeuges
		beschleunigung = verhalten.beschleunigungBestimmen();
		
		//Neue Position
		pos = Physics.bewege(pos, geschwindigkeit, beschleunigung);
		
		//Emissionsdaten sammeln und speichern
		co2sensor.schreibeDaten(emissionBerechnen());
		
		//Neue Geschwindigkeit
		geschwindigkeit = geschwindigkeit + (beschleunigung * Physics.DELTA_TIME);
		
		//Setze die Hindernisse wieder auf null
		hinVorne = null;
		hinHinten = null;
		hinVorneRechts = null;
		hinHintenRechts = null;
		hinVorneLinks = null;
		hinHintenLinks = null;
	}
	
//	/**Ändere die Geschwindigkeit des Fahrzeugs innerhalb einer Zeiteinheit*/
//	public void beschleunige(double zielgeschwindigkeit) {
//		//a = dv/dt
//		beschleunigung = (geschwindigkeit-zielgeschwindigkeit)/Physics.DELTA_TIME;
//	}
	
	
	/**Berechne die CO₂-Emission des Fahrzeugs in dieser Zeiteinheit in kg*/
	public double emissionBerechnen() {
		//Die gefahrene Strecke durch Bewegungsgleichungen berechnen
		double strecke = Physics.bewege(0, geschwindigkeit, beschleunigung);
		//benötigte Energie berechnet sich aus der Summe der Fahrtwiderstandskräfte und der Strecke
		double benoetigteEnergie = strecke * (Physics.rollreibung(rollreibungszahl, masse)
				+ Physics.luftreibung(luftreibungszahl, frontflaeche, geschwindigkeit)
				+ Physics.beschleunigungswiderstand(masse, beschleunigung, gang));
		//Der tatsächliche Energieoutput des Motors wird über den Wirkungsgrad bestimmt
		double motorOutput = (1/wirkungsgrad) * benoetigteEnergie;
		//Die dazugehörige Emission hängt vom verwendeten Kraftstoff ab
		return kraftstoff.verbrenne(motorOutput);
	}
	
	/**Suche ein Hindernis in der angegebenen Richtung*/
	public Hindernis hindernisSuchen(HindernisRichtung richtung) {
		switch(richtung) {
		case VORNE: return spur.hindernisVorne(this, 0);
		case HINTEN: return spur.hindernisHinten(this, 0);
		case VORNE_RECHTS: return spur.hindernisRechts(this, 0, true);
		case VORNE_LINKS: return spur.hindernisLinks(this, 0, true);
		case HINTEN_RECHTS: return spur.hindernisRechts(this, 0, false);
		case HINTEN_LINKS: return spur.hindernisLinks(this, 0, false);
		default: return null;
		}
	}
	
	/**Suche nach Hindernis in allen Richtungen*/
	public void alleHindernisseSuchen() {
		if(hinVorne == null) {
			hinVorne = hindernisSuchen(HindernisRichtung.VORNE);
		}
		if(hinHinten == null) {
			hinHinten = hindernisSuchen(HindernisRichtung.HINTEN);
		}
		if(hinVorneLinks == null) {
			hinVorneLinks = hindernisSuchen(HindernisRichtung.VORNE_LINKS);
		}
		if(hinVorneRechts == null) {
			hinVorneRechts = hindernisSuchen(HindernisRichtung.VORNE_RECHTS);
		}
		if(hinHintenLinks == null) {
			hinHintenLinks = hindernisSuchen(HindernisRichtung.HINTEN_LINKS);
		}
		if(hinHintenRechts == null) {
			hinHintenRechts = hindernisSuchen(HindernisRichtung.HINTEN_RECHTS);
		}
	}
	
	/**Generiere zufällig die Spezifikationen des Fahrzeuges*/
	protected abstract double[] generiereFahrzeugSpecs();
	
//Getter und Setter -----------------------------------------------------------------------------
	
	public double laengeGeben() {
		return laenge;
	}
	
	public double wirkungsgradGeben() {
		return wirkungsgrad;
	}
	
	public double posGeben() {
		return pos;
	}
	
	public void posSetzen(double pos) {
		this.pos = pos;
	}
	
	public Fahrspur spurGeben() {
		return this.spur;
	}
	
	public void spurSetzen(Fahrspur spur) {
		this.spur = spur;
	}
	
	public double geschwindigkeitGeben() {
		return geschwindigkeit;
	}
	
	public double beschleunigungGeben() {
		return beschleunigung;
	}
	
	public void streckeSetzen(Strecke strecke) {
		this.strecke = strecke;
	}
	
	public void sensorAktivieren() {
		co2sensor.aktiviere();
	}
	
	public void hindernisSetzen(Hindernis hindernis, HindernisRichtung richtung) {
		switch(richtung) {
		case VORNE: hinVorne = hindernis;
			break;
		case HINTEN: hinHinten = hindernis;
			break;
		case VORNE_LINKS: hinVorneLinks = hindernis;
			break;
		case VORNE_RECHTS: hinVorneRechts = hindernis;
			break;
		case HINTEN_LINKS: hinHintenLinks = hindernis;
			break;
		case HINTEN_RECHTS: hinHintenRechts = hindernis;
			break;
		}
	}
	
	public Hindernis hindernisGeben(HindernisRichtung richtung) {
		switch(richtung) {
		case VORNE: return hinVorne;
		case HINTEN: return hinHinten;
		case VORNE_LINKS: return hinVorneLinks;
		case VORNE_RECHTS: return hinVorneRechts;
		case HINTEN_LINKS: return hinHintenLinks;
		case HINTEN_RECHTS: return hinHintenRechts;
		default: return null;
		}
	}
//------------------------------------------------------------------------------------------------
}

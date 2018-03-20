package org.Verkehr;
import org.PhysicEngine.*;

public abstract class Fahrzeug {
	
	//Räumliche Dimensionen des Fahrzeugs in m bzw. m²
	private double laenge;
	private double breite;
	private double hoehe;
	private double frontflaeche;
	
	//Masse des Fahrzeugs in kg
	private double masse;
	
	//Verwendeter Kraftstoff
	private Kraftstoffe kraftstoff;
	
	//Wirkungsgrad des Motors
	private double wirkungsgrad;
	
	//Kennzahlen des Fahrzeugs ohne Einheit
	private double rollreibungszahl;
	private double luftreibungszahl;
	
	//Position als Koordinate im eindimensionalen System
	private double pos;
	
	//Momentane Geschwindigkeit
	private double geschwindigkeit;
	
	//Momentan gewählter Gang
	private int gang;
	
	//Momentane Beschleunigung
	private double beschleunigung;
	
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
		
	}
	
	//Berechne die neue Position und Geschwindigkeit aus den momentanen Attributen
	public void update() {
		//Neue Position
		pos = Physics.bewege(pos, geschwindigkeit, beschleunigung);
		
		//TODO: Emissionsdaten sammeln und speichern
		
		
		//Neue Geschwindigkeit
		geschwindigkeit = geschwindigkeit + (beschleunigung * Physics.DELTA_TIME);
		
		
		//Nach der Zeiteinheit wird die Beschleunigung zurückgesetzt
		beschleunigung = 0;
	}
	
	//Ändere die Geschwindigkeit des Fahrzeugs innerhalb einer Zeiteinheit
	public void beschleunige(double zielgeschwindigkeit) {
		//a = dv/dt
		beschleunigung = (geschwindigkeit-zielgeschwindigkeit)/Physics.DELTA_TIME;
	}
	
	
	//CO₂-Emission des Fahrzeugs in kg/Zeiteinheit
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
	
	protected abstract double[] generiereFahrzeugSpecs();
}

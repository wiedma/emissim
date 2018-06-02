package org.Streckennetz;
import org.Verkehr.Fahrzeug;

public class Vorlauf extends Gerade {
	
	public Vorlauf(double laenge) {
		super(laenge, 2, 500, false);
	}
	
	@Override
	//Übergibt das Fahrzeug an die nachfolgende Fahrspur
	protected void uebergebeFahrzeug(Fahrzeug fahrzeug) {
		//Referenzen des Fahrzeugs neu setzen
		fahrzeug.posSetzen(fahrzeug.posGeben() - laenge);
		fahrzeug.spurSetzen(naechsteFahrspur);
		//Aktiviere den Sensor des Fahrzeugs
		fahrzeug.sensorAktivieren();
		
		//Referenzen der Spuren neu setzen
		naechsteFahrspur.fahrzeugHinzufuegen(fahrzeug);
		this.fahrzeugEntfernen(fahrzeug);
	}

}

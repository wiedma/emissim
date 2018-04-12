package org.Verkehr;

import org.PhysicEngine.Physics;

public class PKW extends Fahrzeug {
	
	public PKW() {
		super();
	}

	@Override
	protected double[] generiereFahrzeugSpecs() {
		
		//Längenverteilung nach [ER07]
		double laenge = Math.random() + 4.0;
		
		//Breitenverteilung nach [ER07]
		double breite = Physics.normalverteilung(1.80, 0.1);
		
		//Höhenverteilung nach eigener Abschätzung
		double hoehe = Physics.normalverteilung(1.50, 0.2);
		
		//Massenverteilung nach [FO13], Standartabweichung nach eigener Abschätzung
		double masse = Physics.normalverteilung(1484, 100);
		
		//Verteilung der Kraftstoffarten nach [KR17]
		double kraftstoff = Math.random() * 100; //Zufallszahl
		
		if(kraftstoff > 66.50) {
			kraftstoff = 1; //Diesel etwa 33.5% aller PKW
		}
		else {
			kraftstoff = 0; //Benzin etwa 66.55% aller PKW
		}
		
		//Rollreibungszahl nach [BO98]
		double rollreibung = ((Math.random() * 2) + 1)/100;
		
		//Luftreibungszahl nach [BO98]
		double luftreibung = (Math.random() + 3)/10;
		
		
		return new double [] {laenge, breite, hoehe, masse, kraftstoff, rollreibung, luftreibung};
	}

}

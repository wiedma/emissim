package org.Verkehr;

import org.PhysicEngine.Physics;

public class PKW extends Fahrzeug {
	
	public PKW() {
		super();
	}

	@Override
	protected double[] generiereFahrzeugSpecs() {
		
		//Fahrzeugspezifikationen
		double[] specs = new double[7];
		
		//L�ngenverteilung nach [ER07]
		double laenge = Math.random() + 4.0;
		
		//Breitenverteilung nach [ER07]
		double breite = Physics.normalverteilung(1.80, 0.1);
		
		//H�henverteilung nach eigener Absch�tzung
		double hoehe = Physics.normalverteilung(1.50, 0.2);
		
		//Massenverteilung nach [FO13], Standartabweichung nach eigener Absch�tzung
		double masse = Physics.normalverteilung(1484, 100);
		
		return null;
	}

}

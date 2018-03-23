package org.Verkehr;

import org.PhysicEngine.Physics;

public class LKW extends Fahrzeug {
	
	public LKW(){
		super();
	}

	@Override
	protected double[] generiereFahrzeugSpecs() {
		// TODO Auto-generated method stub
		
		//Längenverteilung nach [ER07]
		double laenge = (Math.random() * 2) + 13;
		
		//Breitenverteilung nach [ER07]
		double breite = (Math.random()*0.55) + 2;
		
		//Höhenverteilung nach eigener Abschätzung (4m sind gesetzlich das Maximum nach § 32 StVZO)
		double hoehe = Physics.normalverteilung(32, 6)/10;
		
		//TODO restliche Maße bestimmen und eintragen
		
		return null;
	}

}

package org.Verkehr;

import org.PhysicEngine.Physics;

public class LKW extends Fahrzeug {
	
	public LKW(){
		super();
	}

	@Override
	protected double[] generiereFahrzeugSpecs() {
		// TODO Auto-generated method stub
		
		//L�ngenverteilung nach [ER07]
		double laenge = (Math.random() * 2) + 13;
		
		//Breitenverteilung nach [ER07]
		double breite = (Math.random()*0.55) + 2;
		
		//H�henverteilung nach eigener Absch�tzung (4m sind gesetzlich das Maximum nach � 32 StVZO)
		double hoehe = Physics.normalverteilung(32, 6)/10;
		
		//TODO restliche Ma�e bestimmen und eintragen
		
		return null;
	}

}

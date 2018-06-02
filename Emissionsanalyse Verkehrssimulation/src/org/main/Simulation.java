package org.main;

import org.PhysicEngine.Physics;
import org.Streckennetz.Netz;
import org.Verkehr.Fahrzeug;

public class Simulation {
	
	//TODO Simulations-Klasse modellieren
	
	private static double zeit;
	private static Netz netz;
	
//Getter und Setter ------------------------------------------------------------------------------
	
	public static double zeitGeben() {
		return zeit;
	}
	
	public static void netzSetzen(Netz netz) {
		Simulation.netz = netz;
	}
	
//------------------------------------------------------------------------------------------------
	
	public void zeitschritt() {
		zeit += Physics.DELTA_TIME;
	}
	
	public static void fahrzeugHinzufuegen(Fahrzeug fahr) {
		netz.fahrzeugHinzufuegen(fahr);
	}
	

}

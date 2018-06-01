package org.main;

import org.PhysicEngine.Physics;
import org.Streckennetz.Netz;

public class Simulation {
	
	//TODO Simulations-Klasse modellieren
	
	private static double zeit;
	private Netz netz;
	
	public Simulation(Netz netz) {
		this.netz = netz;
		zeit = 0;
	}
	
//Getter und Setter ------------------------------------------------------------------------------
	
	public static double zeitGeben() {
		return zeit;
	}
	
//------------------------------------------------------------------------------------------------
	
	public void zeitschritt() {
		zeit += Physics.DELTA_TIME;
	}
	

}

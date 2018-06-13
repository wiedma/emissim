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
	
	public static void zeitschritt() {
		long millis = System.currentTimeMillis();
		System.out.println("Fahrzeuge: " + netz.anzahlFahrzeuge());
		zeit += Physics.DELTA_TIME;
		netz.zeitschritt();
		System.out.println("Dauer: " + (System.currentTimeMillis() - millis));
	}
	
	public static void fahrzeugHinzufuegen(Fahrzeug fahr) {
		netz.fahrzeugHinzufuegen(fahr);
	}
	
	public static void fahrzeugEntfernen(Fahrzeug fahr) {
		netz.fahrzeugEntfernen(fahr);
	}
	

}

package org.PhysicEngine;

public enum Kraftstoffe {
	
	DIESEL(43.35,0.840,2.630), BENZIN(43.9,0.750,2.330);
	
	//Heizwert des Kraftstoffes in MJ/kg
	//Werte aus [HA08]
	private double heizwert;
	
	//Dichte in kg/l bei Normalbedingunen
	//1 g/(cm^3) = 1 kg/l
	//Werte aus [AL13] (Benzin), [AR10] (Diesel)
	private double dichte;
	
	//CO₂-Emissionskennzahl in kg/l
	//Werte aus [BA16]
	private double co2Emission;
	
	private Kraftstoffe(double heizwert, double dichte, double co2Emission) {
		this.heizwert = heizwert;
		this.dichte = dichte;
		this.co2Emission = co2Emission;
	}
	
	//Berechne die Emissionen, die bei der Verbrennung bis zu einer gewissen Energie entstehen
	//Einheit: kg CO₂
	public double verbrenne(double energie) {
//		double masse = energie/heizwert;
//		double volumen = masse/dichte;
//		double co2EmissionsMasse = co2Emission * volumen;
//		return co2EmissionsMasse;
		
		return ((energie/heizwert)/dichte) * co2Emission;
	}

}

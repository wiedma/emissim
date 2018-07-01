package org.PhysicEngine;
/**Ein Enum, welches die verschiedenen Kraftstoffe mit ihren Kennzahlen bereitstellt*/
public enum Kraftstoffe {
	
	DIESEL(43.35,0.840,2.630, 0.35), BENZIN(43.9,0.750,2.330, 0.25);
	
	/**Heizwert des Kraftstoffes in MJ/kg
	Werte aus [HA08]*/
	private double heizwert;
	
	/**Dichte in kg/l bei Normalbedingunen
	1 g/(cm^3) = 1 kg/l
	Werte aus [AL13] (Benzin), [AR10] (Diesel)*/
	private double dichte;
	
	/**CO₂-Emissionskennzahl in kg/l
	Werte aus [BA16]*/
	private double co2Emission;
	
	/**Erwartungswert der Normalverteilung des Motorwirkungsgrad
	 * Werte aus [BO99]*/
	private double wirkungErwartung;
	
	private Kraftstoffe(double heizwert, double dichte, double co2Emission, double wirkungErwartung) {
		this.heizwert = heizwert;
		this.dichte = dichte;
		this.co2Emission = co2Emission;
		this.wirkungErwartung = wirkungErwartung;
	}
	
	/**Berechne die Emissionen, die bei der Verbrennung bis zu einer gewissen Energie entstehen
	Einheit: kg CO₂*/
	public double verbrenne(double energie) {
//		double masse = energie*10^-6/heizwert;
//		double volumen = masse/dichte;
//		double co2EmissionsMasse = co2Emission * volumen;
//		return co2EmissionsMasse;
		
		return (((energie/1000000)/heizwert)/dichte) * co2Emission;
	}
	
	/**Generiere einen normalverteilten Wirkungsgrad für einen Motor dieses Kraftstoffs
	Erwartungswerte nach [BO99], Standardabweichung nach eigener Abschätzung*/
	public double generiereWirkungsgrad() {
		return (1.0/100.0) * Physics.normalverteilung(100*wirkungErwartung, 3);
	}

}

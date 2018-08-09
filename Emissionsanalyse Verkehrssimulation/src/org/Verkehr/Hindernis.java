package org.Verkehr;
/**Verwaltet alle Information über ein gefundenes Hindernis.
 * Hindernis kann ein anderes Fahrzeug, eine Senke oder auch etwa eine Ampel sein
 */
public class Hindernis {
	
	/**Die Entfernung des Betrachters zum Hindernis*/
	private double entfernung;
	/**Die Geschwindigkeit, mit der sich das Hindernis bewegt*/
	private double geschwindigkeit;
	/**Das Objekt, welches das Hindernis darstellt*/
	private Object typ;
	/**Der Betrachter dieses Hindernisses*/
	private Fahrzeug betrachter;
	/**Liegt das Hindernis in Fahrtrichtung?*/
	private boolean inFahrtrichtung;
	/**Befinden sich Hindernis und Betrachter auf der selben Fahrspur?*/
	private boolean gleicheSpur;
	
	public Hindernis(double entfernung, double geschwindigkeit, Object typ, Fahrzeug betrachter,
			boolean inFahrtrichtung, boolean gleicheSpur) {
		this.entfernung = entfernung;
		this.geschwindigkeit = geschwindigkeit;
		this.betrachter = betrachter;
		this.inFahrtrichtung = inFahrtrichtung;
		this.gleicheSpur = gleicheSpur;
		this.typ = typ;
	}
	
//Getter und Setter -------------------------------------------------------------------------------
	
	public double entfernungGeben() {
		return entfernung;
	}
	
	public Fahrzeug zielFahrzeug() {
		return (Fahrzeug) typ;
	}
	
	public void betrachterSetzen(Fahrzeug betrachter) {
		this.betrachter = betrachter;
	}
	
	public boolean istGleicheFahrspur() {
		return gleicheSpur;
	}
	
//-------------------------------------------------------------------------------------------------
	
	/**Zeit bis zur Kollision bei weiterer gleichförmiger Bewegung
	 * @return Die Zeit, die bis zur Kollision verbleibt. Findet keine Kollision statt ist das Ergebnis -1
	 */
	public double kollisionszeit() {
		double geschwindigkeitBetrachter = betrachter.geschwindigkeitGeben();
		//Wenn eine Kollision ausgeschlossen ist
		if((inFahrtrichtung && geschwindigkeitBetrachter < geschwindigkeit)
				|| (!inFahrtrichtung && geschwindigkeitBetrachter > geschwindigkeit)) {
			//Gib -1 zurück
			return -1;
		}
		//Berechne die Differenz der beiden Geschwindigkeiten
		double differenz;
		if(inFahrtrichtung) {
			differenz = geschwindigkeitBetrachter - geschwindigkeit;
		}
		else {
			differenz = geschwindigkeit - geschwindigkeitBetrachter;
		}
		//v = dx/dt --> dt = dx/v
		return entfernung/differenz;
	}
	
	public double geschwindigkeitGeben() {
		return geschwindigkeit;
	}
	
}

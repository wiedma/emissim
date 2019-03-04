package org.Verkehr;
import org.PhysicEngine.Physics;
/**LKWs sind spezielle Fahrzeuge*/
public class LKW extends Fahrzeug {
	
	public LKW(){
		super();
	}

	/**Generiere zuf�llig die Spezifikationen des Fahrzeugs.
	 * L�ngenverteilung nach [ER07],
	 * Breitenverteilung nach [ER07],
	 * H�henverteilung nach eigener Absch�tzung (4m sind gesetzlich das Maximum nach � 32 StVZO),
	 * Gewichtsverteilung nach eigener Absch�tzung (Grenzwerte nach � 34 StVZO),
	 * Als Kraftstoff f�r LKW wird der LKW-Diesel verwendet, welcher die selbe DIN-Normen wie normaler Diesel erf�llt,
	 * Rollreibung nach [AL13],
	 * Luftreibung nach [AL13].
	 */
	@Override
	protected double[] generiereFahrzeugSpecs() {
		
		//L�ngenverteilung nach [ER07]
		double laenge = (Math.random() * 2) + 13;
		
		//Breitenverteilung nach [ER07]
		double breite = (Math.random()*0.55) + 2;
		
		//H�henverteilung nach eigener Absch�tzung (4m sind gesetzlich das Maximum nach � 32 StVZO)
		double hoehe = Physics.normalverteilung(32, 6)/10;
		
		//Gewichtsverteilung nach eigener Absch�tzung (Grenzwerte nach � 34 StVZO)
		double masse = Physics.normalverteilung(30, 10) * 1000;
		
		//Als Kraftstoff f�r LKW wird der LKW-Diesel verwendet, welcher die selbe DIN-Normen wie
		//normaler Diesel erf�llt
		double kraftstoff = 1;
		
		//[AL13]
		double rollreibung = ((Math.random() * 4) + 6)/1000;
		
		//[AL13]
		double luftreibung = 0.8;
		
		return new double [] {laenge, breite, hoehe, masse, kraftstoff, rollreibung, luftreibung};
		
	}

}

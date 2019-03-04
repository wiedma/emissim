package org.Verkehr;

import org.main.Simulation;
/**Ein Sensor zum Sammeln der Kohlenstoffdioxid-Emissionen des Fahrzeugs*/
public class CO2Sensor implements Sensor<Double>{
	
	/**Auf manchen Strecken wird die Messung unterbunden (z.B. Vorläufe)*/
	private boolean aktiv;
	
	public CO2Sensor() {
		aktiv = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void schreibeDaten(Double daten) {
		//Wenn der Sensor aktiviert wurde
		if(aktiv) {
			//Lege die gemessenen Daten in der Tabelle ab
			Simulation.sammleCO2Daten(daten);
		}
	}
	
	public void aktiviere() {
		aktiv = true;
	}
	
	public void deaktiviere() {
		aktiv = false;
	}

}

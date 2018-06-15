package org.Verkehr;

import org.main.Simulation;

public class CO2Sensor implements Sensor<Double>{
	
	//NEXT Datenspeicherung überarbeiten, Excel verwerfen (?)
	
	//Auf manchen Strecken wird die Messung unterbunden (z.B. Vorläufe)
	private boolean aktiv;
	
	public CO2Sensor() {
		aktiv = false;
	}


	@Override
	public void schreibeDaten(double daten) {
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

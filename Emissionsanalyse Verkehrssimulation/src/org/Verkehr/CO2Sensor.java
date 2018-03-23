package org.Verkehr;

import java.io.File;
import java.io.IOException;

public class CO2Sensor implements Sensor<Double>{
	
	//Gemessenes Kohlendioxid in kg (total)
	private double co2Emission;
	
	//Datei in der die Messwerte abgelegt werden
	private File targetFile;
	
	public CO2Sensor(File targetFile) {
		this.targetFile = targetFile;
		//TODO Datei erzeugen
//		if(!targetFile.exists()) {
//			try {
//				targetFile.createNewFile();
//			}catch(IOException e) {
//				e.printStackTrace();
//				System.out.println("Die Ausgabedatei kann nicht erzeugt werden");
//			}
//		}
	}

	@Override
	public void sammleDaten(Double daten) {
		if(co2Emission + daten != Double.MAX_VALUE) {
			co2Emission += daten;
		}
		else {
			schreibeDaten(targetFile);
		}
		
	}

	@Override
	public void schreibeDaten(File file) {
		// TODO Daten in eine Exceltabelle ablegen
	}

}

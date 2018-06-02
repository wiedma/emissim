package org.Verkehr;

import java.io.File;
//import java.io.IOException;

public class CO2Sensor implements Sensor<Double>{
	
	//Datei in der die Messwerte abgelegt werden
	private File targetFile;
	private boolean aktiv;
	
	public CO2Sensor(File targetFile) {
		this.targetFile = targetFile;
		aktiv = false;
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
	public void schreibeDaten(double daten) {
		// TODO Daten in eine Exceltabelle ablegen (mit Zeitpunkt, nur wenn Sensor aktiviert wurde)
	}
	
	public void aktiviere() {
		aktiv = true;
	}
	
	public void deaktiviere() {
		aktiv = false;
	}

}

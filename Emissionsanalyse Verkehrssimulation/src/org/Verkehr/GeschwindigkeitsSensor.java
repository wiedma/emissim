package org.Verkehr;

import java.util.ArrayList;

public class GeschwindigkeitsSensor implements Sensor<Double> {
	
	private ArrayList<Double> vList;
	private boolean aktiv;
	
	public GeschwindigkeitsSensor() {
		vList = new ArrayList<Double>();
		aktiv = false;
	}

	@Override
	public void schreibeDaten(Double daten) {
		if(aktiv) {
			vList.add(daten);
		}
	}
	
	public double mittlereGeschwindigkeit() {
		double sum = 0;
		for(double d : vList) {
			sum += d;
		}
		return (sum/vList.size());
	}
	
	public void aktviere() {
		aktiv = true;
	}
	
	public void deaktiviere() {
		aktiv = false;
	}

}

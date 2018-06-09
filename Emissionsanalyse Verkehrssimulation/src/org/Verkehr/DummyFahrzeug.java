package org.Verkehr;

public class DummyFahrzeug extends Fahrzeug {
	//Dummy zur Hinderniserkennung auf benachbarten Spuren
	public DummyFahrzeug(double laenge, double geschwindigkeit, double pos) {
		this.laenge = laenge;
		this.geschwindigkeit = geschwindigkeit;
		this.pos = pos;
	}

	@Override
	public double[] generiereFahrzeugSpecs() {
		return null;
	}

}

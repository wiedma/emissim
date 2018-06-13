package org.Verkehr;

public class DummyFahrzeug extends Fahrzeug {
	//Das Fahrzeug für welches dieser Dummy nach Hindernissen sucht
	private Fahrzeug original;
	//Gibt an, ob der Dummy links oder rechts vom Original ist
	private boolean links;
	//Dummy zur Hinderniserkennung auf benachbarten Spuren
	public DummyFahrzeug(Fahrzeug original, boolean links) {
		this.laenge = original.laenge;
		this.geschwindigkeit = original.geschwindigkeit;
		this.pos = original.pos;
		this.original = original;
		this.links = links;
	}

	@Override
	public double[] generiereFahrzeugSpecs() {
		return new double[] {0,0,0,0,0,0,0};
	}
	
	public Fahrzeug originalGeben() {
		return original;
	}
	
	public boolean istLinks() {
		return links;
	}

}

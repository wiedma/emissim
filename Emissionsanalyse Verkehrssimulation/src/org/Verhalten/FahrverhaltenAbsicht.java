package org.Verhalten;

import java.util.ArrayList;
import org.Verkehr.Fahrzeug;

public class FahrverhaltenAbsicht extends Fahrverhalten {
	
	private ArrayList<Absicht> absichten;

	public FahrverhaltenAbsicht(Fahrzeug f) {
		super(f);
	}
	
	public void absichtAnmelden(Absicht absicht) {
		absichten.add(absicht);
	}

	@Override
	public double beschleunigungBestimmen() {
		//TODO Abstimmung durchführen und daraus eine Beschleunigung berechnen
		return 0;
	}

	@Override
	public String spurwechselBestimmen() {
		// TODO Abstimmung durchführen und daraus eine Richtung für einen Spurwechsel bestimmen
		return null;
	}

	@Override
	public void tempolimitAktualisieren(double tempolimitNeu) {
		for(Absicht absicht : absichten) {
			absicht.tempolimitAktualisieren(tempolimitNeu);
		}
	}
	
	private double abstimmen(double[] stimmen) {
		//TODO Abstimmungsprozess implementieren
		return 0;
	}

}

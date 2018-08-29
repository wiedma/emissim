package org.Verhalten;

import java.util.ArrayList;
import org.Verkehr.Fahrzeug;
import org.Verkehr.PKW;

public class FahrverhaltenAbsicht extends Fahrverhalten {
	
	private ArrayList<Absicht> absichten;

	public FahrverhaltenAbsicht(Fahrzeug f) {
		super(f);
		absichten = new ArrayList<Absicht>();
	}
	
	public void absichtAnmelden(Absicht absicht) {
		absichten.add(absicht);
	}

	@Override
	public double beschleunigungBestimmen() {
		
		//Hole von jeder Absicht die Abstimmung für die Beschleunigung ein
		double[] stimmen = new double[absichten.size()];
		for(int i = 0; i < absichten.size(); i++) {
			absichten.get(i).anpassen();
			stimmen[i] = absichten.get(i).wert(0);
		}
		
		//Stimme über die zu wählende Beschleunigung ab
		double ergebnis = abstimmen(stimmen);
		
		//Berechne Bmin und Bmax
		double bmax, bmin;
		if(f instanceof PKW) {
			bmax = (0.2 + 0.8 * f.beschleunigungswilleGeben()) * (7 - Math.sqrt(f.geschwindigkeitGeben()));
			bmin = -8 - 2 * f.beschleunigungswilleGeben() + 0.5 * Math.sqrt(f.geschwindigkeitGeben());
		}
		else {
			bmax = 1.581 - 0.057 * f.geschwindigkeitGeben() + (0.6 - 0.01 * f.geschwindigkeitGeben()) * f.beschleunigungswilleGeben();
			bmin = -6 - 2 * f.beschleunigungswilleGeben() + 0.09 * f.geschwindigkeitGeben();
		}
		
		//Berechne die zu wählende Beschleunigung und gib diese zurück
		if(Math.signum(ergebnis) > 0) {
			return ergebnis * bmax;
		}
		else if(Math.signum(ergebnis) < 0) {
			return - (Math.abs(ergebnis) * Math.abs(bmin));
		}
		else {
			return 0;
		}
	}

	@Override
	public boolean spurwechselBestimmen(boolean links) {
		// TODO Abstimmung durchführen und daraus eine Richtung für einen Spurwechsel bestimmen
		return false;
	}

	@Override
	public double tempolimitAktualisieren(double tempolimitNeu) {
		double wunschgeschwindigkeit = 0;
		for(Absicht absicht : absichten) {
			wunschgeschwindigkeit = absicht.tempolimitAktualisieren(tempolimitNeu);
		}
		return wunschgeschwindigkeit;
	}
	
	private double abstimmen(double[] stimmen) {
		double produkt = 1;
		for(double stimme : stimmen) {
			produkt = produkt * (stimme + 1);
		}
		double wurzel = Math.pow(produkt, (1/stimmen.length));
		return wurzel - 1;
	}

}

package org.Verhalten;
import org.Verkehr.Fahrzeug;

public abstract class Fahrverhalten {
	
	protected Fahrzeug f;
	
	public Fahrverhalten(Fahrzeug f) {
		this. f = f;
	}
	
	public abstract double beschleunigungBestimmen();
	public abstract String spurwechselBestimmen();
	public abstract void tempolimitAktualisieren(double tempolimitNeu);
}

package org.Verhalten;
import org.PhysicEngine.Physics;
import org.Verkehr.Fahrzeug;
import org.Verkehr.PKW;

public abstract class Fahrverhalten {
	
	protected Fahrzeug f;
	protected double wunschgeschwindigkeit;
	
	public Fahrverhalten(Fahrzeug f) {
		this. f = f;
	}
	
	public abstract double beschleunigungBestimmen();
	public abstract boolean spurwechselBestimmen(boolean links);
	
	public double tempolimitAktualisieren(double tempolimit) {
		if(f instanceof PKW) {
			if(tempolimit > 120) {
				wunschgeschwindigkeit = Physics.normalverteilung(142, 20);
			}
			else if(tempolimit > 100) {
				wunschgeschwindigkeit = Physics.normalverteilung(120, 20);
			}
			else if(tempolimit > 80) {
				wunschgeschwindigkeit = Physics.normalverteilung(110, 18);
			}
			else if(tempolimit > 60) {
				wunschgeschwindigkeit = Physics.normalverteilung(100, 15);
			}
			else if(tempolimit > 50) {
				wunschgeschwindigkeit = Physics.normalverteilung(80, 15);
			}
			else {
				wunschgeschwindigkeit = Physics.normalverteilung(50, 10);
			}
		}
		else {
			if(tempolimit > 120) {
				wunschgeschwindigkeit = Physics.normalverteilung(92, 5);
			}
			else if(tempolimit > 100) {
				wunschgeschwindigkeit = Physics.normalverteilung(91, 5);
			}
			else if(tempolimit > 80) {
				wunschgeschwindigkeit = Physics.normalverteilung(90, 5);
			}
			else if(tempolimit > 60) {
				wunschgeschwindigkeit = Physics.normalverteilung(85, 4);
			}
			else if(tempolimit > 50) {
				wunschgeschwindigkeit = Physics.normalverteilung(75, 3);
			}
			else {
				wunschgeschwindigkeit = Physics.normalverteilung(50, 6);
			}
		}
		//Konvertierung von km/h zu m/s
		wunschgeschwindigkeit = wunschgeschwindigkeit / 3.6;
		return wunschgeschwindigkeit;
	}
}

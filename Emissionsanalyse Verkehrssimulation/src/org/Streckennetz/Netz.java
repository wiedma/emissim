package org.Streckennetz;
import org.Verkehr.Fahrzeug;
import java.util.ArrayList;
/**Das Eigentliche Streckennetz, welches aus Fahrspuren besteht*/
public class Netz {
	
	/**Die Spuren aus denen das Netz besteht*/
	private Fahrspur[] spuren;
	/**Der Kürzeste Pfad von jeder Quelle zu jeder Senke*/
	private Strecke[] strecken;
	/**Alle Fahrzeuge, die sich im Netz befinden (Anzahl ändert sich at runtime)*/
	private ArrayList<Fahrzeug> fahrzeuge;
	/**Das Tempolimit*/
	private double tempolimit;
	
	public Netz(Fahrspur[] spuren) {
		this.spuren = spuren;
		strecken = Routenplaner.planeRouten(this);
		this.fahrzeuge = new ArrayList<Fahrzeug>();
	}
	
	/**Fügt eine Fahrspur dem Netz hinzu
	 * @param fahrzeug Das Fahrzeug welches hinzugefügt weden soll
	 */
	public void fahrzeugHinzufuegen(Fahrzeug fahrzeug) {
		this.fahrzeuge.add(fahrzeug);
	}
	
	/**Entfernt ein Fahrzeug aus dem Streckennetz
	 * @param fahrzeug Das Fahrzeug welches entfernt werden soll
	 */
	public void fahrzeugEntfernen(Fahrzeug fahrzeug) {
		this.fahrzeuge.remove(fahrzeug);
	}
	
	/**Gibt die Anzahl aller Fahrzuege im Netz
	 * @return Die Anzahl der Fahrzeuge, die sich im Netz befinden
	 */
	public int anzahlFahrzeuge() {
		return fahrzeuge.size();
	}
	
	public Fahrspur[] spurenGeben(){
		return spuren;
	}
	
	/**Gibt das Signal der Simulation an alle Fahrspuren im Netz weiter*/
	public void zeitschritt() {
		for(Fahrzeug f : fahrzeuge) {
			f.alleHindernisseSuchen();
		}
		for(Fahrzeug f : fahrzeuge) {
			f.beschleunigungBestimmen();
		}
		for(Fahrzeug f : fahrzeuge) {
			f.spurwechselBestimmen();
		}
		for(int i = fahrzeuge.size() - 1; i >= 0; i--) {
			fahrzeuge.get(i).zeitschritt();
		}
		for(Fahrspur spur : spuren) {
			spur.zeitschritt();
		}
	}
	
	public void tempolimitSetzen(double tempolimit) {
		this.tempolimit = tempolimit;
		for(Fahrspur spur : spuren) {
			spur.maxGeschwindigkeitSetzen(tempolimit);
		}
	}
	
	public double tempolimitGeben() {
		return this.tempolimit;
	}
	
	public double gesamtVerkehrsstaerke() {
		double gesamt = 0;
		for(Strecke s : strecken) {
			gesamt += s.verkehrsstaerkeGeben();
		}
		return gesamt;
	}
	
	public void gesamtVerkehrsstaerkeSetzen(double verkehrsstaerke) {
		double q = verkehrsstaerke/(strecken.length);
		for(Strecke s : strecken) {
			s.verkehrsstaerkeSetzen(q);
		}
	}
	
	public int gesamtFahrzeugeErzeugt() {
		int summe = 0;
		for(Fahrspur f : spuren) {
			if(f instanceof Quelle) {
				summe += ((Quelle) f).fahrzeugeErzeugt();
			}
		}
		return summe;
	}
	
	public void quellenDektivieren() {
		for(Fahrspur f : spuren) {
			if(f instanceof Quelle) {
				((Quelle) f).aktivSetzen(false);
			}
		}
	}
	
	public void reset() {
		this.fahrzeuge = new ArrayList<Fahrzeug>();
		for(Fahrspur f : spuren) {
			f.reset();
		}
		
	}
	

}

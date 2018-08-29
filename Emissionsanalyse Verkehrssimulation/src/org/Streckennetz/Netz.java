package org.Streckennetz;
import org.Verkehr.Fahrzeug;
import java.util.ArrayList;
/**Das Eigentliche Streckennetz, welches aus Fahrspuren besteht*/
public class Netz {
	
	/**Die Spuren aus denen das Netz besteht*/
	private Fahrspur[] spuren;
	@SuppressWarnings("unused")
	/**Der K�rzeste Pfad von jeder Quelle zu jeder Senke*/
	private Strecke[] strecken;
	/**Alle Fahrzeuge, die sich im Netz befinden (Anzahl �ndert sich at runtime)*/
	private ArrayList<Fahrzeug> fahrzeuge;
	
	public Netz(Fahrspur[] spuren) {
		this.spuren = spuren;
		strecken = Routenplaner.planeRouten(this);
		this.fahrzeuge = new ArrayList<Fahrzeug>();
	}
	
	/**F�gt eine Fahrspur dem Netz hinzu
	 * @param fahrzeug Das Fahrzeug welches hinzugef�gt weden soll
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
		for(int i = fahrzeuge.size() - 1; i >= 0; i--) {
			fahrzeuge.get(i).zeitschritt();
		}
		for(Fahrspur spur : spuren) {
			spur.zeitschritt();
		}
	}
	
	public void tempolimitSetzen(double tempolimit) {
		for(Fahrspur spur : spuren) {
			spur.maxGeschwindigkeitSetzen(tempolimit);
		}
	}
	
//	public void hindernisseBestimmen() {
//		for(Fahrzeug f : fahrzeuge) {
//			f.alleHindernisseSuchen();
//		}
//	}
	

}

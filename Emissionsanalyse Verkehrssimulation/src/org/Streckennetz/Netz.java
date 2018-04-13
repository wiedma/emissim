package org.Streckennetz;

import org.Verkehr.Fahrzeug;
import java.util.ArrayList;

public class Netz {
	
	//Die Spuren aus denen das Netz besteht
	private Fahrspur[] spuren;
	//Der K�rzeste Pfad von jeder Quelle zu jeder Senke
	private Strecke[] strecken;
	//Alle Fahrzeuge, die sich im Netz befinden (Anzahl �ndert sich @runtime)
	private ArrayList<Fahrzeug> fahrzeuge;
	
	public Netz(Fahrspur[] spuren) {
		this.spuren = spuren;
		strecken = Routenplaner.planeRouten(spuren);
		this.fahrzeuge = new ArrayList<Fahrzeug>();
	}
	
	public void fahrzeugHinzufuegen(Fahrzeug fahrzeug) {
		this.fahrzeuge.add(fahrzeug);
	}
	
	public void fahrzeugEntfernen(Fahrzeug fahrzeug) {
		this.fahrzeuge.remove(fahrzeug);
	}
	
	public Fahrspur[] spurenGeben(){
		return spuren;
	}
	
	public void zeitschritt() {
		//TODO in zeitschritt() durch alle Fahrzeuge iterieren und Verhalten berechnen
	}
	

}

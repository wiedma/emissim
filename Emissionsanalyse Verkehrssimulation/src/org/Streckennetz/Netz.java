package org.Streckennetz;

import org.Verkehr.Fahrzeug;
import java.util.ArrayList;

public class Netz {
	
	//Die Spuren aus denen das Netz besteht
	private Fahrspur[] spuren;
	//Der Kürzeste Pfad von jeder Quelle zu jeder Senke
	private Strecke[] strecken;
	//Alle Fahrzeuge, die sich im Netz befinden (Anzahl ändert sich @runtime)
	private ArrayList<Fahrzeug> fahrzeuge;
	
	public Netz(Fahrspur[] spuren) {
		this.spuren = spuren;
		strecken = Routenplaner.planeRouten(spuren);
		this.fahrzeuge = new ArrayList<Fahrzeug>();
	}
	
	public void addFahrzeug(Fahrzeug fahr) {
		this.fahrzeuge.add(fahr);
	}
	
	public void removeFahrzeug(Fahrzeug fahr) {
		this.fahrzeuge.remove(fahr);
	}
	
	public void zeitschritt() {
		//TODO in zeitschritt() durch alle Fahrzeuge iterieren und Verhalten berechnen
	}
	

}

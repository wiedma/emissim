package org.Streckennetz;
import java.util.ArrayList;

public class Routenplaner {
	
	//TODO planeRouten() mit Dijkstra-Algorithmus implementieren
	public static Strecke[] planeRouten(Netz netz) {
		//Array aller Fahrspuren im Netz
		Fahrspur[] spuren = netz.spurenGeben();
		
		//Listen mit allen Quellen und Senken im Netz
		ArrayList<Quelle> quellen = new ArrayList<Quelle>();
		ArrayList<Senke> senken = new ArrayList<Senke>();
		
		//Für jede Fahrspur im spuren-Array
		for(Fahrspur spur : spuren){
			//Wenn die Fahrspur eine Quelle ist
			if(spur.getClass().equals(Quelle.class)){
				//Füge sie zu den Quellen hinzu
				quellen.add((Quelle)spur);
			}
			//Wenn die Fahrspur eine Senke ist
			if(spur.getClass().equals(Senke.class)){
				//Füge sie den Senken hinzu
				senken.add((Senke)spur);
			}
		}
		
		//Für jede Quelle aus der quellen-Liste
		for(Quelle quelle : quellen){
			//TODO Führe den Dijkstra-Algorithmus durch
		}
		return null;
	}
	

}

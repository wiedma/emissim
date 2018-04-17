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
		
		//F�r jede Fahrspur im spuren-Array
		for(Fahrspur spur : spuren){
			//Wenn die Fahrspur eine Quelle ist
			if(spur.getClass().equals(Quelle.class)){
				//F�ge sie zu den Quellen hinzu
				quellen.add((Quelle)spur);
			}
			//Wenn die Fahrspur eine Senke ist
			if(spur.getClass().equals(Senke.class)){
				//F�ge sie den Senken hinzu
				senken.add((Senke)spur);
			}
		}
		
		//F�r jede Quelle aus der quellen-Liste
		for(Quelle quelle : quellen){
			//TODO F�hre den Dijkstra-Algorithmus durch
		}
		return null;
	}
	

}

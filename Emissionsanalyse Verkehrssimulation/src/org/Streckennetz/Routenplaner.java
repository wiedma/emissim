package org.Streckennetz;
import java.util.ArrayList;
import org.Graphen.*;

public class Routenplaner {
	
	public static Strecke[] planeRouten(Netz netz) {
		//Array aller Fahrspuren im Netz
		Fahrspur[] spuren = netz.spurenGeben();
		
		ArrayList<Strecke> strecken = new ArrayList<Strecke>();
		
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
		
		
		//Graph generieren
		Graph graph = new Graph();
		
		//Mithilfe der Tiefensuche das Straßennetz im Graphen eintragen
		spuren[0].eintragen(graph);
		
		//Für jede Quelle aus der quellen-Liste
		for(Quelle quelle : quellen){
			for(Senke senke : senken) {
				//Führe den Dijkstra-Algorithmus durch
				Datenelement[] datenelemente = graph.dijkstra(quelle.knoten, senke.knoten);
				//Caste das Ergebnis zurück in Fahrspuren und generiere daraus die Strecken
				Fahrspur[] strecke = new Fahrspur[datenelemente.length];
				for(int i = 0; i < datenelemente.length; i++) {
					if(datenelemente[i] instanceof Fahrspur) {
						strecke[i] = (Fahrspur) datenelemente[i];
					}
				}
				//Erzeuge das Strecken-Objekt
				Strecke Strecke = new Strecke(strecke, netz);
				//Speichere die Strecke im Array ab
				strecken.add(Strecke);
				//Füge die Strecke der Quelle hinzu
				quelle.streckeHinzufuegen(Strecke);
			}
		}
		return strecken.toArray(new Strecke[0]);
	}
	
}

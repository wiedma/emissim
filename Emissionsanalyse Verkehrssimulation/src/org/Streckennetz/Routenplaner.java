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
		inGraphenEintragen(spuren[0], graph);
		
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
				strecken.add(new Strecke(strecke, netz));
			}
		}
		return strecken.toArray(new Strecke[0]);
	}
	
	//Trägt ein zusammenhängendes Streckennetz mithilfe des Tiefensuche-Algorithmus in einen Graphen
	//ein. Begonnen wird bei der übergebenen Fahrspur-Referenz
	private static void inGraphenEintragen(Fahrspur spur, Graph graph) {
		//Überpfrüfe, dass kein Argument null ist
		if(spur == null || graph == null) {
			return;
		}
		
		//Erzeuge neuen Knoten
		Knoten knoten = new Knoten(spur);
		spur.knoten = knoten;
		
		//Füge den neuen Knoten dem Graphen hinzu
		graph.knotenHinzufuegen(knoten);
		
		//Führe Tiefensuche durch um alle Nachbarn einzutragen
		
		//Markiere diese Fahrspur als eingetragen
		spur.eintragen();
		
		//FIXME Bugfix!: Tiefensuche in das Datenelement kapseln um Quellen und Senken besser berücksichtigen zu können
		
		//Linker Nachbar
		if(spur.linkeFahrspur != null) {
			if(!spur.linkeFahrspur.istEingetragen()) {
				inGraphenEintragen(spur.linkeFahrspur, graph);
			}
			knoten.kanteHinzufuegen(new Kante(spur.linkeFahrspur.knotenGeben(),
					(spur.breite/2) + (spur.linkeFahrspur.breite/2)));
		}
		
		//Rechter Nachbar
		if(spur.rechteFahrspur != null) {
			if(!spur.rechteFahrspur.istEingetragen()) {
				inGraphenEintragen(spur.rechteFahrspur, graph);
			}
			knoten.kanteHinzufuegen(new Kante(spur.rechteFahrspur.knotenGeben(),
					(spur.breite/2) + (spur.rechteFahrspur.breite/2)));
		}
		
		//Vorderer Nachbar
		if(spur.naechsteFahrspur != null) {
			if(!spur.naechsteFahrspur.istEingetragen()) {
				inGraphenEintragen(spur.naechsteFahrspur, graph);
			}
			knoten.kanteHinzufuegen(new Kante(spur.naechsteFahrspur.knotenGeben(),
					(spur.laenge/2) + (spur.naechsteFahrspur.laenge/2)));
		}
		
		//Hinterer Nachbar (ist kein Nachbar im Graphen, da die Autos nicht rückwärts fahren sollen)
		if(spur.vorherigeFahrspur != null && !spur.vorherigeFahrspur.istEingetragen()) {
			inGraphenEintragen(spur.vorherigeFahrspur, graph);
		}
		
	}
}

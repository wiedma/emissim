package org.Graphen;
import java.util.ArrayList;
import java.util.PriorityQueue;
public class Graph {
	private ArrayList<Knoten> knoten;
	
	public Graph(){
		knoten = new ArrayList<Knoten>();
	}
	
	//Fügt dem Graphen einen neuen Knoten hinzu
	public void knotenHinzufuegen(Knoten neu){
		knoten.add(neu);
	}
	
	//Führt den Dijkstra-Algorithmus durch um den kürzesten Weg von start nach ziel zu suchen
	public Datenelement[] dijkstra(Knoten start, Knoten ziel){
		//Initialisierung der Prioritätswarteschlange
		PriorityQueue<Knoten> pq = new PriorityQueue<Knoten>();
		//Initialisierung der Startpfadgewichte
		for(Knoten knoten : knoten){			
			if(knoten.equals(start)){
				knoten.pfadgewichtSetzen(0);
			}
			else{
				knoten.pfadgewichtSetzen(Integer.MAX_VALUE);
			}
			//Einfügen jedes Knoten in die Prioritätswarteschlange
			pq.add(knoten);
			
		}
		
		//Solange die Prioritätswarteschlange nicht leer ist
		while(!pq.isEmpty()) {
			//Entnehme den vordersten Knoten (mit geringstem Pfadgewicht) aus der Prioritätswarteschlange
			Knoten zuBearbeiten = pq.poll();
			for(Knoten aktualisiert : zuBearbeiten.dijkstra()) {
				//Aktualisiere die Prioritätswarteschlange
				pq.remove(aktualisiert);
				pq.add(aktualisiert);
			}
		}
		
		//Verfolge den Pfad zurück bis zum Anfang
		ArrayList<Datenelement> pfad = new ArrayList<Datenelement>();
		Knoten pfadKnoten = ziel;
		pfad.add(ziel.datenGeben());
		while(!pfadKnoten.equals(start)) {
			pfad.add(0, pfadKnoten.vorgaengerKnotenGeben().datenGeben());
			pfadKnoten = pfadKnoten.vorgaengerKnotenGeben();
		}
		
		return pfad.toArray(new Datenelement[0]);
	}
	
	public ArrayList<Knoten> knotenGeben() {
		return knoten;
	}

}

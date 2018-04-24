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
	
	//TODO Dijkstra-Algorithmus implementieren
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
		return null;
	}
	

}

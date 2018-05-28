package org.Graphen;
import java.util.ArrayList;
import java.util.PriorityQueue;
public class Graph {
	private ArrayList<Knoten> knoten;
	
	public Graph(){
		knoten = new ArrayList<Knoten>();
	}
	
	//F�gt dem Graphen einen neuen Knoten hinzu
	public void knotenHinzufuegen(Knoten neu){
		knoten.add(neu);
	}
	
	//F�hrt den Dijkstra-Algorithmus durch um den k�rzesten Weg von start nach ziel zu suchen
	public Datenelement[] dijkstra(Knoten start, Knoten ziel){
		//Initialisierung der Priorit�tswarteschlange
		PriorityQueue<Knoten> pq = new PriorityQueue<Knoten>();
		//Initialisierung der Startpfadgewichte
		for(Knoten knoten : knoten){			
			if(knoten.equals(start)){
				knoten.pfadgewichtSetzen(0);
			}
			else{
				knoten.pfadgewichtSetzen(Integer.MAX_VALUE);
			}
			//Einf�gen jedes Knoten in die Priorit�tswarteschlange
			pq.add(knoten);
			
		}
		
		//Solange die Priorit�tswarteschlange nicht leer ist
		while(!pq.isEmpty()) {
			//Entnehme den vordersten Knoten (mit geringstem Pfadgewicht) aus der Priorit�tswarteschlange
			Knoten zuBearbeiten = pq.poll();
			for(Knoten aktualisiert : zuBearbeiten.dijkstra()) {
				//Aktualisiere die Priorit�tswarteschlange
				pq.remove(aktualisiert);
				pq.add(aktualisiert);
			}
		}
		
		//Verfolge den Pfad zur�ck bis zum Anfang
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

package org.Graphen;
import java.util.ArrayList;
import java.util.PriorityQueue;
/**Implementierung der Datenstruktur Graph mit Adjazenzlisten*/
public class Graph {
	/**Die Liste aller Knoten dieses Graphen*/
	private ArrayList<Knoten> knoten;
	
	/**Konstruktor f�r einen leeren Graphen*/
	public Graph(){
		knoten = new ArrayList<Knoten>();
	}
	
	/**F�gt dem Graphen einen neuen Knoten hinzu*/
	public void knotenHinzufuegen(Knoten neu){
		knoten.add(neu);
	}
	
	/**
	 * F�hrt den Dijkstra-Algorithmus durch um den k�rzesten Weg von start nach ziel zu suchen
	 *@param start Der Knoten von dem die Suche ausgeht
	 *@param ziel Der Knoten, der erreicht werden m�chte
	 *@return Der Pfad von Start nach Ziel als Array von Datenelementen
	*/
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
	
	/**Getter f�r die Knoten-Liste*/
	public ArrayList<Knoten> knotenGeben() {
		return knoten;
	}

}

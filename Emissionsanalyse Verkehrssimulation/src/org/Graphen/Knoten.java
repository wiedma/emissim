package org.Graphen;
import java.util.ArrayList;
import java.util.Comparator;

public class Knoten implements Comparator<Knoten> {
	//Datensätze implementieren das Interface Datenelement
	private Datenelement daten;
	//Alle Kanten, die von diesem Knoten ausgehen
	private ArrayList<Kante> kanten;
	//Pfadgewicht für den Dijkstra-Algorithmus
	private double pfadgewicht;
	//Vorgängerknoten für den Dijkstra-Algorithmus
	private Knoten vorgaenger;
	
	public Knoten(Datenelement daten){
		this.daten = daten;
		kanten = new ArrayList<Kante>();
	}
	
	//Diesem Knoten eine Kante hinzufügen
	public void kanteHinzufuegen(Kante neu){
		kanten.add(neu);
	}
	
	//Verbinde diesen Knoten mit einem Anderen
	public void verbinde(Knoten nachbar, double gewicht){
		kanten.add(new Kante(nachbar, gewicht));
	}
	
	//Verbinde diesen Knoten mit einem Anderen und umgekehrt
	public void verbindeGegenseitig(Knoten nachbar, double gewicht){
		kanten.add(new Kante(nachbar, gewicht));
		nachbar.verbinde(this, gewicht);
	}
	
	//Getter und Setter für das Pfadgewicht
	public void pfadgewichtSetzen(int pfadgewicht){
		this.pfadgewicht = pfadgewicht;
	}
	
	public double pfadgewichtGeben(){
		return pfadgewicht;
	}
	
	public void vorgaengerKnotenSetzen(Knoten vorgaenger){
		this.vorgaenger = vorgaenger;
	}

	@Override
	//Comparator, der zum Sortieren der Prioritätswarteschlange im Dijkstra-Algorithmus genutzt wird
	public int compare(Knoten k1, Knoten k2) {
		return Double.compare(k1.pfadgewicht, k2.pfadgewicht);
	}


}

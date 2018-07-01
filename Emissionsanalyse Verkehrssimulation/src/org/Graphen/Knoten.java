package org.Graphen;
import java.util.ArrayList;
import java.util.Comparator;

/**Implementierung eines Knoten in der Datenstruktur Graph*/
public class Knoten implements Comparator<Knoten>, Comparable<Knoten> {
	/**Datensätze, die vom Graphen verwaltet werden*/
	private Datenelement daten;
	/**Alle Kanten, die von diesem Knoten ausgehen*/
	private ArrayList<Kante> kanten;
	/**Pfadgewicht für den Dijkstra-Algorithmus*/
	private double pfadgewicht;
	/**Vorgängerknoten für den Dijkstra-Algorithmus*/
	private Knoten vorgaenger;
	
	/**
	 * Erzeugt einen Knoten, welcher ein Datenelement trägt
	 * @param daten Das Datenelement, das von diesem Knoten getragen werden soll
	 */
	public Knoten(Datenelement daten){
		this.daten = daten;
		kanten = new ArrayList<Kante>();
	}
	
	/**Diesem Knoten eine Kante hinzufügen*/
	public void kanteHinzufuegen(Kante neu){
		kanten.add(neu);
	}
	
	/**Verbinde diesen Knoten mit einem Anderen*/
	public void verbinde(Knoten nachbar, double gewicht){
		kanten.add(new Kante(nachbar, gewicht));
	}
	
	/**Verbinde diesen Knoten mit einem Anderen und umgekehrt*/
	public void verbindeGegenseitig(Knoten nachbar, double gewicht){
		kanten.add(new Kante(nachbar, gewicht));
		nachbar.verbinde(this, gewicht);
	}
	
	/**Setter für das Pfadgewicht*/
	public void pfadgewichtSetzen(double pfadgewicht){
		this.pfadgewicht = pfadgewicht;
	}
	
	/**Getter für das Pfadgewicht*/
	public double pfadgewichtGeben(){
		return pfadgewicht;
	}
	
	/**Getter für das Datenelement*/
	public Datenelement datenGeben() {
		return daten;
	}
	
	/**Setter für den Vorgängerknoten (Dijkstra)*/
	public void vorgaengerKnotenSetzen(Knoten vorgaenger){
		this.vorgaenger = vorgaenger;
	}
	
	/**Getter für den Vorgängerknoten (Dijkstra)*/
	public Knoten vorgaengerKnotenGeben(){
		return this.vorgaenger;
	}
	
	/**
	 * Aktualisiere alle Pfadgewichte und Bearbeitungsvorgänger dieses Knoten für den Dijkstra-Algorithmus
	 * @return Alle Knoten deren Pfadgewicht sich geändert hat
	 */
	public ArrayList<Knoten> dijkstra() {
		//ArrayList, welche alle Knoten enthält, deren Pfadgewichte aktualisiert wurden
		ArrayList<Knoten> aktualisiert = new ArrayList<Knoten>();
		//Für jeden Nachbarn
		for(Kante kante : kanten) {
			Knoten ziel = kante.zielGeben();
			//Wenn sein Pfadgewicht höher ist, als das Gewicht des Pfades über diesen Knoten
			if(ziel.pfadgewichtGeben() > (pfadgewicht + kante.gewichtGeben())) {
				//Aktualisiere sein Pfadgewicht
				ziel.pfadgewichtSetzen(pfadgewicht + kante.gewichtGeben());
				//Markiere diesen Knoten als Bearbeitungsvorgänger
				ziel.vorgaengerKnotenSetzen(this);
				//Füge den veränderten Knoten in die ArrayList ein
				aktualisiert.add(ziel);
			}
		}
		
		return aktualisiert;
	}

	@Override
	/**Comparator, der zum Sortieren der Prioritätswarteschlange im Dijkstra-Algorithmus genutzt wird*/
	public int compare(Knoten k1, Knoten k2) {
		return Double.compare(k1.pfadgewicht, k2.pfadgewicht);
	}

	@Override
	/**Comparator, der zum Sortieren der Prioritätswarteschlange im Dijkstra-Algorithmus genutzt wird*/
	public int compareTo(Knoten o) {
		return this.compare(this, o);
	}
	
	


}

package org.Graphen;

/**Die Datenelemente, die ein Knoten trägt*/
public interface Datenelement {
	/**Prüft ob dieses Datenelement bereits in einem Graphen eingetragen wurde*/
	public boolean istEingetragen();
	/**Trägt dieses Datenelement in den Graphen ein und startet eine Tiefensuche, um alle Nachbarn einzutragen*/
	public void eintragen(Graph graph);
	/**Gibt den Knoten dieses Datenelements*/
	public Knoten knotenGeben();
}

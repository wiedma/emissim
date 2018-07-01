package org.Graphen;
/**Eine Kante in der Datenstruktur Graph*/
public class Kante {
	
	/**Der Zielknoten dieser Kante*/
	private Knoten ziel;
	/**Die Gewichtung dieser Kante*/
	public double gewicht;
	
	/**
	 * Erzeugt eine gerichtete Kante zum Zielknoten mit gewicht
	 * @param ziel Der Zielknoten auf den diese Kante zeigt
	 * @param gewicht Die Gewichtung dieser Kante
	 */
	public Kante(Knoten ziel, double gewicht){
		this.ziel = ziel;
		this.gewicht = gewicht;
	}
	
	/**Setter f�r das Gewicht*/
	public void gewichtSetzen(double gewicht){
		this.gewicht = gewicht;
	}
	
	/**Getter f�r das Gewicht*/
	public double gewichtGeben(){
		return gewicht;
	}
	
	/**Getter f�r den Zielknoten*/
	public Knoten zielGeben() {
		return ziel;
	}

}

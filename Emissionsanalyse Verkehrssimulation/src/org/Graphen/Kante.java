package org.Graphen;

public class Kante {
	
	//Der Zielknoten dieser Kante
	private Knoten ziel;
	//Die Gewichtung dieser Kante
	public double gewicht;
	
	public Kante(Knoten ziel, double gewicht){
		this.ziel = ziel;
		this.gewicht = gewicht;
	}
	
	public void gewichtSetzen(double gewicht){
		this.gewicht = gewicht;
	}
	
	public double gewichtGeben(){
		return gewicht;
	}
	
	public Knoten zielGeben() {
		return ziel;
	}

}

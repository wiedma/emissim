package org.Streckennetz;
import java.util.ArrayList;

public class Mehrspurbereich {
	
	//Die Spuren, die diesen Mehrspurbereich definieren
	private ArrayList<Fahrspur> fahrspuren;
	
	//Konstruktor
	public Mehrspurbereich(Fahrspur ersteSpur) {
		fahrspuren = new ArrayList<Fahrspur>();
		fahrspuren.add(ersteSpur);
	}
	
	//Prüft, ob dieser Mehrspurbereich die angegebene Fahrspur bereits enthält
	public boolean enthaeltFahrspur(Fahrspur spur){
		return fahrspuren.contains(spur);
	}
	
	//TODO fahrspurHinzufuegen() noch so erweitern, dass fahrspuren auch in der Mitte hinzugefügt werden können
	//Fügt diesem Mehrspurbereich eine weitere Fahrspur hinzu
	//@param referenz Eine Fahrspur, die als Referenzpunkt innerhalb des Mehrspurbereichs dient um die Position festzustellen
	//@param links Gibt an, ob neueSpur links oder rechts von referenz verbunden werden soll
	public void fahrspurHinzufuegen(Fahrspur neueSpur, Fahrspur referenz, boolean links){
		//Referenzen neu setzen
		if(links){
			neueSpur.rechteFahrspur = referenz;
			referenz.linkeFahrspur = neueSpur;
			fahrspuren.add(fahrspuren.indexOf(referenz) + 1, neueSpur);
		}
		else{
			neueSpur.linkeFahrspur = referenz;
			referenz.rechteFahrspur = neueSpur;
			fahrspuren.add(fahrspuren.indexOf(referenz), neueSpur);
		}
	}

}

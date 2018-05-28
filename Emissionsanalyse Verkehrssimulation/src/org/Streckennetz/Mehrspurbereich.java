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
	
	//Fügt diesem Mehrspurbereich eine weitere Fahrspur hinzu
	//@param referenz Eine Fahrspur, die als Referenzpunkt innerhalb des Mehrspurbereichs dient um die Position festzustellen
	//@param links Gibt an, ob neueSpur links oder rechts von referenz verbunden werden soll
	public void fahrspurHinzufuegen(Fahrspur neueSpur, Fahrspur referenz, boolean links){
		//Wenn links eingefügt werden soll
		if(links){
			//Neue Spur rechts mit referenz verbinden
			neueSpur.rechteFahrspur = referenz;
			//Den alten linken Nachbarn von referenz speichern
			Fahrspur altesLinks = referenz.linkeFahrspur;
			//Referenz links mit neueSpur verbinden
			referenz.linkeFahrspur = neueSpur;
			//Neue Spur links mit dem alten linken Nachbarn von referenz verbinden
			neueSpur.linkeFahrspur = altesLinks;
			//Wenn der alte linke Nachbar nicht null war
			if(altesLinks != null){
				//Verbinde den alten linken Nachbarn rechts mit neueSpur
				altesLinks.rechteFahrspur = neueSpur;
			}
			//Füge neueSpur an der korrekten Stelle in der Liste ein
			fahrspuren.add(fahrspuren.indexOf(referenz) + 1, neueSpur);
		}
		//Wenn rechts eingefügt werden soll
		else{
			//Verbinde die neueSpur links mit referenz
			neueSpur.linkeFahrspur = referenz;
			//Den alten rechten Nachbarn von referenz speichern
			Fahrspur altesRechts = referenz.rechteFahrspur;
			//Referenz rechts mit neueSpur verbinden
			referenz.rechteFahrspur = neueSpur;
			//neueSpur rechts mit dem alten rechten Nachbarn von referenz verbinden
			neueSpur.rechteFahrspur = altesRechts;
			//Wenn der alte Nachbar nicht null war
			if(altesRechts != null){
				//Verbinde den alten Nachbarn links mit neueSpur
				altesRechts.linkeFahrspur = neueSpur;
			}
			//Füge neueSpur an der korrekten Stelle in der Liste ein
			fahrspuren.add(fahrspuren.indexOf(referenz), neueSpur);
		}
		//Gib neueSpur die Referenz auf dich selbst
		neueSpur.mehrspurbereich = this;
	}
	
	//TODO fahrspurEntfernen() testen
	//Entfernt eine Fahrspur und räumt alle Referenzen auf
	public void fahrspurEntfernen(Fahrspur spur){
		fahrspuren.remove(spur);
		spur.mehrspurbereich = null;
		if(spur.linkeFahrspur != null){
			spur.linkeFahrspur.rechteFahrspur = spur.rechteFahrspur;
		}
		if(spur.rechteFahrspur != null){
			spur.rechteFahrspur.linkeFahrspur = spur.linkeFahrspur;
		}
		spur.linkeFahrspur = null;
		spur.rechteFahrspur = null;
	}

}

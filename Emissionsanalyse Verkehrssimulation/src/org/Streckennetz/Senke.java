package org.Streckennetz;

import org.Verkehr.Fahrzeug;
import org.Verkehr.Hindernis;
import org.main.Simulation;

/**Eine Fahrspur ohne r�umliche Ausdehnung, welche Fahrzeuge aus der Simulation entfernt*/
public class Senke extends Fahrspur{
	
	/**Der Zeitpunkt zu welchem das letzte Fahrzeug entfernt wurde*/
	private double letzteZeit;
	/**Die Geschwindigkeit des Fahrzeugs, welches als letztes entfernt wurde*/
	private double letzteGeschwindigkeit;
	/**Anzahl der entfernten Fahrzeuge*/
	private static int entfernteFahrzeuge = 0;
	/**Anzahl der Fahrzeuge, die entfernt werden m�ssen, bis der GarbageCollector benachrichtigt wird*/
	public static final int FAHRZEUGE_BIS_GARBAGE_COLLECTION = 1000;
	
	
	public Senke(){
		super(0,0,0,false);
		letzteZeit = 0;
		letzteGeschwindigkeit = 0;
	}
	
	/**Gibt die Gesamtanzahl aller an dieser Senke entfernten Fahrzeuge*/
	public static int anzahlFahrzeugeEntfernt() {
		return entfernteFahrzeuge;
	}
	
	/**Entfernt das Fahrzeug bei erreichen der Senke aus dem Streckennetz
	 * @param fahrzeug Das zu entfernende Fahrzeug*/
	@Override
	public void fahrzeugHinzufuegen(Fahrzeug fahrzeug) {
		//Speichere die Zeit des Entfernens und die Geschwindigkeit des Fahrzeugs
		letzteZeit = Simulation.zeitGeben();
		letzteGeschwindigkeit = fahrzeug.geschwindigkeitGeben();
		//Entferne das Fahrzeug aus dem Netz
		Simulation.fahrzeugEntfernen(fahrzeug);
		fahrzeug = null;
		//Ab einer gewissen Anzahl von entfernten Fahrzeugen, wird der GarbageCollector
		//benachrichtigt den Arbeitsspeicher freizugeben
		entfernteFahrzeuge++;
		if(entfernteFahrzeuge % FAHRZEUGE_BIS_GARBAGE_COLLECTION == 0) {
			System.gc();
		}
	}
	
	
	/**Senken gaukeln den Fahrzeugen vor das entfernte Fahrzeug w�rde noch mit konstanter Geschwindigkeit weiterfahren*/
	@Override
	public Hindernis hindernisVorne(Fahrzeug sucher, double entfernung) {
		//Jetzige Position des letzten entfernten Fahrzeuges auf der gedachten Fahrspur
		//nach der Senke
		double geisterPos = letzteGeschwindigkeit * (Simulation.zeitGeben() - letzteZeit);
		//Wenn das Fahrzeug noch sichtbar w�re
		if(geisterPos + entfernung <= 1000) {
			//Gebe ein Hindernis zur�ck
			return new Hindernis(geisterPos + entfernung, letzteGeschwindigkeit, this, sucher,
					true, false);
		}
		//Sonst
		else {
			//Breche die Suche ab
			return null;
		}
	}
	
	@Override
	public Hindernis hindernisHinten(Fahrzeug sucher, double entfernung) {
		return null;
	}
	
	@Override
	public Hindernis hindernisLinks(Fahrzeug sucher, double entfernung, boolean vorne) {
		return null;
	}
	
	@Override
	public Hindernis hindernisRechts(Fahrzeug sucher, double entfernung, boolean vorne) {
		return null;
	}
	
	@Override
	/**Senken m�ssen beim Zeitschritt selbst nichts machen*/
	public void zeitschritt() {
		//Nichts tun
	}
}

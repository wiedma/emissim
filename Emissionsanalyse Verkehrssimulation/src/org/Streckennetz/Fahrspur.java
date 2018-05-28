package org.Streckennetz;
import org.Graphen.*;
import org.Verkehr.Fahrzeug;
import java.util.ArrayList;

public abstract class Fahrspur implements Datenelement {
	
	//Gibt an, ob Fahrzeuge auf dieser Spur �berholen d�rfen
	protected boolean ueberholverbot;
	
	//Gibt an, ob man diese Fahrspur befahren kann
	protected boolean befahrbar;
	
	//Das Tempolimit
	protected double maxGeschwindigkeit;
	
	//R�umliche Ausdehnung, breite eigentlich nur f�r GUI relevant
	protected double laenge, breite;
	
	//Die Nachbarn dieser Spur
	protected Fahrspur naechsteFahrspur;
	protected Fahrspur vorherigeFahrspur;
	protected Fahrspur linkeFahrspur;
	protected Fahrspur rechteFahrspur;
	
	//Der Mehrspurbereich, dem diese Fahrspur angeh�rt
	protected Mehrspurbereich mehrspurbereich;
	
	//Die Fahrzeuge auf dieser Fahrspur
	protected ArrayList<Fahrzeug> fahrzeuge;
	
	//Speichert, ob diese Fahrspur bereits in den Graphen zur Routenplanung eingetragen ist
	protected boolean eingetragen;
	
	//Der Knoten der diese Fahrspur im Graphen repr�sentiert
	protected Knoten knoten;
	
	//Konstruktor
	public Fahrspur(double laenge, double breite, double maxGeschwindigkeit, boolean ueberholverbot) {
		this.laenge = laenge;
		this.breite = breite;
		this.maxGeschwindigkeit = maxGeschwindigkeit;
		this.ueberholverbot = ueberholverbot;
	}
	
//------------------------------------------------------------------------------------------------
	
	//Getter und Setter
	public void ueberholverbotSetzen(boolean ueberholverbot) {
		this.ueberholverbot = ueberholverbot;
	}
	
	public boolean ueberholverbotGeben() {
		return ueberholverbot;
	}
	
	public void maxGeschwindigkeitSetzen(double maxGeschwindigkeit) {
		this.maxGeschwindigkeit = maxGeschwindigkeit;
	}
	
	public double maxGeschwindigkeitGeben() {
		return this.maxGeschwindigkeit;
	}
	
	public Fahrspur naechsteFahrspurGeben() {
		return this.naechsteFahrspur;
	}
	
	public double laengeGeben() {
		return laenge;
	}
	
//-------------------------------------------------------------------------------------------------
	
	//Fahrzeuge der Spur hinzuf�gen und diese wieder entfernen
	public void fahrzeugHinzufuegen(Fahrzeug fahrzeug) {
		fahrzeuge.add(fahrzeug);
	}
	
	public void fahrzeugEntfernen(Fahrzeug fahrzeug) {
		fahrzeuge.remove(fahrzeug);
	}
	
	//Verbinde die Spuren f1 und f2 (f2 ist der Nachfolger von f1)
	public static void verbinde(Fahrspur f1, Fahrspur f2) {
		//Pr�fe, ob f1 bereits verbunden ist
		boolean verbunden = f1.naechsteFahrspur != null;
		
		//Wenn f1 bereits verbunden war
		if(verbunden) {
			//Setze den Vorg�nger von f1s Nachfolger auf null
			f1.naechsteFahrspur.vorherigeFahrspur = null;
		}
		
		//Verbinde die beiden Spuren
		f1.naechsteFahrspur = f2;
		f2.vorherigeFahrspur = f1;
	}
	
	//�bergibt das Fahrzeug an die nachfolgende Fahrspur
	protected void uebergebeFahrzeug(Fahrzeug fahrzeug) {
		//Referenzen des Fahrzeugs neu setzen
		fahrzeug.posSetzen(fahrzeug.posGeben() - laenge);
		fahrzeug.spurSetzen(naechsteFahrspur);
		
		//Referenzen der Spuren neu setzen
		naechsteFahrspur.fahrzeugHinzufuegen(fahrzeug);
		this.fahrzeugEntfernen(fahrzeug);
	}
	
	//Pr�ft, ob diese Fahrspur ein "Nachbar" (im Mehrspurbereich) des Argumentes ist
	public boolean istBenachbart(Fahrspur spur){
		try{
			return mehrspurbereich.enthaeltFahrspur(spur);
		} catch(NullPointerException e){
			return false;
		}
	}
	
	//Markiert diese Fahrspur als in den Graphen eingetragen
	public void eintragen() {
		eingetragen = true;
	}
	
	//Pr�ft, ob diese Fahrspur bereits in den Graphen eingetragen ist
	public boolean istEingetragen() {
		return eingetragen;
	}
	
	//Gibt den Knoten, der diese Fahrspur im Graphen repr�sentiert
	public Knoten knotenGeben() {
		return this.knoten;
	}
}

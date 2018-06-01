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
	
	/*Die Verkehrsst�rke dieser Fahrspur berechnet sich als Summe der Verkehrsst�rken aller
	 * Strecken, die diese Fahrspur beinhalten. Zugewiesen wird dieser Wert nicht bei erstellung
	 * der Fahrspur, sondern nach Generierung der Strecken. Der Wert soll noch w�hrend der
	 * Laufzeit ver�nderbar sein*/
	protected double verkehrsstaerke;
	
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
	
	public boolean istEingetragen() {
		return eingetragen;
	}
	
	public Knoten knotenGeben() {
		return this.knoten;
	}
	
	public double verkehrsstaerkeGeben() {
		return verkehrsstaerke;
	}
	
	/*�ndert die Verkehrsst�rke um den gegebenen Wert. Diese Methode wird von den Strecken
	 * genutzt, um die Verkehrsst�rken der Fahrspuren zu berechnen, ohne Zugriff auf die Verkehrs-
	 * st�rken der anderen Strecken zu besitzen.
	 * Au�erdem erlaubt dieser Ansatz eine leichtere �nderung der Verkehrsst�rken in der Laufzeit*/
	public void verkehrsstaerkeAendern(double aenderung) {
		verkehrsstaerke = verkehrsstaerke += aenderung;
	}
	
//-------------------------------------------------------------------------------------------------
	
	//Fahrzeuge der Spur hinzuf�gen und diese wieder entfernen
	//FIXME Pr�fe, ob sich das Fahrzeug noch auf dieser Spur befindet
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
		
		//Wenn f2 keine Senke war
		if(!(f2 instanceof Senke)) {
			//Pr�fe, ob f2 bereits verbunden ist
			verbunden = f2.vorherigeFahrspur != null;
			
			//Wenn f2 bereits verbunden war
			if(verbunden) {
				//Setze den Nachfolger von f2s Vorg�nger auf null
				f2.vorherigeFahrspur.naechsteFahrspur = null;
			}
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
	
	/*Markiert diese Fahrspur als in den Graphen eingetragen und ruft die Methode bei allen
	 * unmarkierten Nachbarn auf (Tiefensuche)
	 */
	public void eintragen(Graph graph) {
		//Stelle sicher, dass kein Argument null ist
		if(graph == null) {
			return;
		}
		
		//Erzeuge neuen Knoten
		Knoten knoten = new Knoten(this);
		this.knoten = knoten;
		
		//F�ge den neuen Knoten dem Graphen hinzu
		graph.knotenHinzufuegen(knoten);
		
		//Markiere diese Fahrspur als eingetragen
		eingetragen = true;
		
		//Linker Nachbar
		if(linkeFahrspur != null) {
			if(!linkeFahrspur.istEingetragen()) {
				linkeFahrspur.eintragen(graph);
			}
			knoten.kanteHinzufuegen(new Kante(linkeFahrspur.knotenGeben(),
					(breite/2) + (linkeFahrspur.breite/2)));
		}
		
		//Rechter Nachbar
		if(rechteFahrspur != null) {
			if(!rechteFahrspur.istEingetragen()) {
				rechteFahrspur.eintragen(graph);
			}
			knoten.kanteHinzufuegen(new Kante(rechteFahrspur.knotenGeben(),
					(breite/2) + (rechteFahrspur.breite/2)));
		}
		
		//Vorderer Nachbar
		if(naechsteFahrspur != null) {
			if(!naechsteFahrspur.istEingetragen()) {
				naechsteFahrspur.eintragen(graph);
			}
			knoten.kanteHinzufuegen(new Kante(naechsteFahrspur.knotenGeben(),
					(laenge/2) + (naechsteFahrspur.laenge/2)));
		}
		
		//Hinterer Nachbar (ist kein Nachbar im Graphen, da die Autos nicht r�ckw�rts fahren sollen)
		if(vorherigeFahrspur != null && !vorherigeFahrspur.istEingetragen()) {
			vorherigeFahrspur.eintragen(graph);
		}
				
		
	}
	
}

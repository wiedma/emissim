package org.Streckennetz;
import org.Graphen.*;
import org.Verkehr.DummyFahrzeug;
import org.Verkehr.Fahrzeug;
import org.Verkehr.Hindernis;
import org.Verkehr.HindernisRichtung;
import java.util.ArrayList;
/**
 * Die Fahrspur stellt die kleinste Einheit im Streckennetz dar.
 * Dieses wird nach dem Baukastenprinzip aus den Einzelteilen zusammengebaut
 */
public abstract class Fahrspur implements Datenelement {
	
	/**Gibt an, ob Fahrzeuge auf dieser Spur überholen dürfen*/
	protected boolean ueberholverbot;
	
	/**Gibt an, ob man diese Fahrspur befahren kann*/
	protected boolean befahrbar;
	
	/**Das Tempolimit in km/h*/
	protected double maxGeschwindigkeit;
	
	/**Räumliche Ausdehnung*/
	protected double laenge, breite;
	
	/**Die Nachbarn dieser Spur*/
	protected Fahrspur naechsteFahrspur, vorherigeFahrspur, linkeFahrspur, rechteFahrspur;
	
	/**Der Mehrspurbereich, dem diese Fahrspur angehört*/
	protected Mehrspurbereich mehrspurbereich;
	
	/**Die Fahrzeuge auf dieser Fahrspur*/
	protected ArrayList<Fahrzeug> fahrzeuge;
	
	/**Die Verkehrsstärke dieser Fahrspur berechnet sich als Summe der Verkehrsstärken aller
	 * Strecken, die diese Fahrspur beinhalten. Zugewiesen wird dieser Wert nicht bei erstellung
	 * der Fahrspur, sondern nach Generierung der Strecken. Der Wert soll noch während der
	 * Laufzeit veränderbar sein*/
	protected double verkehrsstaerke;
	
	/**Speichert, ob diese Fahrspur bereits in den Graphen zur Routenplanung eingetragen ist*/
	protected boolean eingetragen;
	
	/**Der Knoten der diese Fahrspur im Graphen repräsentiert*/
	protected Knoten knoten;
	
	//Konstruktor
	public Fahrspur(double laenge, double breite, double maxGeschwindigkeit, boolean ueberholverbot) {
		this.laenge = laenge;
		this.breite = breite;
		this.maxGeschwindigkeit = maxGeschwindigkeit;
		this.ueberholverbot = ueberholverbot;
		this.fahrzeuge = new ArrayList<Fahrzeug>();
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
	
	@Override
	public boolean istEingetragen() {
		return eingetragen;
	}
	
	@Override
	public Knoten knotenGeben() {
		return this.knoten;
	}
	
	public double verkehrsstaerkeGeben() {
		return verkehrsstaerke;
	}
	
	/**Ändert die Verkehrsstärke um den gegebenen Wert. Diese Methode wird von den Strecken
	 * genutzt, um die Verkehrsstärken der Fahrspuren zu berechnen, ohne Zugriff auf die Verkehrs-
	 * stärken der anderen Strecken zu besitzen.
	 * Außerdem erlaubt dieser Ansatz eine leichtere Änderung der Verkehrsstärken in der Laufzeit*/
	public void verkehrsstaerkeAendern(double aenderung) {
		verkehrsstaerke = verkehrsstaerke += aenderung;
	}
	
//-------------------------------------------------------------------------------------------------
	
	/**Fahrzeuge der Spur hinzufügen*/
	public void fahrzeugHinzufuegen(Fahrzeug fahrzeug) {
		if(fahrzeug.posGeben() >= laenge) {
			fahrzeug.posSetzen(fahrzeug.posGeben() - laenge);
			uebergebeFahrzeug(fahrzeug);
		}
		
		fahrzeuge.add(fahrzeug);
	}
	
	/**Fahrzeuge aus der Spur entfernen*/
	public void fahrzeugEntfernen(Fahrzeug fahrzeug) {
		fahrzeuge.remove(fahrzeug);
	}
	
	/**Verbinde die Spuren f1 und f2 (f2 wird der Nachfolger von f1)*/
	public static void verbinde(Fahrspur f1, Fahrspur f2) {
		//Prüfe, ob f1 bereits verbunden ist
		boolean verbunden = f1.naechsteFahrspur != null;
		
		//Wenn f1 bereits verbunden war
		if(verbunden) {
			//Setze den Vorgänger von f1s Nachfolger auf null
			f1.naechsteFahrspur.vorherigeFahrspur = null;
		}
		
		//Wenn f2 keine Senke war
		if(!(f2 instanceof Senke)) {
			//Prüfe, ob f2 bereits verbunden ist
			verbunden = f2.vorherigeFahrspur != null;
			
			//Wenn f2 bereits verbunden war
			if(verbunden) {
				//Setze den Nachfolger von f2s Vorgänger auf null
				f2.vorherigeFahrspur.naechsteFahrspur = null;
			}
		}
		
		//Verbinde die beiden Spuren
		f1.naechsteFahrspur = f2;
		f2.vorherigeFahrspur = f1;
	}
	
	/**Übergibt das Fahrzeug an die nachfolgende Fahrspur*/
	protected void uebergebeFahrzeug(Fahrzeug fahrzeug) {
		//Referenzen des Fahrzeugs neu setzen
		fahrzeug.posSetzen(fahrzeug.posGeben() - laenge);
		fahrzeug.spurSetzen(naechsteFahrspur);
		
		//Referenzen der Spuren neu setzen
		naechsteFahrspur.fahrzeugHinzufuegen(fahrzeug);
		this.fahrzeugEntfernen(fahrzeug);
	}
	
	/**Prüft, ob diese Fahrspur ein "Nachbar" (im Mehrspurbereich) des Argumentes ist
	 * @param spur Die Spur auf welche geprüft werden soll*/
	public boolean istBenachbart(Fahrspur spur){
		try{
			return mehrspurbereich.enthaeltFahrspur(spur);
		} catch(NullPointerException e){
			return false;
		}
	}
	
	@Override
	/**Markiert diese Fahrspur als in den Graphen eingetragen und ruft die Methode bei allen
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
		
		//Füge den neuen Knoten dem Graphen hinzu
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
		
		//Hinterer Nachbar (ist kein Nachbar im Graphen, da die Autos nicht rückwärts fahren sollen)
		if(vorherigeFahrspur != null && !vorherigeFahrspur.istEingetragen()) {
			vorherigeFahrspur.eintragen(graph);
		}
	}
	
	/**Berechne die neue Position dieses Fahrzeugs nach dem Zeitschritt
	 * Berechnet auch die Emissionen*/
	public void zeitschritt() {
		//Itereriere rückwärts um ConcurrentModificationException zu vermeiden
		for(int i = fahrzeuge.size() - 1; i >= 0; i--) {
			Fahrzeug fahrzeug = fahrzeuge.get(i);
			fahrzeug.zeitschritt();
			//Streckenende überschritten
			if(fahrzeug.posGeben() >= laenge) {
				uebergebeFahrzeug(fahrzeug);
			}
		}
	}
	
//Hinderniserkennung -----------------------------------------------------------------------------
	/*TODO Hinderniserkennung komplett überarbeiten. Mehr mit Variablen arbeiten, Code reduzieren
	 * und Entfernungsberechnung auf Mittelpunkte beziehen
	 */
	/**Suche nach dem ersten Hindernis vor dem Fahrzeug in der Entfernung von 1km
	*@param entfernung Die von der vorherigen Spur bereits abgesuchte Entfernung
	*@param sucher Das Fahrzeug, welches die Hindernissuche durchführt*/
	public Hindernis hindernisVorne(Fahrzeug sucher, double entfernung) {
		//Wenn sich das suchende Fahrzeug auf dieser Spur befindet
		if(entfernung == 0) {
			//Suche nach dem Fahrzeug mit dem kleinsten positiven Abstand zum Sucher
			Fahrzeug naechster = null;
			double abstandNaechster = laenge - sucher.posGeben() - (sucher.laengeGeben()/2.0);
			for(int i = 0; i < fahrzeuge.size(); i++) {
				Fahrzeug momentan = fahrzeuge.get(i);
				//Nettoabstand der beiden Fahrzeuge
				double abstand = (momentan.posGeben() - sucher.posGeben()) - 
						(momentan.laengeGeben()/2.0 + sucher.laengeGeben()/2.0);
				if(abstand > 0 && abstand < abstandNaechster && abstand <= 1000) {
					naechster = momentan;
					abstandNaechster = abstand;
				}
			}
			//Wenn ein Hindernis in Sichtweite (1km) gefunden wurde
			if(naechster != null && abstandNaechster <= 1000) {
				//Wenn der Sucher kein Dummy war
				if(!(sucher instanceof DummyFahrzeug)) {
					//Nutze Symmetrie aus um Laufzeit zu sparen
					Hindernis symmetrisch = new Hindernis(-abstandNaechster,
							sucher.geschwindigkeitGeben(), sucher, naechster, false, true);
					naechster.hindernisSetzen(symmetrisch, HindernisRichtung.HINTEN);
				}
				//Sonst
				else {
					/*Erstelle ein symmetrisches Hindernis, bei dem die Referenzen auf das
					/Original zeigen*/
					DummyFahrzeug dummy = (DummyFahrzeug) sucher;
					Hindernis symmetrisch = new Hindernis(-abstandNaechster,
							dummy.originalGeben().geschwindigkeitGeben(), dummy.originalGeben(), 
							naechster, false, true);
					//Prüfe, ob dieses Hindernis näher, als das jetzige Hindernis des Ziels ist
					//Wenn der Dummy links gesetzt wurde
					if(dummy.istLinks()) {
						//Wenn das Ziel noch kein Hindernis hatte
						if(naechster.hindernisGeben(HindernisRichtung.HINTEN_RECHTS) != null) {
							//Wenn die Entfernung zum neuen Hindernis kleiner wäre
							if(naechster.hindernisGeben(HindernisRichtung.HINTEN_RECHTS).entfernungGeben()
									< symmetrisch.entfernungGeben()) {
								//Nutze Symmetrie aus und setze das neue Hindernis
								naechster.hindernisSetzen(symmetrisch, HindernisRichtung.HINTEN_RECHTS);
							}
						}
						//Wenn das Ziel noch kein Hindernis hatte
						else {
							//Nutze Symmetrie aus und setze das Hindernis
							naechster.hindernisSetzen(symmetrisch, HindernisRichtung.HINTEN_RECHTS);
						}
					}
					//Wenn der Dummy rechts gesetzt wurde
					else {
						//Wenn das Ziel noch kein Hindernis hatte
						if(naechster.hindernisGeben(HindernisRichtung.HINTEN_LINKS) != null) {
							//Wenn die Entfernung zum neuen Hindernis kleiner wäre
							if(naechster.hindernisGeben(HindernisRichtung.HINTEN_LINKS).entfernungGeben()
									< symmetrisch.entfernungGeben()) {
								//Nutze Symmetrie aus und setze das neue Hindernis
								naechster.hindernisSetzen(symmetrisch, HindernisRichtung.HINTEN_LINKS);
							}
						}
						//Wenn das Ziel noch kein Hindernis hatte
						else {
							//Nutze Symmetrie aus und setze das Hindernis
							naechster.hindernisSetzen(symmetrisch, HindernisRichtung.HINTEN_LINKS);
						}
					}
				}
				//Gebe das Hindernis zurück
				return new Hindernis(abstandNaechster, naechster.geschwindigkeitGeben(),
						naechster, sucher, true, true);
			}
			//Wenn die gesuchte Entfernung größer, als 1km ist
			else if(laenge - sucher.posGeben() > 1000) {
				//Breche die Suche ab
				return null;
			}
			//Wenn kein Hindernis gefunden werden konnte
			else {
				//Führe die Suche auf der nächsten Spur fort
				return naechsteFahrspur.hindernisVorne(sucher,
						laenge - sucher.posGeben() - (sucher.laengeGeben()/2.0));
			}
		}
		//Wenn sich das suchende Fahrzeug nicht auf dieser Spur befindet
		else {
			//Suche nach dem Fahrzeug dieser Spur mit der kleinsten Position
			Fahrzeug naechster = null;
			double naechsterPos = laenge;
			for(int i = 0; i < fahrzeuge.size(); i++) {
				Fahrzeug momentan = fahrzeuge.get(i);
				if(momentan.posGeben() - (momentan.laengeGeben()/2.0) < naechsterPos) {
					naechster = momentan;
					naechsterPos = momentan.posGeben() - (momentan.laengeGeben()/2.0);
				}
			}
			
			//Wenn ein Hindernis gefunden wurde, das in Sichtweite (1km) liegt
			if(naechster != null && naechsterPos + entfernung <= 1000) {
				//Wenn der Sucher kein Dummy war
				if(!(sucher instanceof DummyFahrzeug)) {
					//Nutze Symmetrie aus um Laufzeit zu sparen
					Hindernis symmetrisch = new Hindernis(naechsterPos + entfernung,
							sucher.geschwindigkeitGeben(), sucher, naechster, false, false);
					naechster.hindernisSetzen(symmetrisch, HindernisRichtung.HINTEN);
				}
				//Sonst
				else {
					/*Erstelle ein symmetrisches Hindernis, bei dem die Referenzen auf das
					/Original zeigen*/
					DummyFahrzeug dummy = (DummyFahrzeug) sucher;
					Hindernis symmetrisch = new Hindernis(naechsterPos + entfernung,
							dummy.originalGeben().geschwindigkeitGeben(), dummy.originalGeben(), 
							naechster, false, false);
					//Prüfe, ob dieses Hindernis näher, als das jetzige Hindernis des Ziels ist
					//Wenn der Dummy links gesetzt wurde
					if(dummy.istLinks()) {
						//Wenn das Ziel noch kein Hindernis hatte
						if(naechster.hindernisGeben(HindernisRichtung.HINTEN_RECHTS) != null) {
							//Wenn die Entfernung zum neuen Hindernis kleiner wäre
							if(naechster.hindernisGeben(HindernisRichtung.HINTEN_RECHTS).entfernungGeben()
									> symmetrisch.entfernungGeben()) {
								//Nutze Symmetrie aus und setze das neue Hindernis
								naechster.hindernisSetzen(symmetrisch, HindernisRichtung.HINTEN_RECHTS);
							}
						}
						//Wenn das Ziel noch kein Hindernis hatte
						else {
							//Nutze Symmetrie aus und setze das Hindernis
							naechster.hindernisSetzen(symmetrisch, HindernisRichtung.HINTEN_RECHTS);
						}
					}
					//Wenn der Dummy rechts gesetzt wurde
					else {
						//Wenn das Ziel noch kein Hindernis hatte
						if(naechster.hindernisGeben(HindernisRichtung.HINTEN_LINKS) != null) {
							//Wenn die Entfernung zum neuen Hindernis kleiner wäre
							if(naechster.hindernisGeben(HindernisRichtung.HINTEN_LINKS).entfernungGeben()
									> symmetrisch.entfernungGeben()) {
								//Nutze Symmetrie aus und setze das neue Hindernis
								naechster.hindernisSetzen(symmetrisch, HindernisRichtung.HINTEN_LINKS);
							}
						}
						//Wenn das Ziel noch kein Hindernis hatte
						else {
							//Nutze Symmetrie aus und setze das Hindernis
							naechster.hindernisSetzen(symmetrisch, HindernisRichtung.HINTEN_LINKS);
						}
					}
				}
				
				//Gebe dieses zurück
				return new Hindernis(naechsterPos + entfernung,
						naechster.geschwindigkeitGeben(), naechster, sucher, true, false);
			}
			//Wenn die Sichtweite erfolglos abgesucht wurde
			else if(naechsterPos + entfernung > 1000) {
				//Breche die Suche ab
				return null;
			}
			//Sonst
			else {
				//Führe die Suche beim Nachfolger fort
				return naechsteFahrspur.hindernisVorne(sucher, entfernung + laenge);
			}
		}
	}
	
	/** Sucht nach dem ersten Hindernis hinter dem Fahrzeug in einer Entfernung von 1km
	 *  @param entfernung Die von der vorherigen Spur bereits abgesuchte Entfernung
	 *  @param sucher Das Fahrzeug, das die Hindernissuche durchführt
	 */
	public Hindernis hindernisHinten(Fahrzeug sucher, double entfernung) {
		//Wenn sich das suchende Fahrzeug auf dieser Spur befindet
		if(entfernung == 0) {
			//Suche nach dem Fahrzeug mit dem kleinsten negativen Abstand zum Sucher
			Fahrzeug naechster = null;
			double abstandNaechster = -sucher.posGeben() + (sucher.laengeGeben()/2.0);
			for(int i = 0; i < fahrzeuge.size(); i++) {
				Fahrzeug momentan = fahrzeuge.get(i);
				double abstand = (momentan.posGeben() - sucher.posGeben()) +
						(momentan.laengeGeben()/2.0 + sucher.laengeGeben()/2.0);
				if(abstand < 0 && abstand > abstandNaechster && abstand >= -1000) {
					naechster = momentan;
					abstandNaechster = abstand;
				}
			}
			//Wenn ein Hindernis in Sichtweite (1km) gefunden wurde
			if(naechster != null && abstandNaechster >= -1000) {
				//Wenn der Sucher kein Dummy war
				if(!(sucher instanceof DummyFahrzeug)) {
					//Nutze Symmetrie aus um Laufzeit zu sparen
					Hindernis symmetrisch = new Hindernis(-abstandNaechster,
							sucher.geschwindigkeitGeben(), sucher, naechster, true, true);
					naechster.hindernisSetzen(symmetrisch, HindernisRichtung.VORNE);
				}
				//Sonst
				else {
					/*Erstelle ein symmetrisches Hindernis, bei dem die Referenzen auf das
					/Original zeigen*/
					DummyFahrzeug dummy = (DummyFahrzeug) sucher;
					Hindernis symmetrisch = new Hindernis(-abstandNaechster,
							dummy.originalGeben().geschwindigkeitGeben(), dummy.originalGeben(), 
							naechster, true, true);
					//Prüfe, ob dieses Hindernis näher, als das jetzige Hindernis des Ziels ist
					//Wenn der Dummy links gesetzt wurde
					if(dummy.istLinks()) {
						//Wenn das Ziel noch kein Hindernis hatte
						if(naechster.hindernisGeben(HindernisRichtung.VORNE_RECHTS) != null) {
							//Wenn die Entfernung zum neuen Hindernis kleiner wäre
							if(naechster.hindernisGeben(HindernisRichtung.VORNE_RECHTS).entfernungGeben()
									> symmetrisch.entfernungGeben()) {
								//Nutze Symmetrie aus und setze das neue Hindernis
								naechster.hindernisSetzen(symmetrisch, HindernisRichtung.VORNE_RECHTS);
							}
						}
						//Wenn das Ziel noch kein Hindernis hatte
						else {
							//Nutze Symmetrie aus und setze das Hindernis
							naechster.hindernisSetzen(symmetrisch, HindernisRichtung.VORNE_RECHTS);
						}
					}
					//Wenn der Dummy rechts gesetzt wurde
					else {
						//Wenn das Ziel noch kein Hindernis hatte
						if(naechster.hindernisGeben(HindernisRichtung.VORNE_LINKS) != null) {
							//Wenn die Entfernung zum neuen Hindernis kleiner wäre
							if(naechster.hindernisGeben(HindernisRichtung.VORNE_LINKS).entfernungGeben()
									> symmetrisch.entfernungGeben()) {
								//Nutze Symmetrie aus und setze das neue Hindernis
								naechster.hindernisSetzen(symmetrisch, HindernisRichtung.VORNE_LINKS);
							}
						}
						//Wenn das Ziel noch kein Hindernis hatte
						else {
							//Nutze Symmetrie aus und setze das Hindernis
							naechster.hindernisSetzen(symmetrisch, HindernisRichtung.VORNE_LINKS);
						}
					}
				}
				//Gebe dieses zurück
				return new Hindernis(abstandNaechster, naechster.geschwindigkeitGeben(),
						naechster, sucher, false, true);
			}
			//Wenn die gesuchte Entfernung größer, als 1km ist
			else if(abstandNaechster < -1000) {
				//Breche die Suche ab
				return null;
			}
			//Sonst
			else {
				//Führe die Suche auf der nächsten Spur fort
				return vorherigeFahrspur.hindernisHinten(sucher,
						sucher.posGeben() - (sucher.laengeGeben()/2.0));
			}
		}
		//Wenn sich das suchende Fahrzeug nicht auf dieser Spur befindet
		else {
			//Suche nach dem Fahrzeug dieser Spur mit der größten Position
			Fahrzeug naechster = null;
			double naechsterPos = 0;
			for(int i = 0; i < fahrzeuge.size(); i++) {
				Fahrzeug momentan = fahrzeuge.get(i);
				if(momentan.posGeben() + (momentan.laengeGeben()/2.0) > naechsterPos) {
					naechster = momentan;
					naechsterPos = momentan.posGeben() + (momentan.laengeGeben()/2.0);
				}
			}
			
			//Wenn ein Hindernis gefunden wurde, das in Sichtweite (1km) liegt
			if(naechster != null && (laenge - naechsterPos) + entfernung <= 1000) {
				//Wenn der Sucher kein Dummy war
				if(!(sucher instanceof DummyFahrzeug)) {
					//Nutze Symmetrie aus um Laufzeit zu sparen
					Hindernis symmetrisch = new Hindernis((laenge - naechsterPos) + entfernung,
							sucher.geschwindigkeitGeben(), sucher, naechster, true, false);
					naechster.hindernisSetzen(symmetrisch, HindernisRichtung.VORNE);
				}
				//Sonst
				else {
					/*Erstelle ein symmetrisches Hindernis, bei dem die Referenzen auf das
					/Original zeigen*/
					DummyFahrzeug dummy = (DummyFahrzeug) sucher;
					Hindernis symmetrisch = new Hindernis((laenge - naechsterPos) + entfernung,
							dummy.originalGeben().geschwindigkeitGeben(), dummy.originalGeben(), 
							naechster, true, false);
					//Prüfe, ob dieses Hindernis näher, als das jetzige Hindernis des Ziels ist
					//Wenn der Dummy links gesetzt wurde
					if(dummy.istLinks()) {
						//Wenn das Ziel noch kein Hindernis hatte
						if(naechster.hindernisGeben(HindernisRichtung.VORNE_RECHTS) != null) {
							//Wenn die Entfernung zum neuen Hindernis kleiner wäre
							if(naechster.hindernisGeben(HindernisRichtung.VORNE_RECHTS).entfernungGeben()
									> symmetrisch.entfernungGeben()) {
								//Nutze Symmetrie aus und setze das neue Hindernis
								naechster.hindernisSetzen(symmetrisch, HindernisRichtung.VORNE_RECHTS);
							}
						}
						//Wenn das Ziel noch kein Hindernis hatte
						else {
							//Nutze Symmetrie aus und setze das Hindernis
							naechster.hindernisSetzen(symmetrisch, HindernisRichtung.VORNE_RECHTS);
						}
					}
					//Wenn der Dummy rechts gesetzt wurde
					else {
						//Wenn das Ziel noch kein Hindernis hatte
						if(naechster.hindernisGeben(HindernisRichtung.VORNE_LINKS) != null) {
							//Wenn die Entfernung zum neuen Hindernis kleiner wäre
							if(naechster.hindernisGeben(HindernisRichtung.VORNE_LINKS).entfernungGeben()
									> symmetrisch.entfernungGeben()) {
								//Nutze Symmetrie aus und setze das neue Hindernis
								naechster.hindernisSetzen(symmetrisch, HindernisRichtung.VORNE_LINKS);
							}
						}
						//Wenn das Ziel noch kein Hindernis hatte
						else {
							//Nutze Symmetrie aus und setze das Hindernis
							naechster.hindernisSetzen(symmetrisch, HindernisRichtung.VORNE_LINKS);
						}
					}
				}
				//Gebe dieses zurück
				return new Hindernis((laenge - naechsterPos) + entfernung,
						naechster.geschwindigkeitGeben(), naechster, sucher, false, false);
			}
			//Wenn die Sichtweite erfolglos abgesucht wurde
			else if((laenge - naechsterPos) + entfernung > 1000) {
				//Breche die Suche ab
				return null;
			}
			//Sonst
			else {
				//Führe die Suche beim Vorgänger fort
				return vorherigeFahrspur.hindernisHinten(sucher, entfernung + laenge);
			}
		}
	}
	
	/**Suche auf dem linken Nachbarn nach einem Hindernis
	*  @param vorne Soll vorne oder hinten gesucht werden?
	*  @param sucher Das Fahrzeug, das die Hindernissuche durchführt
	*  @param entfernung Die bereits abgesuchte Entfernung*/
	public Hindernis hindernisLinks(Fahrzeug sucher, double entfernung, boolean vorne) {
		//Wenn es keinen linken Nachbarn gibt
		if(linkeFahrspur == null) {
			//Breche die Hindernissuche ab
			return null;
		}
		//Erzeuge einen Dummy auf dem linken Nachbarn
		DummyFahrzeug dummy = new DummyFahrzeug(sucher, true);
		dummy.spurSetzen(linkeFahrspur);
		linkeFahrspur.fahrzeugHinzufuegen(dummy);
		//Lasse den Dummy die Suche durchführen
		Hindernis h;
		if(vorne) {
			h = dummy.hindernisSuchen(HindernisRichtung.VORNE);
			linkeFahrspur.fahrzeugEntfernen(dummy);
		}
		else {
			h = dummy.hindernisSuchen(HindernisRichtung.HINTEN);
			linkeFahrspur.fahrzeugEntfernen(dummy);
		}
		if(h != null) {
			h.betrachterSetzen(sucher);
		}
		return h;
	}
	
	/**Suche auf dem rechten Nachbarn nach einem Hindernis
	*  @param vorne Soll vorne oder hinten gesucht werden?
	*  @param sucher Das Fahrzeug, das die Hindernissuche durchführt
	*  @param entfernung Die bereits abgesuchte Entfernung*/
	public Hindernis hindernisRechts(Fahrzeug sucher, double entfernung, boolean vorne) {
		//Wenn es keinen rechten Nachbarn gibt
		if(rechteFahrspur == null) {
			//Breche die Hindernissuche ab
			return null;
		}
		//Erzeuge einen Dummy auf dem rechten Nachbarn
		DummyFahrzeug dummy = new DummyFahrzeug(sucher, false);
		dummy.spurSetzen(rechteFahrspur);
		rechteFahrspur.fahrzeugHinzufuegen(dummy);
		//Lasse den Dummy die Suche durchführen
		Hindernis h;
		if(vorne) {
			h = dummy.hindernisSuchen(HindernisRichtung.VORNE);
			rechteFahrspur.fahrzeugEntfernen(dummy);
		}
		else {
			h = dummy.hindernisSuchen(HindernisRichtung.HINTEN);
			rechteFahrspur.fahrzeugEntfernen(dummy);
		}
		if(h != null) {
			h.betrachterSetzen(sucher);
		}
		return h;
	}
//------------------------------------------------------------------------------------------------
}

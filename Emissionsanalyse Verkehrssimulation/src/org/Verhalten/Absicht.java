package org.Verhalten;

import org.Verkehr.Fahrzeug;

/**
 * Abstrakte Oberklasse zur Verwaltung aller Absichten nach dem Absichtsbasierten Verhaltensmodell nach
 * Erlemann ([ER07]). \nEine Absicht ist ein Ziel, welches ein Fahrer verfolgt. Eine Absicht gibt Bewertungen
 * zu bestimmten Aktionen ab, welche in der Verhaltensklasse demokratisch ausgewertet werden. \nWird ein
 * gewisser Schwellenwert überschritten, wird diese Aktion eingeleitet. Alle Bewertungen bewegen sich 
 * im Intervall [-1; 1]. \nMögliche Aktionen sind Beschleunigen/Bremsen, Spurwechsel nach links/rechts, 
 * Sicherheitsbedürfnis abmindern und ein Fahrzeug bei seinem Spurwechsel durch das Bilden einer Lücke 
 * zu unterstützen.
 */
public abstract class Absicht {
	
	/**
	 * Bewertung für die Aktion: Beschleunigen/Bremsen. Diese Bewertung muss keinen Schwellenwert
	 * überschreiten, sondern wird durch lineare Transformation in eine Beschleunigung umgerechnet.
	 */
	protected double beschleunigung;
	
	/**
	 * Bewertung für die Aktion: Spurwechsel nach links. Schwellenwert: 0.9
	 */
	protected double wechselLinks;
	
	/**
	 * Bewertung für die Aktion: Spurwechsel nach rechts. Schwellenwert: 0.9
	 */
	protected double wechselRechts;
	
	/**
	 * Bewertung für die Aktion: Verringern des Sicherheitsabstands. Bewertung 1 entspricht einem halbierten
	 * Sicherheitsabstand.
	 */
	protected double abminderung;
	
	/**
	 * Bewertung für die Aktion: Anforderung für Unterstützung beim Spurwechselvorgang nach links
	 */
	protected double unterstuetzungLinks;
	
	/**
	 * Bewertung für die Aktion: Anforderung für Unterstützung beim Spurwechselvorgang nach rechts
	 */
	protected double unterstuetzungRechts;
	
	/**
	 * Das Fahrzeug, welchem diese Absicht zugeordnet wird
	 */
	protected Fahrzeug fahrzeug;
	
	public Absicht(Fahrzeug f) {
		this.fahrzeug = f;
	}
	
	/**
	 * Gibt den Wert der Bewertung einer bestimmten Aktion zurück.
	 * @param index Der Index der Aktion, deren Bewertung gegeben werden soll.
	 * @return Die Bewertung der gewünschten Aktion
	 */
	public double wert(int index) {
		switch(index) {
		case 0: return beschleunigung;
		case 1: return wechselLinks;
		case 2: return wechselRechts;
		case 3: return abminderung;
		case 4: return unterstuetzungLinks;
		case 5: return unterstuetzungRechts;
		default: return 0;
		}
	}
	
	public abstract void anpassen();
	
	public abstract double tempolimitAktualisieren(double tempolimit);
}

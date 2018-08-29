package org.Verhalten;

import org.Verkehr.Fahrzeug;

/**
 * Abstrakte Oberklasse zur Verwaltung aller Absichten nach dem Absichtsbasierten Verhaltensmodell nach
 * Erlemann ([ER07]). \nEine Absicht ist ein Ziel, welches ein Fahrer verfolgt. Eine Absicht gibt Bewertungen
 * zu bestimmten Aktionen ab, welche in der Verhaltensklasse demokratisch ausgewertet werden. \nWird ein
 * gewisser Schwellenwert �berschritten, wird diese Aktion eingeleitet. Alle Bewertungen bewegen sich 
 * im Intervall [-1; 1]. \nM�gliche Aktionen sind Beschleunigen/Bremsen, Spurwechsel nach links/rechts, 
 * Sicherheitsbed�rfnis abmindern und ein Fahrzeug bei seinem Spurwechsel durch das Bilden einer L�cke 
 * zu unterst�tzen.
 */
public abstract class Absicht {
	
	/**
	 * Bewertung f�r die Aktion: Beschleunigen/Bremsen. Diese Bewertung muss keinen Schwellenwert
	 * �berschreiten, sondern wird durch lineare Transformation in eine Beschleunigung umgerechnet.
	 */
	protected double beschleunigung;
	
	/**
	 * Bewertung f�r die Aktion: Spurwechsel nach links. Schwellenwert: 0.9
	 */
	protected double wechselLinks;
	
	/**
	 * Bewertung f�r die Aktion: Spurwechsel nach rechts. Schwellenwert: 0.9
	 */
	protected double wechselRechts;
	
	/**
	 * Bewertung f�r die Aktion: Verringern des Sicherheitsabstands. Bewertung 1 entspricht einem halbierten
	 * Sicherheitsabstand.
	 */
	protected double abminderung;
	
	/**
	 * Bewertung f�r die Aktion: Anforderung f�r Unterst�tzung beim Spurwechselvorgang nach links
	 */
	protected double unterstuetzungLinks;
	
	/**
	 * Bewertung f�r die Aktion: Anforderung f�r Unterst�tzung beim Spurwechselvorgang nach rechts
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
	 * Gibt den Wert der Bewertung einer bestimmten Aktion zur�ck.
	 * @param index Der Index der Aktion, deren Bewertung gegeben werden soll.
	 * @return Die Bewertung der gew�nschten Aktion
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

package org.Verkehr;
/**Alle Methoden �ber die ein Sensor verf�gen muss*/
public interface Sensor<T> {
	/**Speichere die gesammelten Daten ab*/
	public void schreibeDaten(T daten);
	
}

package org.Verkehr;
/**Alle Methoden über die ein Sensor verfügen muss*/
public interface Sensor<T> {
	/**Speichere die gesammelten Daten ab*/
	public void schreibeDaten(T daten);
	
}

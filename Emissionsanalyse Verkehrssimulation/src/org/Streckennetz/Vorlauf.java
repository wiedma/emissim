package org.Streckennetz;
import org.Verkehr.Fahrzeug;
import org.main.Simulation;
/**Ein Vorlauf ist immer Nachfolger einer Quelle.
 * Er dient dazu den Verkehr zu harmonisieren, bevor die eigentlichen Messungen beginnen.
 * Alle Sensoren bleiben auf den Vorläufen deaktiviert.
 * Die Fahrzuege sollen hier ihre Geschwindigkeiten ihrem Vordermann anpassen und sich für eine Spur entscheiden.
 */
public class Vorlauf extends Gerade {
	
	public Vorlauf(double laenge, double tempolimit) {
		super(laenge, 2, 200, false);
	}
	
	/**Übergibt das Fahrzeug an die nachfolgende Fahrspur und aktiviert den Sensor des Fahrzeugs
	 * @param fahrzeug Das Fahrzeug welches übergeben werden soll
	 */
	@Override
	protected void uebergebeFahrzeug(Fahrzeug fahrzeug) {
		//Referenzen des Fahrzeugs neu setzen
		fahrzeug.posSetzen(fahrzeug.posGeben() - laenge);
		fahrzeug.spurSetzen(naechsteFahrspur);
//		Simulation.fahrzeugHinzufuegen(fahrzeug);
		//Aktiviere den Sensor des Fahrzeugs
		fahrzeug.sensorAktivieren();
		
		//Referenzen der Spuren neu setzen
		naechsteFahrspur.fahrzeugHinzufuegen(fahrzeug);
		this.fahrzeugEntfernen(fahrzeug);
	}

}

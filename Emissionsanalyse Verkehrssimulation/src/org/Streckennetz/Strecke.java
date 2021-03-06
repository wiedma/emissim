package org.Streckennetz;
import org.Verkehr.Fahrzeug;
/**Eine Strecke ist der k�rzeste Pfad zwischen einer Quelle und einer Senke*/
public class Strecke {
	
	/**Die Fahrspuren, die zu dieser Strecke geh�ren*/
	private Fahrspur[] spuren;
	
	/**Die Verkehrsst�rke, die von der Quelle auf dieser Strecke angestrebt wird in Fz/h*/
	private double verkehrsstaerke;
	
	public Strecke(Fahrspur[] spuren, Netz netz) {
		this.spuren = spuren;
		//Standartm��ige Verkehrsst�rke einer Strecke nach Vorbild von [ER07] S.132
		this.verkehrsstaerke = 10;
	}
	
//Getter und Setter ---------------------------------------------------------------------------
	
	public void verkehrsstaerkeSetzen(double verkehrsstaerke) {
		//Speichere die alte Verkehrsst�rke
		double alteVerkehrsstaerke = this.verkehrsstaerke;
		//Setze die neue Verkehrsst�rke
		this.verkehrsstaerke = verkehrsstaerke;
		//Ermittle die Ver�nderung
		double aenderung = this.verkehrsstaerke - alteVerkehrsstaerke;
		//Erneuere alle Verkehrsst�rken in den Fahrspuren dieser Strecke
		for(Fahrspur spur : spuren) {
			spur.verkehrsstaerkeAendern(aenderung);
		}
		
	}
	
	public double verkehrsstaerkeGeben() {
		return verkehrsstaerke;
	}
	
//---------------------------------------------------------------------------------------------
	
	//TODO entfernungBisSpurende() testen
	/**Gibt die Entfernung bis zum Ende des aktuellen Fahrstreifens zur�ck
	 * 
	 * @param fahrzeug Das Fahrzeug, welches die Entfernung anfr�gt
	 * @return Die Entfernung bis zum Ende der aktuellen Spur
	 */
	public double entfernungBisSpurende(Fahrzeug fahrzeug){
		double entfernung = 0;
		Fahrspur spur = fahrzeug.spurGeben();
		//Entfernung bis zum Ende der aktuellen Fahrspur
		entfernung += spur.laengeGeben() - fahrzeug.posGeben();
		//Entfernung bis zum Ende aller Nachfolger der aktuellen Fahrspur
		while(spur.naechsteFahrspurGeben() != null){
			spur = spur.naechsteFahrspurGeben();
			entfernung += spur.laengeGeben();
		}
		return entfernung;
	}
	
	/**Gibt die Ausfahrt an, an der das Fahrzeug die Autobahn verlassen wird
	 * @return Die letzte befahrbare Fahrspur dieser Strecke ist die Ausfahrt
	 */
	public Fahrspur naechsteAusfahrt(){
		//Die letzte Fahrspur in der Strecke ist immer die Senke, also ist die Fahrspur davor die Ausfahrt
		return spuren[spuren.length - 2];
	}
	
	//TODO entfernungBisAusfahrt() testen
	/**Gibt die Entfernung des Fahrzeugs von seiner Ausfahrt an
	 * @param fahrzeug Das Fahrzeug, welches die Entfernung anfr�gt
	 * @return Die Entfernung bis zur Ausfahrt
	 */
	public double entfernungBisAusfahrt(Fahrzeug fahrzeug){
		//Die Ausfahrt, an der das Fahrzeug die Autobahn verl�sst
		Fahrspur ausfahrt = naechsteAusfahrt();
		
		double entfernung = 0;
		Fahrspur spur = fahrzeug.spurGeben();
		//Solange die aktuelle Fahrspur nicht mit der Ausfahrt benachbart ist
		while(!spur.istBenachbart(ausfahrt)){
			//Addiere die L�nge der Spur bzw. die Entfernung des Fahrzeugs bis zum Ende der Spur
			if(spur.equals(fahrzeug.spurGeben())){
				entfernung += spur.laengeGeben() - fahrzeug.posGeben();
			}
			else{
				entfernung += spur.laengeGeben();
			}
		}
		
		return entfernung;
	}
}

package org.Streckennetz;
import org.Verkehr.Fahrzeug;

public class Strecke {
	
	//Die Fahrspuren, die zu dieser Strecke gehören
	private Fahrspur[] spuren;
	
	//Die Verkehrsstärke, die von der Quelle auf dieser Strecke angestrebt wird in Fz/h
	private double verkehrsstaerke;
	
	public Strecke(Fahrspur[] spuren, Netz netz) {
		this.spuren = spuren;
		//Standartmäßige Verkehrsstärke einer Strecke nach Vorbild von [ER07] S.132
		this.verkehrsstaerke = 10;
	}
	
//Getter und Setter ---------------------------------------------------------------------------
	
	public void verkehrsstaerkeSetzen(double verkehrsstaerke) {
		//Speichere die alte Verkehrsstärke
		double alteVerkehrsstaerke = this.verkehrsstaerke;
		//Setze die neue Verkehrsstärke
		this.verkehrsstaerke = verkehrsstaerke;
		//Ermittle die Veränderung
		double aenderung = alteVerkehrsstaerke - this.verkehrsstaerke;
		//Erneuere alle Verkehrsstärken in den Fahrspuren dieser Strecke
		for(Fahrspur spur : spuren) {
			spur.verkehrsstaerkeAendern(aenderung);
		}
		
	}
	
	public double verkehrsstaerkeGeben() {
		return verkehrsstaerke;
	}
	
//---------------------------------------------------------------------------------------------
	
	//TODO entfernungBisSpurende() testen
	//Gibt die Entfernung bis zum Ende des aktuellen Fahrstreifens zurück
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
	
	//Gibt die Ausfahrt an, an der das Fahrzeug die Autobahn verlassen wird
	public Fahrspur naechsteAusfahrt(){
		//Die letzte Fahrspur in der Strecke ist immer die Senke, also ist die Fahrspur davor die Ausfahrt
		return spuren[spuren.length - 2];
	}
	
	//TODO entfernungBisAusfahrt() testen
	//Gibt die Entfernung des Fahrzeugs von seiner Ausfahrt an
	public double entfernungBisAusfahrt(Fahrzeug fahrzeug){
		//Die Ausfahrt, an der das Fahrzeug die Autobahn verlässt
		Fahrspur ausfahrt = naechsteAusfahrt();
		
		double entfernung = 0;
		Fahrspur spur = fahrzeug.spurGeben();
		//Solange die aktuelle Fahrspur nicht mit der Ausfahrt benachbart ist
		while(!spur.istBenachbart(ausfahrt)){
			//Addiere die Länge der Spur bzw. die Entfernung des Fahrzeugs bis zum Ende der Spur
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

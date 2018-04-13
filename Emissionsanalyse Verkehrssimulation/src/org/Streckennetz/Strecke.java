package org.Streckennetz;
import org.Verkehr.Fahrzeug;

public class Strecke {
	
	//Die Fahrspuren, die zu dieser Strecke gehören
	private Fahrspur[] spuren;
	
	public Strecke(Fahrspur[] spuren, Netz netz) {
		this.spuren = spuren;
	}
	
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

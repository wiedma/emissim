package org.Verhalten;

import org.PhysicEngine.Physics;
import org.Verkehr.Fahrzeug;
import org.Verkehr.Hindernis;
import org.Verkehr.HindernisRichtung;
import org.Verkehr.PKW;

public class AbstandHalten extends Absicht {
	
	/**
	 * Die Wunschgeschwindigkeit des Fahrers als normalverteilte Zufallszahl (Werte nach [ER07])
	 */
	private double wunschgeschwindigkeit;
	/**
	 * Das Sicherheitsbedürfnis des Fahrers als (0.5, 0.15)-normalverteilte Zufallszahl
	 */
	private double sicherheitsbeduerfnis;
	
	/**
	 * Das Schätzvermögen des Fahrers als (0.5, 0.15)-normalverteilte Zufallszahl
	 */
	private double schaetzvermoegen;
	
	/**
	 * Der Beschleunigungswille des Fahrers als (0.5, 0.15)-normalverteilte Zufallszahl
	 */
	private double beschleunigungswille;
	
	/**
	 * Die Fähigkeit des Fahrers sein Gaspedal zu kontrollieren als (0.5, 0.15)-normalverteilte Zufallszahl
	 */
	private double gaspedalkontrolle;
	
	/**
	 * Minimal gewünschter Bruttoabstand zum Vordermann bei Stillstand
	 */
	private double ax;
	
	/**
	 * Minimal gewünschter Folgeabstand zum Vordermann bei annähernd gleicher Geschwindigkeit
	 */
	private double bx;
	
	/**
	 * Wahrnehmungsschwelle für Geschwindigkeitsdifferenzen bei relativ großen Abständen
	 */
	private double sdv;
	
	/**
	 * obere Grenze für das Abdriften vom Vordermann beim Folgevorgang
	 */
	private double sdx;
	
	/**
	 * Wahrnehmungsschwelle für kleinste Geschwindigkeitsdifferenzen bei kleinen, abnehmenden Abständen
	 */
	private double cldv;
	
	/**
	 * Wahrnehmungsschwelle für kleinste Geschwindigkeitsdifferenzen bei kleinen, zunehmenden Abständen
	 */
	private double opdv;
	
	/**
	 * Der Abstand des Fahrzeugs zu seinem Vordermann
	 */
	private double dx;
	
	/**
	 * Die Geschwindigkeitsdifferenz zwischen diesem Fahrzeug und dem Vordermann
	 */
	private double dv;
	
	/**
	 * Erzeugt einen Lerneffekt der Einschätzung der Geschwindigkeit des Vordermanns gegenüber
	 */
	private double r;
	
	/**
	 * Die kleinste mögliche Änderung am Gaspedal
	 */
	private double bnull;
	
	/**
	 * Gibt an, ob das Fahrzeug aufgrund eines Unfalls zum stehen kommen soll
	 */
	private boolean unfall;
	
	public AbstandHalten(Fahrzeug f) {
		super(f);
		//Erzeugung der Zufallszahlen ZF0 bis ZF4
		double tempolimit = fahrzeug.spurGeben().maxGeschwindigkeitGeben();
		tempolimitAktualisieren(tempolimit);
		sicherheitsbeduerfnis = f.sicherheitsbeduerfnisGeben();
		schaetzvermoegen = f.schaetzvermoegenGeben();
		beschleunigungswille = f.beschleunigungswilleGeben();
		gaspedalkontrolle = f.gaspedalkontrolleGeben();
		bnull = 0.2 * (gaspedalkontrolle + Physics.normalverteilung(0.5, 0.15));
		unfall = false;
	}
	
	@Override
	/**
	 * Bestimmung der Wunschgeschwindigkeit nach [ER07]
	 * @param tempolimit Die neu geltende Geschwindigkeitsbeschränkung
	 */
	public double tempolimitAktualisieren(double tempolimit) {
		if(fahrzeug instanceof PKW) {
			if(tempolimit > 120) {
				wunschgeschwindigkeit = Physics.normalverteilung(142, 20);
			}
			else if(tempolimit > 100) {
				wunschgeschwindigkeit = Physics.normalverteilung(120, 20);
			}
			else if(tempolimit > 80) {
				wunschgeschwindigkeit = Physics.normalverteilung(110, 18);
			}
			else if(tempolimit > 60) {
				wunschgeschwindigkeit = Physics.normalverteilung(100, 15);
			}
			else if(tempolimit > 50) {
				wunschgeschwindigkeit = Physics.normalverteilung(80, 15);
			}
			else {
				wunschgeschwindigkeit = Physics.normalverteilung(50, 10);
			}
		}
		else {
			if(tempolimit > 120) {
				wunschgeschwindigkeit = Physics.normalverteilung(92, 5);
			}
			else if(tempolimit > 100) {
				wunschgeschwindigkeit = Physics.normalverteilung(91, 5);
			}
			else if(tempolimit > 80) {
				wunschgeschwindigkeit = Physics.normalverteilung(90, 5);
			}
			else if(tempolimit > 60) {
				wunschgeschwindigkeit = Physics.normalverteilung(85, 4);
			}
			else if(tempolimit > 50) {
				wunschgeschwindigkeit = Physics.normalverteilung(75, 3);
			}
			else {
				wunschgeschwindigkeit = Physics.normalverteilung(50, 6);
			}
		}
		//Konvertierung von km/h zu m/s
		wunschgeschwindigkeit = wunschgeschwindigkeit / 3.6;
		
		return wunschgeschwindigkeit;
	}

	@Override
	public void anpassen() {
		//Bestimme die Beschleunigung nach Wiedemann
		double beschleunigungAbsolut = beschleunigungBestimmen();
		
		//Berechne Bmin und Bmax
		double bmax, bmin;
		if(fahrzeug instanceof PKW) {
			bmax = (0.2 + 0.8 * beschleunigungswille) * (7 - Math.sqrt(fahrzeug.geschwindigkeitGeben()));
			bmin = -8 - 2 * beschleunigungswille + 0.5 * Math.sqrt(fahrzeug.geschwindigkeitGeben());
		}
		else {
			bmax = 1.581 - 0.057 * fahrzeug.geschwindigkeitGeben() + (0.6 - 0.01 * fahrzeug.geschwindigkeitGeben()) * beschleunigungswille;
			bmin = -6 - 2 * beschleunigungswille + 0.09 * fahrzeug.geschwindigkeitGeben();
		}
		
		//Normiere die gewählte Beschleunigung auf das Intervall [-1; 1]
		double beschleunigungNormiert;
		if(Math.signum(beschleunigungAbsolut) > 0) {
			beschleunigungNormiert = beschleunigungAbsolut/bmax;
		}
		else if(Math.signum(beschleunigungAbsolut) < 0) {
			beschleunigungNormiert = - (Math.abs(beschleunigungAbsolut) / Math.abs(bmin));
		}
		else {
			beschleunigungNormiert = 0;
		}
		
		beschleunigung = beschleunigungNormiert;
	}
	
	/**
	 * Bestimme die neue Beschleunigung für das Fahrzeug nach dem Fahrzeug-Folgemodell von Wiedemann
	 * @param f Das Fahrzeug, das dieses Fahrverhalten anwenden soll
	 * @return Die neue Beschleunigung
	 */
	private double beschleunigungBestimmen() {
		
		if(unfall) {
			return 0;
		}
		
		Fahrzeug vordermann;
		Hindernis vorne;
		try {
			vorne = fahrzeug.hindernisGeben(HindernisRichtung.VORNE);
			vordermann = vorne.zielFahrzeug();
		} catch (Exception e) {
			return wunsch();
		}
		
		//Berechnung der einzelnen Parameter
		dx = vorne.entfernungGeben();
		dv = fahrzeug.geschwindigkeitGeben() - vordermann.geschwindigkeitGeben();
				
		ax = vordermann.laengeGeben() + 1 + 2 * sicherheitsbeduerfnis;
		
		double bxGeschwindigkeit = (fahrzeug.hindernisGeben(HindernisRichtung.VORNE).kollisionszeit() > 0) ? fahrzeug.geschwindigkeitGeben() : vordermann.geschwindigkeitGeben();
		bx = ax + (1 + 7 * sicherheitsbeduerfnis) * Math.sqrt(bxGeschwindigkeit);
		
		double cx = 25 * (1 + sicherheitsbeduerfnis + schaetzvermoegen);
		sdv = Math.pow((dx - ax)/cx, 2);
		
		double ex = 2 - schaetzvermoegen + Physics.normalverteilung(0.5, 0.15);
		sdx = ax + ex * (bx - ax);
		
		cldv = sdv * ex * ex;
		
		opdv = cldv * (-1 - 2 * Physics.normalverteilung(0.5, 0.15));
		
		//Bestimme die anzuwendende Prozedur zur Beschleunigungsbestimmung
		if(dx <= bx) {
			return bremsax();
		}
		
		if(dx < sdx) {
			if(dv > cldv) {
				return bremsbx();
			}
			else if(dv > opdv) {
				return folgen();
			}
			else {
				return wunsch();
			}
		}
		else if(dv >= sdv && dx < 1000) {
			return bremsbx();
		}
		else {
			return wunsch();
		}
		
	}
	
	/**
	 * Entspricht der Prozedur BREMSAX. Notfallbremsung um einen Unfall zu verhindern
	 * @return Beschleunigung, die das Fahrzeug in diesem Zeitschritt anwenden soll
	 */
	private double bremsax() {
		r += 1;
		double bmin;
		if(fahrzeug instanceof PKW) {
			bmin = -8 - 2 * beschleunigungswille + 0.5 * Math.sqrt(fahrzeug.geschwindigkeitGeben());
		}
		else {
			bmin = -6 - 2 * beschleunigungswille + 0.09 * fahrzeug.geschwindigkeitGeben();
		}
		
		double beschleunigungVordermann = fahrzeug.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug().beschleunigungGeben();
		
		double beschleunigungObjektiv = 0.5 * ((dv * dv)/(ax - dx)) + beschleunigungVordermann + bmin * 
				((bx - dx)/(bx - ax));
		
		double schaetzfehler = (1 - schaetzvermoegen) * ((1 - 2 * Physics.normalverteilung(0.5, 0.15))/r);
		
		double beschleunigungSubjektiv = beschleunigungObjektiv + beschleunigungObjektiv * schaetzfehler;
		
		//Untere Schwelle der Gaspedalkontrolle
		if(Math.abs(beschleunigungSubjektiv) < Math.abs(bnull)) {
			beschleunigungSubjektiv = Math.signum(beschleunigungSubjektiv) * bnull;
		}
		
		//Maximale Bremsfähigkeit des Fahrzeuges
		if(beschleunigungSubjektiv < bmin) {
			beschleunigungSubjektiv = bmin;
		}
		
		
		//Unfall
		if(dx < (fahrzeug.laengeGeben()/2.0) + (fahrzeug.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug().laengeGeben()/2.0)) {
			unfall();
		}
		
		return beschleunigungSubjektiv;
	}
	
	/**
	 * Entspricht der Prozedur BREMSBX. Bewusst beeinflusstes Fahren hinter dem Vordermann
	 * @return Beschleunigung, die das Fahrzeug in diesem Zeitschritt anwenden soll
	 */
	private double bremsbx() {
		r += 1;
		double beschleunigungObjektiv = 0.5 * ((dv*dv)/(bx-dx));
		double br = -0.5 - 1.5 * beschleunigungswille;
		if(fahrzeug.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug().beschleunigungGeben() < br) {
			beschleunigungObjektiv += fahrzeug.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug().beschleunigungGeben();
		}
		double schaetzfehler = (1 - schaetzvermoegen) * ((1 - 2 * Physics.normalverteilung(0.5, 0.15))/r);
		
		double beschleunigungSubjektiv = beschleunigungObjektiv + beschleunigungObjektiv * schaetzfehler;
		
		double bmin;
		if(fahrzeug instanceof PKW) {
			bmin = -8 - 2 * beschleunigungswille + 0.5 * Math.sqrt(fahrzeug.geschwindigkeitGeben());
		}
		else {
			bmin = -6 - 2 * beschleunigungswille + 0.09 * fahrzeug.geschwindigkeitGeben();
		}
		
		//Untere Schwelle der Gaspedalkontrolle
		if(Math.abs(beschleunigungSubjektiv) < Math.abs(bnull)) {
			beschleunigungSubjektiv = Math.signum(beschleunigungSubjektiv) * bnull;
		}
		
		//Maximale Bremsfähigkeit des Fahrzeuges
		if(beschleunigungSubjektiv < bmin) {
			beschleunigungSubjektiv = bmin;
		}
		
		return beschleunigungSubjektiv;
	}
	
	/**
	 * Entspricht der Prozedur FOLGEN. Unbewusst beeinflusstes Fahren hinter dem Vordermann
	 * @return Beschleunigung, die das Fahrzeug in diesem Zeitschritt anwenden soll
	 */
	private double folgen() {
		double beschleunigung;
		if(fahrzeug.beschleunigungGeben() > 0) {
			beschleunigung = bnull;
		}
		else {
			beschleunigung = -bnull;
		}
		
		Fahrzeug vordermann, vorvordermann;
		try {
			vordermann = fahrzeug.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug();
			vorvordermann = vordermann.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug();
		} catch (NullPointerException e) {
			return beschleunigung;
		}
		
		double br = -0.5 - 1.5 * beschleunigungswille;
		
		if(vordermann.hindernisGeben(HindernisRichtung.VORNE).entfernungGeben() < bx) {
			if(Math.abs(vorvordermann.beschleunigungGeben()) > br) {
				r+=1;
			}
			else {
				r = 0;
			}
		}
		
		return beschleunigung;
		
	}
	
	/**
	 * Entspricht der Prozedur WUNSCH. Unbeeinflusstes Fahren
	 * @return Beschleunigung, die das Fahrzeug in diesem Zeitschritt anwenden soll
	 */
	private double wunsch() {
		r = 0;
		
		//Berechne Bmin und Bmax
		double bmax, bmin;
		if(fahrzeug instanceof PKW) {
			bmax = (0.2 + 0.8 * beschleunigungswille) * (7 - Math.sqrt(fahrzeug.geschwindigkeitGeben()));
			bmin = -8 - 2 * beschleunigungswille + 0.5 * Math.sqrt(fahrzeug.geschwindigkeitGeben());
		}
		else {
			bmax = 1.581 - 0.057 * fahrzeug.geschwindigkeitGeben() + (0.6 - 0.01 * fahrzeug.geschwindigkeitGeben()) * beschleunigungswille;
			bmin = -6 - 2 * beschleunigungswille + 0.09 * fahrzeug.geschwindigkeitGeben();
		}
		
		//Wunschbeschleunigung führt zur Wunschgeschwindigkeit innnerhalb dieser Zeiteinheit
		double beschleunigungWunsch = (wunschgeschwindigkeit - fahrzeug.geschwindigkeitGeben())/Physics.DELTA_TIME;
		
		//Obere Grenze für die Beschleunigung
		if(beschleunigungWunsch > bmax) {
			beschleunigungWunsch = bmax;
		}
		//Obere Grenze für den Bremsvorgang
		else if(beschleunigungWunsch < bmin) {
			beschleunigungWunsch = bmin;
		}
		//Untere Grenze der Gaspedalkontrolle
		else if(Math.abs(beschleunigungWunsch) < bnull) {
			beschleunigungWunsch = (beschleunigungWunsch > 0) ? bnull : -bnull;
		}
		
		//Wenn kein Vordermann existiert, oder dieser zu weit entfernt ist
		if(dx == 0 || dx > 2 * bx) {
			return beschleunigungWunsch;
		}
		
		return beschleunigungWunsch * ((dx - bx)/bx);
	}
	
	/**
	 * Entspricht der Prozedur UNFALL. Eine Kollision zwischen zwei Fahrzeugen.
	 * Ihre Geschwindigkeiten werden auf 0 gesetzt und die Fahrzeuge kommen zum Stillstand.
	 */
	private void unfall() {
		fahrzeug.unfall();
		fahrzeug.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug().unfall();
		System.out.println("UNFALL");
		System.exit(1);
	}

}

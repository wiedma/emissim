package org.Verhalten;
import org.PhysicEngine.Physics;
import org.Verkehr.Fahrzeug;
import org.Verkehr.Hindernis;
import org.Verkehr.HindernisRichtung;
import org.Verkehr.PKW;

/**
 * Beschreibt das Verhalten von Fahrzeugen im einspurigen Verkehr nach Wiedemann [WI74]
 */
public class FahrverhaltenWiedemann extends Fahrverhalten {
	/**
	 * Die Wunschgeschwindigkeit des Fahrers als normalverteilte Zufallszahl (Werte nach [ER07])
	 */
	private double wunschgeschwindigkeit;
	/**
	 * Das Sicherheitsbed�rfnis des Fahrers als (0.5, 0.15)-normalverteilte Zufallszahl
	 */
	private double sicherheitsbeduerfnis;
	
	/**
	 * Das Sch�tzverm�gen des Fahrers als (0.5, 0.15)-normalverteilte Zufallszahl
	 */
	private double schaetzvermoegen;
	
	/**
	 * Der Beschleunigungswille des Fahrers als (0.5, 0.15)-normalverteilte Zufallszahl
	 */
	private double beschleunigungswille;
	
	/**
	 * Die F�higkeit des Fahrers sein Gaspedal zu kontrollieren als (0.5, 0.15)-normalverteilte Zufallszahl
	 */
	private double gaspedalkontrolle;
	
	/**
	 * Minimal gew�nschter Bruttoabstand zum Vordermann bei Stillstand
	 */
	private double ax;
	
	/**
	 * Minimal gew�nschter Folgeabstand zum Vordermann bei ann�hernd gleicher Geschwindigkeit
	 */
	private double bx;
	
	/**
	 * Wahrnehmungsschwelle f�r Geschwindigkeitsdifferenzen bei relativ gro�en Abst�nden
	 */
	private double sdv;
	
	/**
	 * obere Grenze f�r das Abdriften vom Vordermann beim Folgevorgang
	 */
	private double sdx;
	
	/**
	 * Wahrnehmungsschwelle f�r kleinste Geschwindigkeitsdifferenzen bei kleinen, abnehmenden Abst�nden
	 */
	private double cldv;
	
	/**
	 * Wahrnehmungsschwelle f�r kleinste Geschwindigkeitsdifferenzen bei kleinen, zunehmenden Abst�nden
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
	 * Erzeugt einen Lerneffekt der Einsch�tzung der Geschwindigkeit des Vordermanns gegen�ber
	 */
	private double r;
	
	/**
	 * Die kleinste m�gliche �nderung am Gaspedal
	 */
	private double bnull;
	
	/**
	 * Gibt an, ob das Fahrzeug aufgrund eines Unfalls zum stehen kommen soll
	 */
	private boolean unfall;
	
	/**
	 * Erzeugt ein neues Fahrverhalten nach Wiedemann mit den entsprechenden Parametern
	 * @param f Das Fahrzeug, das dieses Fahrverhalten anwenden soll
	 */
	public FahrverhaltenWiedemann(Fahrzeug f) {
		super(f);
		//Erzeugung der Zufallszahlen ZF0 bis ZF4
		double tempolimit = f.spurGeben().maxGeschwindigkeitGeben();
		tempolimitAktualisieren(tempolimit);
		sicherheitsbeduerfnis = Physics.normalverteilung(0.5, 0.15);
		schaetzvermoegen = Physics.normalverteilung(0.5, 0.15);
		beschleunigungswille = Physics.normalverteilung(0.5, 0.15);
		gaspedalkontrolle = Physics.normalverteilung(0.5, 0.15);
		bnull = 0.2 * (gaspedalkontrolle + Physics.normalverteilung(0.5, 0.15));
		this.f = f;
		unfall = false;
	}

	@Override
	/**
	 * Bestimme die neue Beschleunigung f�r das Fahrzeug nach dem Fahrzeug-Folgemodell von Wiedemann
	 * @param f Das Fahrzeug, das dieses Fahrverhalten anwenden soll
	 * @return Die neue Beschleunigung
	 */
	public double beschleunigungBestimmen() {
		
		if(unfall) {
			return 0;
		}
		
		Fahrzeug vordermann;
		Hindernis vorne;
		try {
			vorne = f.hindernisGeben(HindernisRichtung.VORNE);
			vordermann = vorne.zielFahrzeug();
		} catch (Exception e) {
			return wunsch();
		}
		
		//Berechnung der einzelnen Parameter
		dx = vorne.entfernungGeben();
		dv = f.geschwindigkeitGeben() - vordermann.geschwindigkeitGeben();
				
		ax = 5.5 + 2 * sicherheitsbeduerfnis;
		
		double bxGeschwindigkeit = (f.hindernisGeben(HindernisRichtung.VORNE).kollisionszeit() > 0) ? f.geschwindigkeitGeben() : vordermann.geschwindigkeitGeben();
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

	@Override
	/**
	 * Bestimme, ob das Fahrzeug seine Spur wechseln soll, oder nicht
	 * @param f Das Fahrzeug, das dieses Fahrverhalten anwenden soll
	 * @return "links" f�r Spurwechsel links, "rechts" f�r Spurwechsel rechts und "" f�r kein Wechsel
	 */
	public String spurwechselBestimmen() {
		//TODO Spurwechselentscheidung implementieren
		return "";
	}

	@Override
	/**
	 * Bestimmung der Wunschgeschwindigkeit nach [ER07]
	 * @param tempolimit Die neu geltende Geschwindigkeitsbeschr�nkung
	 */
	public void tempolimitAktualisieren(double tempolimit) {
		if(f instanceof PKW) {
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
	}
	
	/**
	 * Entspricht der Prozedur BREMSAX. Notfallbremsung um einen Unfall zu verhindern
	 * @return Beschleunigung, die das Fahrzeug in diesem Zeitschritt anwenden soll
	 */
	private double bremsax() {
		r += 1;
		double bmin;
		if(f instanceof PKW) {
			bmin = -8 - 2 * beschleunigungswille + 0.5 * Math.sqrt(f.geschwindigkeitGeben());
		}
		else {
			bmin = -6 - 2 * beschleunigungswille + 0.09 * f.geschwindigkeitGeben();
		}
		
		double beschleunigungVordermann = f.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug().beschleunigungGeben();
		
		double beschleunigungObjektiv = 0.5 * ((dv * dv)/(ax - dx)) + beschleunigungVordermann + bmin * 
				((bx - dx)/(bx - ax));
		
		double schaetzfehler = (1 - schaetzvermoegen) * ((1 - 2 * Physics.normalverteilung(0.5, 0.15))/r);
		
		double beschleunigungSubjektiv = beschleunigungObjektiv + beschleunigungObjektiv * schaetzfehler;
		
		//Untere Schwelle der Gaspedalkontrolle
		if(beschleunigungSubjektiv > -bnull) {
			beschleunigungSubjektiv = -bnull;
		}
		
		//Maximale Bremsf�higkeit des Fahrzeuges
		if(beschleunigungSubjektiv < bmin) {
			beschleunigungSubjektiv = bmin;
		}
		
		//Unfall
		if(dx < (f.laengeGeben()/2.0) + (f.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug().laengeGeben()/2.0)) {
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
		if(f.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug().beschleunigungGeben() < br) {
			beschleunigungObjektiv += f.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug().beschleunigungGeben();
		}
		double schaetzfehler = (1 - schaetzvermoegen) * ((1 - 2 * Physics.normalverteilung(0.5, 0.15))/r);
		
		double beschleunigungSubjektiv = beschleunigungObjektiv + beschleunigungObjektiv * schaetzfehler;
		
		double bmin;
		if(f instanceof PKW) {
			bmin = -8 - 2 * beschleunigungswille + 0.5 * Math.sqrt(f.geschwindigkeitGeben());
		}
		else {
			bmin = -6 - 2 * beschleunigungswille + 0.09 * f.geschwindigkeitGeben();
		}
		
		//Untere Schwelle der Gaspedalkontrolle
		if(beschleunigungSubjektiv > -bnull) {
			beschleunigungSubjektiv = -bnull;
		}
		
		//Maximale Bremsf�higkeit des Fahrzeuges
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
		if(f.beschleunigungGeben() > 0) {
			beschleunigung = bnull;
		}
		else {
			beschleunigung = -bnull;
		}
		
		Fahrzeug vordermann, vorvordermann;
		try {
			vordermann = f.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug();
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
		if(f instanceof PKW) {
			bmax = (0.2 + 0.8 * beschleunigungswille) * (7 - Math.sqrt(f.geschwindigkeitGeben()));
			bmin = -8 - 2 * beschleunigungswille + 0.5 * Math.sqrt(f.geschwindigkeitGeben());
		}
		else {
			bmax = 1.581 - 0.057 * f.geschwindigkeitGeben() + (0.6 - 0.01 * f.geschwindigkeitGeben()) * beschleunigungswille;
			bmin = -6 - 2 * beschleunigungswille + 0.09 * f.geschwindigkeitGeben();
		}
		
		//Wunschbeschleunigung f�hrt zur Wunschgeschwindigkeit innnerhalb dieser Zeiteinheit
		double beschleunigungWunsch = (wunschgeschwindigkeit - f.geschwindigkeitGeben())/Physics.DELTA_TIME;
		
		//Obere Grenze f�r die Beschleunigung
		if(beschleunigungWunsch > bmax) {
			beschleunigungWunsch = bmax;
		}
		//Obere Grenze f�r den Bremsvorgang
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
		f.unfall();
		f.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug().unfall();
		System.out.println("UNFALL");
		System.exit(1);
	}

}

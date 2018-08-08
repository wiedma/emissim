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
	}

	@Override
	/**
	 * Bestimme die neue Beschleunigung für das Fahrzeug nach dem Fahrzeug-Folgemodell von Wiedemann
	 * @param f Das Fahrzeug, das dieses Fahrverhalten anwenden soll
	 * @return Die neue Beschleunigung
	 */
	public double beschleunigungBestimmen() {
		Fahrzeug vordermann;
		Hindernis vorne;
		try {
			vorne = f.hindernisGeben(HindernisRichtung.VORNE);
			vordermann = vorne.zielFahrzeug();
		} catch (NullPointerException e) {
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
	 * @return "links" für Spurwechsel links, "rechts" für Spurwechsel rechts und "" für kein Wechsel
	 */
	public String spurwechselBestimmen() {
		//TODO Spurwechselentscheidung implementieren
		return "";
	}

	@Override
	/**
	 * Bestimmung der Wunschgeschwindigkeit nach [ER07]
	 * @param tempolimit Die neu geltende Geschwindigkeitsbeschränkung
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
		//TODO Prozedur BREMSAX implementieren
		return 0;
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
		
		double beschleunigungSubjektiv = beschleunigungObjektiv * schaetzfehler;
		
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
		//TODO Prozedur FOLGEN implementieren
		return 0;
	}
	
	/**
	 * Entspricht der Prozedur WUNSCH. Unbeeinflusstes Fahren
	 * @return Beschleunigung, die das Fahrzeug in diesem Zeitschritt anwenden soll
	 */
	private double wunsch() {
		r = 0;
		
		double bmax;
		if(f instanceof PKW) {
			bmax = (0.2 + 0.8 * beschleunigungswille) * (7 - Math.sqrt(f.geschwindigkeitGeben()));
		}
		else {
			bmax = 1.581 - 0.057 * f.geschwindigkeitGeben() + (0.6 - 0.01 * f.geschwindigkeitGeben()) * beschleunigungswille;
		}
		
		return bmax * ((dx - bx)/bx);
	}

}

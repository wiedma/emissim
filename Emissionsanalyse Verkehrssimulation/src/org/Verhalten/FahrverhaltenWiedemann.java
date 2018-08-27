package org.Verhalten;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.PhysicEngine.Physics;
import org.Verkehr.Fahrzeug;
import org.Verkehr.Hindernis;
import org.Verkehr.HindernisRichtung;
import org.Verkehr.PKW;

/**
 * Beschreibt das Verhalten von Fahrzeugen im einspurigen Verkehr nach Wiedemann [WI74] \n
 * im mehrspurigen Verkehr nach Sparmann [SPA78].
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
	 * Debug-Datei
	 */
	private File file;
	
	private int id;
	
	private static int nummer = 0;
	
	private PrintWriter writer;
	
	private static final boolean writing = false;
	
	/**
	 * Wahrscheinlichkeit, mit der in diesem Zeitschritt der Tr�deleffekt eingeleitet wird
	 */
	public static final double TROEDELWAHRSCHEINLICHKEIT = 0.05;
	
	/**
	 * Die Dauer des Tr�deleffektes in s
	 */
	public static final double TROEDELDAUER = 2.5;
	
	/**
	 * Die Zeit, die dieses Fahrzeug bereits tr�delt
	 */
	private double troedelzeit;
	
	/**
	 * Erzeugt ein neues Fahrverhalten nach Wiedemann mit den entsprechenden Parametern
	 * @param f Das Fahrzeug, das dieses Fahrverhalten anwenden soll
	 */
	public FahrverhaltenWiedemann(Fahrzeug f) {
		super(f);
		
		id = nummer;
		nummer++;
		
		//Erzeugung der Zufallszahlen ZF0 bis ZF4
		double tempolimit = f.spurGeben().maxGeschwindigkeitGeben();
		tempolimitAktualisieren(tempolimit);
		sicherheitsbeduerfnis = f.sicherheitsbeduerfnisGeben();
		schaetzvermoegen = f.schaetzvermoegenGeben();
		beschleunigungswille = f.beschleunigungswilleGeben();
		gaspedalkontrolle = f.gaspedalkontrolleGeben();
		bnull = 0.2 * (gaspedalkontrolle + Physics.normalverteilung(0.5, 0.15));
		this.f = f;
		unfall = false;
		
		if(writing) {
			file = new File("DebugLog/Wiedemann" + id + ".txt");
			File parent = file.getParentFile();
			if (!parent.exists() && !parent.mkdirs()) {
				throw new IllegalStateException("Couldn't create dir: " + parent);
			}
			try {
				file.createNewFile();
				writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	/**
	 * Bestimme die neue Beschleunigung f�r das Fahrzeug nach dem Fahrzeug-Folgemodell von Wiedemann
	 * @param f Das Fahrzeug, das dieses Fahrverhalten anwenden soll
	 * @return Die neue Beschleunigung
	 */
	public double beschleunigungBestimmen() {
		
		//Wenn das Fahrzeug in einen Unfall verwickelt ist
		if(unfall) {
			//Soll es nicht beschleunigen
			return 0;
		}
		
		double beschleunigung;
		
		//Bestimme den Vordemann des Fahrzeuges
		Fahrzeug vordermann;
		Hindernis vorne;
		try {
			vorne = f.hindernisGeben(HindernisRichtung.VORNE);
			vordermann = vorne.zielFahrzeug();
		} catch (Exception e) {
			//Wenn es keinen Vordermann gibt, strebe die Wunschgeschwindigkeit an
			beschleunigung = wunsch();
			return troedel(beschleunigung);
		}
		
		
		//Berechnung der einzelnen Parameter
		dx = vorne.entfernungGeben();
		
		dv = f.geschwindigkeitGeben() - vordermann.geschwindigkeitGeben();
				
		ax = (vordermann.laengeGeben()/2.0) + (f.laengeGeben()/2.0) + 1 + 2 * sicherheitsbeduerfnis;
		
		double bxGeschwindigkeit = (f.hindernisGeben(HindernisRichtung.VORNE).kollisionszeit() > 0) ? f.geschwindigkeitGeben() : vordermann.geschwindigkeitGeben();
		bx = ax + (1 + 7 * sicherheitsbeduerfnis) * Math.sqrt(bxGeschwindigkeit);
		
		double cx = 25 * (1 + sicherheitsbeduerfnis + schaetzvermoegen);
		sdv = Math.pow((dx - ax)/cx, 2);
		
		double ex = 2 - schaetzvermoegen + Physics.normalverteilung(0.5, 0.15);
		sdx = ax + ex * (bx - ax);
		
		cldv = sdv * ex * ex;
		
		opdv = cldv * (-1 - 2 * Physics.normalverteilung(0.5, 0.15));
		
		if(writing) {
			writer.println("------------------------------------------------------------------");
			writer.println("DX: " + dx);
			writer.println("DV: " +  dv);
			writer.println("AX: " + ax);
			writer.println("BX: " + bx);
			writer.println("SDV: " + sdv);
			writer.println("SDX: " + sdx);
			writer.println("CLDV: " + cldv);
			writer.println("OPDV: " + opdv);
			writer.println("R: " + r);
			writer.println("Geschwindigkeit: " + f.geschwindigkeitGeben());
			writer.println("Geschwindigkeit Vordermann: " + vordermann.geschwindigkeitGeben());
			writer.println("POS: " + f.posGeben());
		}
		
		//Bestimme die anzuwendende Prozedur zur Beschleunigungsbestimmung
		if(dx <= bx) {
			if(writing)
			writer.println("PROZEDUR: BREMSAX");
			beschleunigung = troedel(bremsax());
		}
		else {
			if(dx < sdx) {
				if(dv > cldv) {
					if(writing)
						writer.println("PROZEDUR: BREMSBX");
					beschleunigung = troedel(bremsbx());
				}
				else if(dv > opdv) {
					if(writing)
						writer.println("PROZEDUR: FOLGEN");
					beschleunigung = troedel(folgen());
				}
				else {
					if(writing)
					writer.println("PROZEDUR: WUNSCH");
					beschleunigung = troedel(wunsch());
				}
			}
			else if(dv >= sdv && dx < 1000) {
				if(writing)
					writer.println("PROZEDUR: BREMSBX");
				beschleunigung = troedel(bremsbx());
			}
			else {
				if(writing)
					writer.println("PROZEDUR: WUNSCH");
				beschleunigung = troedel(wunsch());
			}
		}
		
		//Verhindere negative Geschwindigkeiten
		if(f.geschwindigkeitGeben() + (beschleunigung * Physics.DELTA_TIME) <= 0) {
			f.geschwindigkeitSetzen(0);
			return 0;
		}
		else {
			return beschleunigung;
		}
	}

	@Override
	/**
	 * Bestimme, ob das Fahrzeug seine Spur wechseln soll, oder nicht
	 * @param f Das Fahrzeug, das dieses Fahrverhalten anwenden soll
	 * @return "links" f�r Spurwechsel links, "rechts" f�r Spurwechsel rechts und "" f�r kein Wechsel
	 */
	public String spurwechselBestimmen() {
		//TODO Spurwechselentscheidungnach Sparmann implementieren
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
		if(Math.abs(beschleunigungSubjektiv) < Math.abs(bnull)) {
			beschleunigungSubjektiv = Math.signum(beschleunigungSubjektiv) * bnull;
		}
		
		//Maximale Bremsf�higkeit des Fahrzeuges
		if(beschleunigungSubjektiv < bmin) {
			beschleunigungSubjektiv = bmin;
		}
		
		if(writing) {
			writer.println("Beschleunigung: " + beschleunigungSubjektiv);
			writer.flush();			
		}
		
		//Unfall
		if(dx < ax) {
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
		if(Math.abs(beschleunigungSubjektiv) < Math.abs(bnull)) {
			beschleunigungSubjektiv = Math.signum(beschleunigungSubjektiv) * bnull;
		}
		
		//Maximale Bremsf�higkeit des Fahrzeuges
		if(beschleunigungSubjektiv < bmin) {
			beschleunigungSubjektiv = bmin;
		}
		
		if(writing) {
			writer.println("Beschleunigung: " + beschleunigungSubjektiv);
			writer.flush();			
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
		
		if(writing) {
			writer.println("Beschleunigung: " + beschleunigung);
			writer.flush();			
		}
		
		Fahrzeug vordermann, vorvordermann;
		try {
			vordermann = f.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug();
			vorvordermann = vordermann.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug();
		} catch (Exception e) {
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
		
		if(writing) {
			writer.println("Beschleunigung: " + beschleunigungWunsch);
			writer.flush();			
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
		if(writing) {
			writer.flush();
			writer.close();			
		}
		f.unfall();
		f.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug().unfall();
		System.out.println("UNFALL");
		System.exit(0);
	}
	
	/**
	 * 
	 * @param beschleunigung die Beschleunigung, die ohne den Tr�deleffekt errechnet wurde
	 * @return Die Beschleunigung unter ber�cksichtigung des Tr�deleffekts
	 */
	private double troedel(double beschleunigung) {
		
		//Wenn der Tr�delvorgang bereits eingeleitet wurde
		if(troedelzeit != 0 && troedelzeit < TROEDELDAUER) {
			//Addiere den Zeitschritt zur Tr�delzeit hinzu
			troedelzeit += Physics.DELTA_TIME;
			//Gebe entweder -BNULL oder die Beschleunigung zur�ck
			double beschleunigungT = Math.min(beschleunigung, -bnull);
			//Wenn ein Bremsman�ver eingeleitet wurde
			if(beschleunigungT == beschleunigung) {
				//Beende den Tr�delvorgang
				troedelzeit = 0;
			}
			
			if(writing)
			writer.println("TR�DELEFFEKT");
			
			return beschleunigungT;
		}
		//Wenn der Tr�delvorgang bereit beendet wurde
		else {
			//Setze die Tr�delzeit zur�ck
			troedelzeit = 0;
		}
		
		//Wenn der Tr�deleffekt zuf�llig eingeleitet werden soll
		double random = Math.random();
		if(random < TROEDELWAHRSCHEINLICHKEIT) {
			//Addiere den Zeitschritt zur Tr�delzeit hinzu
			troedelzeit += Physics.DELTA_TIME;
			//Gebe entweder -BNULL oder die Beschleunigung zur�ck
			double beschleunigungT = Math.min(beschleunigung, -bnull);
			//Wenn ein Bremsman�ver eingeleitet wurde
			if(beschleunigungT == beschleunigung) {
				//Beende den Tr�delvorgang
				troedelzeit = 0;
			}
			if(writing)
			writer.println("TR�DELEFFEKT");
			return beschleunigungT;
			
		}
		//Wenn der Tr�deleffekt nicht eingeleitet werden soll
		else {
			//Gebe die normal gew�hlte Beschleunigung zur�ck
			return beschleunigung;
		}
	}
	
}

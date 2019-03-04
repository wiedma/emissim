package org.Verhalten;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.PhysicEngine.Physics;
import org.Verkehr.Fahrzeug;
import org.Verkehr.Hindernis;
import org.Verkehr.HindernisRichtung;
import org.Verkehr.LKW;
import org.Verkehr.PKW;
import org.main.Simulation;

/**
 * Beschreibt das Verhalten von Fahrzeugen im einspurigen Verkehr nach Wiedemann [WI74] \n
 * im mehrspurigen Verkehr nach Sparmann [SPA78].
 */
public class FahrverhaltenWiedemann extends Fahrverhalten {
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
	
	/**Erweiterung des Wiedemann-Modells um den Faktor Zeitlücke zur reduzierung der gewünschten
	 * Folgeabstände um höhere Verkehrsstärken erreichen zu können 
	 */
	private double zeitluecke;
	
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
	
	/**
	 * Debug-Datei
	 */
	private File file;
	
	private int id;
	
	private static int nummer = 0;
	
	private PrintWriter writer;
	
	private static final boolean writing = false;
	
	/**
	 * Wahrscheinlichkeit, mit der in diesem Zeitschritt der Trödeleffekt eingeleitet wird
	 */
	public static final double TROEDELWAHRSCHEINLICHKEIT = 0.05;
	
	/**
	 * Die Dauer des Trödeleffektes in s
	 */
	public static final double TROEDELDAUER = 2.5;
	
	/**
	 * Die Zeit, die dieses Fahrzeug bereits trödelt
	 */
	private double troedelzeit;
	
	/**
	 * Kontrollvariable für den Spurwechselwunsch nach links. Wird in jedem Zeitschritt, in dem ein
	 * Spurwechselwunsch nach links besteht um die Dauer des Zeitschrittes erhöht. Andernfalls wird sie
	 * um die Dauer des Zeitschrittes verringert. Sie kann Werte zwischen 0s und 2s annehmen.
	 */
	private double spurwechselWunschLinks = 0;
	
	/**
	 * Kontrollvariable für den Spurwechselwunsch nach rechts. Wird in jedem Zeitschritt, in dem ein
	 * Spurwechselwunsch nach rechts besteht um die Dauer des Zeitschrittes erhöht. Andernfalls wird sie
	 * um die Dauer des Zeitschrittes verringert. Sie kann Werte zwischen 0s und 2s annehmen.
	 */
	private double spurwechselWunschRechts = 0;
	
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
		zeitluecke = f.zeitlueckeGeben();
		
		//Berechnung der minimalen gaspedalkontrolle
		bnull = 0.2 * (gaspedalkontrolle + Physics.normalverteilung(0.5, 0.15));
		this.f = f;
		unfall = false;
		
		//Erzeugung der Debug-Log Datei
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
	 * Bestimme die neue Beschleunigung für das Fahrzeug nach dem Fahrzeug-Folgemodell von Wiedemann
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
		bx = ax + ((1 + 7 * sicherheitsbeduerfnis) * Math.sqrt(bxGeschwindigkeit) * zeitluecke);
		
		double cx = 25 * (1 + sicherheitsbeduerfnis + schaetzvermoegen);
		sdv = Math.pow((dx - ax)/cx, 2);
		
		double ex = 2 - schaetzvermoegen + Physics.normalverteilung(0.5, 0.15);
		sdx = ax + ex * (bx - ax);
		
		cldv = sdv * ex * ex;
		
		opdv = cldv * (-1 - 2 * Physics.normalverteilung(0.5, 0.15));
		
		if(writing) {
			writer.println("------------------------------------------------------------------");
			writer.println("Zeit: " + Simulation.zeitGeben());
			writer.println("DX: " + dx);
			writer.println("DV: " +  dv);
			writer.println("AX: " + ax);
			writer.println("BX: " + bx);
//			writer.println("Sicherheit: " + sicherheitsbeduerfnis);
//			writer.println("SDV: " + sdv);
//			writer.println("SDX: " + sdx);
//			writer.println("CLDV: " + cldv);
//			writer.println("OPDV: " + opdv);
//			writer.println("R: " + r);
			writer.println("Geschwindigkeit: " + f.geschwindigkeitGeben());
			writer.println("Geschwindigkeit Vordermann: " + vordermann.geschwindigkeitGeben());
			writer.println("ID Vordermann: " + vordermann.idGeben());
			writer.println("POS: " + f.posGeben());
			writer.println("Spur-Hash: " + f.spurGeben().hashCode());
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

	/**
	 * Bestimme, ob das Fahrzeug seine Spur wechseln soll, oder nicht
	 * @param links Ob der Spurwechsel nach links, oder nach rechts geprüft werden soll
	 * @return Ob der Spurwechsel durchgeführt werden soll, oder nicht
	 */
	@Override
	public boolean spurwechselBestimmen(boolean links) {
		
		if(links) {
			//Wenn ein Spurwechselwunsch nach links besteht
			if(spurwechselWunschLinks()) {
				//Erhöhe die Kontrollvariable
				spurwechselWunschLinks += Physics.DELTA_TIME;
			}
			//Sonst
			else {
				//Verringere die Kontrollvariable
				spurwechselWunschLinks -= Physics.DELTA_TIME;
			}
			//Sorge dafür, dass die Kontrollvariablen im Wertebereich [0;2] bleiben
			spurwechselWunschLinks = Math.min(spurwechselWunschLinks, 2.0);
			spurwechselWunschLinks = Math.max(spurwechselWunschLinks, 0);
			
			if(spurwechselWunschLinks >= 1.0) {
				boolean spurwechsel = spurwechselEntscheidungLinks();
				if(spurwechsel) {
					spurwechselWunschLinks = 0;
					spurwechselWunschRechts = 0;
				}
				return spurwechsel;
			}
		}
		else {
			//Wenn ein Spurwechselwunsch nach rechts besteht
			if(spurwechselWunschRechts()) {
				//Erhöhe die Kontrollvariable
				spurwechselWunschRechts += Physics.DELTA_TIME;
			}
			//Sonst
			else {
				//Verringere die Kontrollvariable
				spurwechselWunschRechts -= Physics.DELTA_TIME;
			}
			//Sorge dafür, dass die Kontrollvariablen im Wertebereich [0;2] bleiben
			spurwechselWunschRechts = Math.min(spurwechselWunschRechts, 2.0);
			spurwechselWunschRechts = Math.max(spurwechselWunschRechts, 0);
			
			if(spurwechselWunschRechts >= 1.0) {
				boolean spurwechsel = spurwechselEntscheidungRechts();
				if(spurwechsel) {
					spurwechselWunschRechts = 0;
					spurwechselWunschLinks = 0;
				}
				return spurwechsel;
			}
		}
		
		return false;
	}
	
	/**
	 * Stellt fest, ob ein Spurwechselwunsch nach links besteht
	 * @return Der Status des Spurwechselwunsches
	 */
	private boolean spurwechselWunschLinks() {
		boolean beeinflussungM = false, beeinflussungV = false;
		
		//Berechnung der beeinflussung durch Fahrzeug M
		double dxM, dvM;
		try {
			dxM = f.hindernisGeben(HindernisRichtung.VORNE).entfernungGeben();
			dvM = f.geschwindigkeitGeben() - f.hindernisGeben(HindernisRichtung.VORNE).geschwindigkeitGeben();
			
			//Grenzabstand für potentielle Beeinflussung
			if(dxM < (ax + bx)) {
				beeinflussungM = true;
			}
			//Grenzgeschwindigkeitsdifferenz für potentielle Beeinflussung
			if(dvM > sdv) {
				beeinflussungM = true;
			}
		}
		catch(Exception e) {
			beeinflussungM = false;
		}
		
		//Berechnung der Beeinflussung durch Fahrzeug V
		double dxV, dvV;
		try {
			dxV = f.hindernisGeben(HindernisRichtung.VORNE_LINKS).entfernungGeben();
			dvV = f.geschwindigkeitGeben() - f.hindernisGeben(HindernisRichtung.VORNE_LINKS).geschwindigkeitGeben();
			Fahrzeug vordermann = f.hindernisGeben(HindernisRichtung.VORNE_LINKS).zielFahrzeug();
			
			//Bestimmung der Wiedemann-Parameter
			double axv = (vordermann.laengeGeben()/2.0) + (f.laengeGeben()/2.0) + 1 + 2 * sicherheitsbeduerfnis;
			double bxvGeschwindigkeit = (f.hindernisGeben(HindernisRichtung.VORNE_LINKS).kollisionszeit() > 0) ? f.geschwindigkeitGeben() : vordermann.geschwindigkeitGeben();
			double bxv = axv + (1 + 7 * sicherheitsbeduerfnis) * Math.sqrt(bxvGeschwindigkeit) * zeitluecke;
			double cx = 25 * (1 + sicherheitsbeduerfnis + schaetzvermoegen);
			double sdvv = Math.pow((dxV - axv)/cx, 2);
			
			//Grenzabstand für potentielle Beeinflussung
			if(dxV < (axv + bxv)) {
				beeinflussungV = true;
			}
			//Grenzgeschwindigkeitsdifferenz für potentielle Beeinflussung
			if(dvV > 0.8 * sdvv) {
				beeinflussungV = true;
			}
			
		}catch(Exception e) {
			beeinflussungV = false;
		}
		
		//Feststellung des Spurwechselwunsches
		if(beeinflussungM) {
			return true;
		}
		else {
			if(beeinflussungV) {
				return true;
			}
			else {
				return false;
			}
		}
		
	}
	
	/**
	 * Stellt fest, ob ein Spurwechselwunsch nach rechts besteht
	 * @return Der Status des Spurwechselwunsches
	 */
	private boolean spurwechselWunschRechts() {
		boolean beeinflussungM = false, beeinflussungV = false, bFaehrtDichtAuf = false;
		
		//Berechnung der beeinflussung durch Fahrzeug M
        double dxM, dvM;
		try {
			dxM = f.hindernisGeben(HindernisRichtung.VORNE).entfernungGeben();
			dvM = f.geschwindigkeitGeben() - f.hindernisGeben(HindernisRichtung.VORNE).geschwindigkeitGeben();
			
			//Grenzabstand für potentielle Beeinflussung
			if(dxM < ax +  (1.5 * bx)) {
				beeinflussungM = true;
			}
			//Grenzgeschwindigkeitsdifferenz für potentielle Beeinflussung
			if(dvM > 0.5 * sdv) {
				beeinflussungM = true;
			}
		}
		catch(Exception e) {
			beeinflussungM = false;
		}
		
		//Berechnung der Beeinflussung durch Fahrzeug V
		double dxV, dvV;
		try {
			dxV = f.hindernisGeben(HindernisRichtung.VORNE_RECHTS).entfernungGeben();
			dvV = f.geschwindigkeitGeben() - f.hindernisGeben(HindernisRichtung.VORNE_RECHTS).geschwindigkeitGeben();
			Fahrzeug vordermann = f.hindernisGeben(HindernisRichtung.VORNE_RECHTS).zielFahrzeug();
			
			//Bestimmung der Wiedemann-Parameter
			double axv = (vordermann.laengeGeben()/2.0) + (f.laengeGeben()/2.0) + 1 + 2 * sicherheitsbeduerfnis;
			double bxvGeschwindigkeit = (f.hindernisGeben(HindernisRichtung.VORNE_RECHTS).kollisionszeit() > 0) ? f.geschwindigkeitGeben() : vordermann.geschwindigkeitGeben();
			double bxv = axv + (1 + 7 * sicherheitsbeduerfnis) * Math.sqrt(bxvGeschwindigkeit) * zeitluecke;
			double cx = 25 * (1 + sicherheitsbeduerfnis + schaetzvermoegen);
			double sdvv = Math.pow((dxV - axv)/cx, 2);
			
			//Grenzabstand für potentielle Beeinflussung
			double sdxp;
			if(f.hindernisGeben(HindernisRichtung.VORNE_RECHTS).zielFahrzeug() instanceof LKW) {
				sdxp = axv + bxv;
			}
			else {
				sdxp = axv + 2 * bxv;
			}
			if(dxV < sdxp) {
				beeinflussungV = true;
			}
			
			//Grenzgeschwindigkeitsdifferenz für potentielle Beeinflussung
			if(dvV > 0.75 * sdvv) {
				beeinflussungV = true;
			}
			
		}catch(Exception e) {
			beeinflussungV = false;
		}
		
		//Festellung, ob das Fahrzeug B dicht auffährt
		double dxB, wunschB;
		try {
			dxB = Math.abs(f.hindernisGeben(HindernisRichtung.HINTEN).entfernungGeben());
			wunschB = f.hindernisGeben(HindernisRichtung.HINTEN).zielFahrzeug().wunschgeschwindigkeitGeben();
			Fahrzeug vordermann = f.hindernisGeben(HindernisRichtung.HINTEN).zielFahrzeug();
			
			//Bestimmung der Wiedemann-Parameter
			double axb = (vordermann.laengeGeben()/2.0) + (f.laengeGeben()/2.0) + 1 + 2 * sicherheitsbeduerfnis;
			double bxbGeschwindigkeit = (f.hindernisGeben(HindernisRichtung.HINTEN).kollisionszeit() > 0) ? vordermann.geschwindigkeitGeben() : f.geschwindigkeitGeben();
			double bxb = axb + (1 + 7 * sicherheitsbeduerfnis) * Math.sqrt(bxbGeschwindigkeit) * zeitluecke;
			
			if(dxB < axb + bxb && wunschB - wunschgeschwindigkeit > (5.0)/3.6) {
				bFaehrtDichtAuf = true;
			}
		} catch(Exception e) {
			bFaehrtDichtAuf = false;
		}
		
		//Feststellung des Spurwechselwunsches
		if(beeinflussungV) {
			return false;
		}
		else {
			if(beeinflussungM) {
				if(bFaehrtDichtAuf) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return true;
			}
		}
	}
	
	/**
	 * Stellt fest, ob ein Spurwechsel nach links gefahrenlos möglich ist
	 * @return Der Status der Spurwechselentscheidung
	 */
	private boolean spurwechselEntscheidungLinks() {
		boolean aktuelleBeeinflussungV = false, potentielleBeeinflussungH = false, aktuelleBeeinflussungH = false;
		
		//Berechnung der aktuellen Beeinflussung durch Fahrzeug V
		double dxV, dvV;
		try {
			dxV = f.hindernisGeben(HindernisRichtung.VORNE_LINKS).entfernungGeben();
			dvV = f.geschwindigkeitGeben() - f.hindernisGeben(HindernisRichtung.VORNE_LINKS).geschwindigkeitGeben();
			Fahrzeug vordermann = f.hindernisGeben(HindernisRichtung.VORNE_LINKS).zielFahrzeug();
			
			if(writing) {
				writer.println("ID Vorne Links: " + vordermann.idGeben());
			}
			
			//Bestimmung der Wiedemann-Parameter
			double axv = (vordermann.laengeGeben()/2.0) + (f.laengeGeben()/2.0) + 1 + 2 * sicherheitsbeduerfnis;
			double bxvGeschwindigkeit = (f.hindernisGeben(HindernisRichtung.VORNE_LINKS).kollisionszeit() > 0) ? f.geschwindigkeitGeben() : vordermann.geschwindigkeitGeben();
			double bxv = axv + (1 + 7 * sicherheitsbeduerfnis) * Math.sqrt(bxvGeschwindigkeit) * zeitluecke;
			double cx = 25 * (1 + sicherheitsbeduerfnis + schaetzvermoegen);
			double sdvv = Math.pow((dxV - axv)/cx, 2);
			
			//Grenzabstand für aktuelle Beeinflussung
			if(dxV < bxv) {
				aktuelleBeeinflussungV = true;
			}
			//Grenzgeschwindigkeitsdifferenz für aktuelle Beeinflussung
			if(dvV > sdvv) {
				aktuelleBeeinflussungV = true;
			}
			
		}catch(Exception e) {
			aktuelleBeeinflussungV = false;
		}
		
		//Bestimmung einer aktuellen oder potentiellen Beeinflussung durch Fahrzeug H
		double dxH, dvH;
		try {
			dxH = Math.abs(f.hindernisGeben(HindernisRichtung.HINTEN_LINKS).entfernungGeben());
			dvH = f.hindernisGeben(HindernisRichtung.HINTEN_LINKS).geschwindigkeitGeben() - f.geschwindigkeitGeben();
			Fahrzeug vordermann = f.hindernisGeben(HindernisRichtung.HINTEN_LINKS).zielFahrzeug();
			
			//Bestimmung der Wiedemann-Parameter
			double axh = (vordermann.laengeGeben()/2.0) + (f.laengeGeben()/2.0) + 1 + 2 * sicherheitsbeduerfnis;
			double bxhGeschwindigkeit = (f.hindernisGeben(HindernisRichtung.HINTEN_LINKS).kollisionszeit() > 0) ? vordermann.geschwindigkeitGeben() : f.geschwindigkeitGeben();
			double bxh = axh + (1 + 7 * sicherheitsbeduerfnis) * Math.sqrt(bxhGeschwindigkeit) * zeitluecke;
			double cx = 25 * (1 + sicherheitsbeduerfnis + schaetzvermoegen);
			double sdvh = Math.pow((dxH - axh)/cx, 2);
			//Grenzabstand und Grenzgeschwindigkeitsdifferenz für potentielle Beeinflussung
			double sdxp, sdvp;
			if(f.hindernisGeben(HindernisRichtung.HINTEN_LINKS).zielFahrzeug() instanceof LKW) {
				sdxp = axh + 3 * bxh;
				sdvp = 0.5 * sdvh;
			}
			else {
				sdxp = axh + 2 * bxh;
				sdvp = 0.8 * sdvh;
			}
			
			//Feststellung der potentiellen, oder aktuellen Beeinflussung
			if(dxH <= sdxp) {
				potentielleBeeinflussungH = true;
			}
			if(dxH <= bxh) {
				aktuelleBeeinflussungH = true;
			}
			
			if(dvH > sdvp) {
				potentielleBeeinflussungH = true;
			}
			if(dvH > sdvh) {
				aktuelleBeeinflussungH = true;
			}
		}catch(Exception e) {
			aktuelleBeeinflussungH = false;
			potentielleBeeinflussungH = false;
		}
		
		//Feststellung der Spurwechselentscheidung
		if(aktuelleBeeinflussungV) {
			return false;
		}
		
		if(!aktuelleBeeinflussungH && !potentielleBeeinflussungH) {
			return true;
		}
		
		if(aktuelleBeeinflussungH) {
			return false;
		}
		
		if(potentielleBeeinflussungH) {
			return wunschgeschwindigkeit - f.geschwindigkeitGeben() > 4;
		}
		
		return false;
	}
	
	/**
	 * Stellt fest, ob ein Spurwechsel nach rechts gefahrenlos möglich ist
	 * @return Der Status der Spurwechselentscheidung
	 */
	private boolean spurwechselEntscheidungRechts() {
		boolean beeinflussungV = false, aktuelleBeeinflussungH = false,
				potentielleBeeinflussungH = false, bFaehrtDichtAuf = false;
		
		//Berechnung der Beeinflussung durch Fahrzeug V
		double dxV, dvV;
		try {
			dxV = f.hindernisGeben(HindernisRichtung.VORNE_RECHTS).entfernungGeben();
			dvV = f.geschwindigkeitGeben() - f.hindernisGeben(HindernisRichtung.VORNE_RECHTS).geschwindigkeitGeben();
			Fahrzeug vordermann = f.hindernisGeben(HindernisRichtung.VORNE_RECHTS).zielFahrzeug();
			
			if(writing) {
				writer.println("ID Vorne Rechts: " + vordermann.idGeben());
			}
			
			//Bestimmung der Wiedemann-Parameter
			double axv = (vordermann.laengeGeben()/2.0) + (f.laengeGeben()/2.0) + 1 + 2 * sicherheitsbeduerfnis;
			double bxvGeschwindigkeit = (f.hindernisGeben(HindernisRichtung.VORNE_RECHTS).kollisionszeit() > 0) ? f.geschwindigkeitGeben() : vordermann.geschwindigkeitGeben();
			double bxv = axv + (1 + 7 * sicherheitsbeduerfnis) * Math.sqrt(bxvGeschwindigkeit) * zeitluecke;
			double cx = 25 * (1 + sicherheitsbeduerfnis + schaetzvermoegen);
			double sdvv = Math.pow((dxV - axv)/cx, 2);
			
			//Grenzabstand für potentielle Beeinflussung
			double sdxp;
			if(f.hindernisGeben(HindernisRichtung.VORNE_RECHTS).zielFahrzeug() instanceof LKW) {
				sdxp = axv + bxv;
			}
			else {
				sdxp = axv + 2 * bxv;
			}
			
			if(dxV < sdxp) {
				beeinflussungV = true;
			}
			
			//Grenzgeschwindigkeitsdifferenz für potentielle Beeinflussung
			if(dvV > 0.75 * sdvv) {
				beeinflussungV = true;
			}
			
		}catch(Exception e) {
			beeinflussungV = false;
		}
		
		
		//Bestimmung einer aktuellen oder potentiellen Beeinflussung durch Fahrzeug H
		double dxH, dvH;
		try {
			dxH = Math.abs(f.hindernisGeben(HindernisRichtung.HINTEN_RECHTS).entfernungGeben());
			dvH = f.hindernisGeben(HindernisRichtung.HINTEN_RECHTS).geschwindigkeitGeben() - f.geschwindigkeitGeben();
			Fahrzeug vordermann = f.hindernisGeben(HindernisRichtung.HINTEN_RECHTS).zielFahrzeug();
			
			//Bestimmung der Wiedemann-Parameter
			double axh = (vordermann.laengeGeben()/2.0) + (f.laengeGeben()/2.0) + 1 + 2 * sicherheitsbeduerfnis;
			double bxhGeschwindigkeit = (f.hindernisGeben(HindernisRichtung.HINTEN_RECHTS).kollisionszeit() > 0) ? vordermann.geschwindigkeitGeben() : f.geschwindigkeitGeben();
			double bxh = axh + (1 + 7 * sicherheitsbeduerfnis) * Math.sqrt(bxhGeschwindigkeit) * zeitluecke;
			double cx = 25 * (1 + sicherheitsbeduerfnis + schaetzvermoegen);
			double sdvh = Math.pow((dxH - axh)/cx, 2);
			//Grenzabstand und Grenzgeschwindigkeitsdifferenz für potentielle Beeinflussung
			double sdxp, sdvp;
			if(f.hindernisGeben(HindernisRichtung.HINTEN_RECHTS).zielFahrzeug() instanceof LKW) {
				sdxp = axh + 1.0 * bxh;
				sdvp = 0.3 * sdvh;
			}
			else {
				sdxp = axh + 1.2 * bxh;
				sdvp = 0.3 * sdvh;
			}
			
			//Feststellung der potentiellen, oder aktuellen Beeinflussung
			if(dxH <= sdxp) {
				potentielleBeeinflussungH = true;
			}
			if(dxH <= bxh) {
				aktuelleBeeinflussungH = true;
			}
			
			if(dvH > sdvp) {
				potentielleBeeinflussungH = true;
			}
			if(dvH > sdvh) {
				aktuelleBeeinflussungH = true;
			}
		}catch(Exception e) {
			aktuelleBeeinflussungH = false;
			potentielleBeeinflussungH = false;
		}
		
		//Festellung, ob das Fahrzeug B dicht auffährt
		double dxB, wunschB;
		try {
			dxB = Math.abs(f.hindernisGeben(HindernisRichtung.HINTEN).entfernungGeben());
			wunschB = f.hindernisGeben(HindernisRichtung.HINTEN).zielFahrzeug().wunschgeschwindigkeitGeben();
			Fahrzeug vordermann = f.hindernisGeben(HindernisRichtung.HINTEN).zielFahrzeug();
			
			//Bestimmung der Wiedemann-Parameter
			double axb = (vordermann.laengeGeben()/2.0) + (f.laengeGeben()/2.0) + 1 + 2 * sicherheitsbeduerfnis;
			double bxbGeschwindigkeit = (f.hindernisGeben(HindernisRichtung.HINTEN).kollisionszeit() > 0) ? vordermann.geschwindigkeitGeben() : f.geschwindigkeitGeben();
			double bxb = axb + (1 + 7 * sicherheitsbeduerfnis) * Math.sqrt(bxbGeschwindigkeit) * zeitluecke;

			if(dxB < axb + bxb && wunschB - wunschgeschwindigkeit > (5.0)/3.6) {
				bFaehrtDichtAuf = true;
			}
		} catch(Exception e) {
			bFaehrtDichtAuf = false;
		}
		
		//Bestimmung der Spurwechselentscheidung
		if(beeinflussungV) {
			return false;
		}
		
		if(!potentielleBeeinflussungH && !aktuelleBeeinflussungH) {
			return true;
		}
		
		if(aktuelleBeeinflussungH) {
			return false;
		}
		
		if(potentielleBeeinflussungH) {
			return wunschgeschwindigkeit - f.geschwindigkeitGeben() < 2.0 && bFaehrtDichtAuf;
		}
		
		return false;
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
		
		//Maximale Bremsfähigkeit des Fahrzeuges
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
		
		//Maximale Bremsfähigkeit des Fahrzeuges
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
		
		//Wunschbeschleunigung führt zur Wunschgeschwindigkeit innnerhalb dieser Zeiteinheit
		double beschleunigungWunsch = (wunschgeschwindigkeit - f.geschwindigkeitGeben())/Physics.DELTA_TIME;
		
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
		Simulation.reset(false);
	}
	
	/**
	 * 
	 * @param beschleunigung die Beschleunigung, die ohne den Trödeleffekt errechnet wurde
	 * @return Die Beschleunigung unter berücksichtigung des Trödeleffekts
	 */
	private double troedel(double beschleunigung) {
		
		//Wenn der Trödelvorgang bereits eingeleitet wurde
		if(troedelzeit != 0 && troedelzeit < TROEDELDAUER) {
			//Addiere den Zeitschritt zur Trödelzeit hinzu
			troedelzeit += Physics.DELTA_TIME;
			//Gebe entweder -BNULL oder die Beschleunigung zurück
			double beschleunigungT = Math.min(beschleunigung, -bnull);
			//Wenn ein Bremsmanöver eingeleitet wurde
			if(beschleunigungT == beschleunigung) {
				//Beende den Trödelvorgang
				troedelzeit = 0;
			}
			
			if(writing)
			writer.println("TRÖDELEFFEKT");
			
			return beschleunigungT;
		}
		//Wenn der Trödelvorgang bereit beendet wurde
		else {
			//Setze die Trödelzeit zurück
			troedelzeit = 0;
		}
		
		//Wenn der Trödeleffekt zufällig eingeleitet werden soll
		double random = Math.random();
		if(random < TROEDELWAHRSCHEINLICHKEIT) {
			//Addiere den Zeitschritt zur Trödelzeit hinzu
			troedelzeit += Physics.DELTA_TIME;
			//Gebe entweder -BNULL oder die Beschleunigung zurück
			double beschleunigungT = Math.min(beschleunigung, -bnull);
			//Wenn ein Bremsmanöver eingeleitet wurde
			if(beschleunigungT == beschleunigung) {
				//Beende den Trödelvorgang
				troedelzeit = 0;
			}
			if(writing)
			writer.println("TRÖDELEFFEKT");
			return beschleunigungT;
			
		}
		//Wenn der Trödeleffekt nicht eingeleitet werden soll
		else {
			//Gebe die normal gewählte Beschleunigung zurück
			return beschleunigung;
		}
	}
	
	public double wunschgeschwindigkeitGeben() {
		return wunschgeschwindigkeit;
	}
	
	public int idGeben() {
		return id;
	}
	
}

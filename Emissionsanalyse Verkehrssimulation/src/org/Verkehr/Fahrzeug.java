package org.Verkehr;
import org.PhysicEngine.*;
import org.Streckennetz.Fahrspur;
import org.Streckennetz.Strecke;
import org.Verhalten.Fahrverhalten;
import org.Verhalten.FahrverhaltenWiedemann;

public abstract class Fahrzeug {
	
	/**Räumliche Dimensionen des Fahrzeugs in m bzw. m²*/
	protected double laenge, breite, hoehe, frontflaeche;
	
	/**Masse des Fahrzeugs in kg*/
	protected double masse;
	
	/**Verwendeter Kraftstoff*/
	protected Kraftstoffe kraftstoff;
	
	/**Wirkungsgrad des Motors*/
	protected double wirkungsgrad;
	
	/**Kennzahlen des Fahrzeugs ohne Einheit*/
	protected double rollreibungszahl, luftreibungszahl;
	
	/**Position als Koordinate im eindimensionalen System*/
	protected double pos;
	/**Die Fahrspur auf der sich das Fahrzeug befindet*/
	protected Fahrspur spur;
	
	/**Momentane Geschwindigkeit in m/s*/
	protected double geschwindigkeit;
	
	/**Momentan gewählter Gang*/
	protected int gang;
	
	/**Momentane Beschleunigung*/
	protected double beschleunigung;
	
	/**CO2-Sensorobjekt des Fahrzeugs*/
	protected CO2Sensor co2sensor;
	
	/**Geschwindigkeits-Messgerät des Fahrzeugs*/
	protected GeschwindigkeitsSensor vSensor;
	
	/**Strecke, die diesem Fahrzeug zugewiesen wurde*/
	protected Strecke strecke;
	
	/**Die Hindernisse in allen 6 Richtungen*/
	protected Hindernis hinVorne, hinHinten, hinVorneLinks, hinVorneRechts, hinHintenLinks, hinHintenRechts;
	/**Das Konkrete Fahrverhalten dieses Fahrzeuges*/
	protected Fahrverhalten verhalten;
	/**Beschreibt, ob das Fahrzeug in einem Unfall verwickelt ist und deshalb stehen bleiben soll*/
	protected boolean unfall;
	
	/**
	 * Die Wunschgeschwindigkeit des Fahrzeuges
	 */
	protected double wunschgeschwindigkeit;
	
	/**
	 * Das Sicherheitsbedürfnis des Fahrers als (0.5, 0.15)-normalverteilte Zufallszahl
	 */
	protected double sicherheitsbeduerfnis;
	
	/**
	 * Das Schätzvermögen des Fahrers als (0.5, 0.15)-normalverteilte Zufallszahl
	 */
	protected double schaetzvermoegen;
	
	/**
	 * Der Beschleunigungswille des Fahrers als (0.5, 0.15)-normalverteilte Zufallszahl
	 */
	protected double beschleunigungswille;
	
	/**
	 * Die Fähigkeit des Fahrers sein Gaspedal zu kontrollieren als (0.5, 0.15)-normalverteilte Zufallszahl
	 */
	protected double gaspedalkontrolle;
	
	/**Erweiterung des Wiedemann-Modells um den Faktor Zeitlücke zur reduzierung der gewünschten
	 * Folgeabstände um höhere Verkehrsstärken erreichen zu können 
	 */
	protected double zeitluecke;
	
	
	public Fahrzeug() {
		double[] specs = generiereFahrzeugSpecs();
		
		//Räumliche Ausdehnung
		laenge = specs[0];
		breite = specs[1];
		hoehe = specs[2];
		frontflaeche = breite * hoehe;
		
		//Masse des Fahrzeugs
		masse = specs[3];
		
		//Verwendeter Kraftstoff
		switch((int) specs[4]) {
		case 0: kraftstoff = Kraftstoffe.BENZIN;
				wirkungsgrad = Kraftstoffe.BENZIN.generiereWirkungsgrad(); break;
		case 1: kraftstoff = Kraftstoffe.DIESEL;
				wirkungsgrad = Kraftstoffe.DIESEL.generiereWirkungsgrad(); break;
		default: kraftstoff = Kraftstoffe.BENZIN;
				wirkungsgrad = Kraftstoffe.BENZIN.generiereWirkungsgrad(); break;
		}
		
		//Reibungskonstanten
		rollreibungszahl = specs[5];
		luftreibungszahl = specs[6];
		
		//Positionsstuff
		pos = 0;
		geschwindigkeit = 0;
		gang = 0;
		beschleunigung = 0;
		
		//CO2-Sensorobjekt
		if(!(this instanceof DummyFahrzeug)) {
			co2sensor = new CO2Sensor();
			vSensor = new GeschwindigkeitsSensor();
		}
		
		unfall = false;	
		
		do {
			sicherheitsbeduerfnis = Physics.normalverteilung(0.5, 0.15);
		} while (sicherheitsbeduerfnis < 0.05 || sicherheitsbeduerfnis > 0.95);
		
		do {
			schaetzvermoegen = Physics.normalverteilung(0.5, 0.15);
		} while (schaetzvermoegen < 0.05 || schaetzvermoegen > 0.95);
		
		do {
			beschleunigungswille = Physics.normalverteilung(0.5, 0.15);
		} while (beschleunigungswille < 0.05 || beschleunigungswille > 0.95);
		
		do {
			gaspedalkontrolle = Physics.normalverteilung(0.5, 0.15);
		} while (gaspedalkontrolle < 0.05 || gaspedalkontrolle > 0.95);
		
		//Erzeugung der Zeitlücke als 0.5 - 1 gleichverteilte Variable
		zeitluecke = (Math.random() * 0.5) + 0.5;
//		zeitluecke = 1;
	}
	
	/**Bestimmt die Beschleunigung dieses Fahrzeuges für den nächsten Zeitschritt*/
	public void beschleunigungBestimmen() {
		
		//Wenn ein Unfall passiert ist
		if(unfall) {
			//Bleibe stehen
			unfall();
			return;
		}
		
		//Bestimme die neue Beschleunigung des Fahrzeuges
		beschleunigung = verhalten.beschleunigungBestimmen();
	}
	
	/**Treffe eine Spurwechselentscheidung*/
	public void spurwechselBestimmen() {
		//Treffe eine Spurwechselentscheidung
		boolean spurwechselLinks = false, spurwechselRechts = false;
		
		if(!spur.ueberholverbotGeben()) {
			if(spur.hatLinkenNachbarn()) {
				hinVorneLinks = hindernisSuchen(HindernisRichtung.VORNE_LINKS);
				hinHintenLinks = hindernisSuchen(HindernisRichtung.HINTEN_LINKS);
				spurwechselLinks = verhalten.spurwechselBestimmen(true);
			}
			
			if(spur.hatRechtenNachbarn()) {
				hinVorneRechts = hindernisSuchen(HindernisRichtung.VORNE_RECHTS);
				hinHintenRechts = hindernisSuchen(HindernisRichtung.HINTEN_RECHTS);
				spurwechselRechts = verhalten.spurwechselBestimmen(false);			
			}	
		}
		
		if(spurwechselLinks) {
			spur.spurwechsel(this, true);
		}
		else if(spurwechselRechts) {
			spur.spurwechsel(this, false);
		}
	}
	
	/**Berechne die neue Position und Geschwindigkeit aus den momentanen Attributen*/
	public void zeitschritt() {
		
		//Neue Position
		pos = Physics.bewege(pos, geschwindigkeit, beschleunigung);
		
		//Neue Geschwindigkeit
		geschwindigkeit = geschwindigkeit + (beschleunigung * Physics.DELTA_TIME);
		//Neuer Gang
		gang = schalten(geschwindigkeit);
		
		//Emissionsdaten sammeln und speichern
		co2sensor.schreibeDaten(emissionBerechnen());
		
		//Geschwindigkeitsdaten Sammeln
		vSensor.schreibeDaten(geschwindigkeit);
		
		//Setze die Hindernisse wieder auf null
		hinVorne = null;
		hinHinten = null;
		hinVorneRechts = null;
		hinHintenRechts = null;
		hinVorneLinks = null;
		hinHintenLinks = null;
		
	}
	
	public void unfall() {
		this.geschwindigkeit = 0;
		this.beschleunigung = 0;
		unfall = true;
		System.out.println("Unfall " + ((FahrverhaltenWiedemann) verhalten).idGeben());
	}
	
	//Wählt für eine gewisse Geschwindigkeit nach Faustregel einen passenden Gang
	public int schalten(double geschwindigkeit) {
		if(geschwindigkeit < (5.0/3.6)) {
			return 1;
		}
		else if(geschwindigkeit < (25/3.6)) {
			return 2;
		}
		else if(geschwindigkeit < (40/3.6)) {
			return 3;
		}
		else if(geschwindigkeit < (50/3.6)) {
			return 4;
		}
		else {
			return 5;
		}
	}
	
	/**Berechne die CO₂-Emission des Fahrzeugs in dieser Zeiteinheit in kg*/
	public double emissionBerechnen() {
		//Die gefahrene Strecke durch Bewegungsgleichungen berechnen
		double strecke = Math.abs(Physics.bewege(0, geschwindigkeit, beschleunigung));
		//benötigte Energie berechnet sich aus der Summe der Fahrtwiderstandskräfte und der Strecke
		double benoetigteEnergie = strecke * (Physics.rollreibung(rollreibungszahl, masse)
				+ Physics.luftreibung(luftreibungszahl, frontflaeche, geschwindigkeit)
				+ Physics.beschleunigungswiderstand(masse, beschleunigung, gang));
		//Der tatsächliche Energieoutput des Motors wird über den Wirkungsgrad bestimmt
		double motorOutput = (1/wirkungsgrad) * benoetigteEnergie;
		//Die dazugehörige Emission hängt vom verwendeten Kraftstoff ab
		return kraftstoff.verbrenne(motorOutput);
	}
	
	/**Suche ein Hindernis in der angegebenen Richtung*/
	public Hindernis hindernisSuchen(HindernisRichtung richtung) {
		switch(richtung) {
		case VORNE: return spur.hindernisVorne(this, 0);
		case HINTEN: return spur.hindernisHinten(this, 0);
		case VORNE_RECHTS: return spur.hindernisRechts(this, 0, true);
		case VORNE_LINKS: return spur.hindernisLinks(this, 0, true);
		case HINTEN_RECHTS: return spur.hindernisRechts(this, 0, false);
		case HINTEN_LINKS: return spur.hindernisLinks(this, 0, false);
		default: return null;
		}
	}
	
	/**Suche nach Hindernis in allen Richtungen*/
	public void alleHindernisseSuchen() {
		if(hinVorne == null) {
			hinVorne = hindernisSuchen(HindernisRichtung.VORNE);
		}
		if(hinHinten == null) {
			hinHinten = hindernisSuchen(HindernisRichtung.HINTEN);
		}
		if(hinVorneLinks == null) {
			hinVorneLinks = hindernisSuchen(HindernisRichtung.VORNE_LINKS);
		}
		if(hinVorneRechts == null) {
			hinVorneRechts = hindernisSuchen(HindernisRichtung.VORNE_RECHTS);
		}
		if(hinHintenLinks == null) {
			hinHintenLinks = hindernisSuchen(HindernisRichtung.HINTEN_LINKS);
		}
		if(hinHintenRechts == null) {
			hinHintenRechts = hindernisSuchen(HindernisRichtung.HINTEN_RECHTS);
		}
//		hinVorne = hindernisSuchen(HindernisRichtung.VORNE);
//		hinHinten = hindernisSuchen(HindernisRichtung.HINTEN);
//		hinVorneLinks = hindernisSuchen(HindernisRichtung.VORNE_LINKS);
//		hinVorneRechts = hindernisSuchen(HindernisRichtung.VORNE_RECHTS);
//		hinHintenLinks = hindernisSuchen(HindernisRichtung.HINTEN_LINKS);
//		hinHintenRechts = hindernisSuchen(HindernisRichtung.HINTEN_RECHTS);
	}
	
	/**Generiere zufällig die Spezifikationen des Fahrzeuges*/
	protected abstract double[] generiereFahrzeugSpecs();
	
//Getter und Setter -----------------------------------------------------------------------------
	
	public double laengeGeben() {
		return laenge;
	}
	
	public double wirkungsgradGeben() {
		return wirkungsgrad;
	}
	
	public double posGeben() {
		return pos;
	}
	
	public void posSetzen(double pos) {
		this.pos = pos;
	}
	
	public Fahrspur spurGeben() {
		return this.spur;
	}
	
	public void spurSetzen(Fahrspur spur) {
		this.spur = spur;
	}
	
	public void verhaltenSetzen(Fahrverhalten verhalten) {
		this.verhalten = verhalten;
	}
	
	public double geschwindigkeitGeben() {
		return geschwindigkeit;
	}
	
	public void geschwindigkeitSetzen(double geschwindigkeit) {
		this.geschwindigkeit = geschwindigkeit;
	}
	
	public double beschleunigungGeben() {
		return beschleunigung;
	}
	
	public void streckeSetzen(Strecke strecke) {
		this.strecke = strecke;
	}
	
	public double wunschgeschwindigkeitGeben() {
		return wunschgeschwindigkeit;
	}

	public double sicherheitsbeduerfnisGeben() {
		return sicherheitsbeduerfnis;
	}

	public double schaetzvermoegenGeben() {
		return schaetzvermoegen;
	}

	public double beschleunigungswilleGeben() {
		return beschleunigungswille;
	}

	public double gaspedalkontrolleGeben() {
		return gaspedalkontrolle;
	}
	
	public double zeitlueckeGeben() {
		return zeitluecke;
	}

	public void sensorAktivieren() {
		co2sensor.aktiviere();
		vSensor.aktviere();
	}
	
	public void hindernisSetzen(Hindernis hindernis, HindernisRichtung richtung) {
		switch(richtung) {
		case VORNE: hinVorne = hindernis;
			break;
		case HINTEN: hinHinten = hindernis;
			break;
		case VORNE_LINKS: hinVorneLinks = hindernis;
			break;
		case VORNE_RECHTS: hinVorneRechts = hindernis;
			break;
		case HINTEN_LINKS: hinHintenLinks = hindernis;
			break;
		case HINTEN_RECHTS: hinHintenRechts = hindernis;
			break;
		}
	}
	
	public Hindernis hindernisGeben(HindernisRichtung richtung) {
		switch(richtung) {
		case VORNE: return hinVorne;
		case HINTEN: return hinHinten;
		case VORNE_LINKS: return hinVorneLinks;
		case VORNE_RECHTS: return hinVorneRechts;
		case HINTEN_LINKS: return hinHintenLinks;
		case HINTEN_RECHTS: return hinHintenRechts;
		default: return null;
		}
	}
	
	public void tempolimitAktualisieren(double tempolimitNeu) {
		wunschgeschwindigkeit = verhalten.tempolimitAktualisieren(tempolimitNeu);
	}
	
	public int idGeben() {
		return ((FahrverhaltenWiedemann) verhalten).idGeben();
	}
	
	public double mittlereGeschwindigkeitGeben() {
		return vSensor.mittlereGeschwindigkeit();
	}
//------------------------------------------------------------------------------------------------
}

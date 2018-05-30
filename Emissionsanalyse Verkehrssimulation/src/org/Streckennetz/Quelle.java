package org.Streckennetz;

public class Quelle extends Fahrspur {
	//NEXT Fahrzeuggeneration in den Quellen
	
	private double[] letzteZeit;
	private double[] zeitluecke;
	private int[] rueckstau;
	private Vorlauf[] vorlaeufe;
	
	
	public Quelle(Fahrspur einfahrt){
		//Rufe den Konstruktor der Oberklasse auf
		super(0,0,0,false);
		//Initialisiere die Felder mit Länge 1
		letzteZeit = new double[1];
		zeitluecke = new double[1];
		rueckstau = new int[1];
		vorlaeufe = new Vorlauf[1];
		//Erstelle den Vorlauf mit Länge 1000m (vgl. [ER07] S.37f)
		vorlaeufe[0] = new Vorlauf(1000);
		//Verbinde den Vorlauf mit seinem Nachfolger
		Fahrspur.verbinde(vorlaeufe[0], einfahrt);
	}
	
	public Quelle(Mehrspurbereich einfahrten) {
		
		//Rufe den Konstruktor der Oberklasse auf
		super(0,0,0,false);
		
		//Initialisiere die Felder mit der Länge der Anzahl an Fahrspuren im Mehrspurbereich
		int anzahl = einfahrten.anzahlGeben();
		letzteZeit = new double[anzahl];
		zeitluecke = new double[anzahl];
		rueckstau = new int[anzahl];
		vorlaeufe = new Vorlauf[anzahl];
		
		/*Erstelle die Vorläufe mit einer Länge proportional zur Anzahl der angeschlossenen
		* Fahrspuren */
		double vorlaufLaenge = Math.max(1000, anzahl * 500);
		for(int i = 0; i < anzahl; i++) {
			vorlaeufe[i] = new Vorlauf(vorlaufLaenge);
			//Verbinde die Vorläufe mit ihren Einfahrten
			Fahrspur.verbinde(vorlaeufe[i], einfahrten.fahrspurGeben(i));
		}
		
		//Fasse die Vorläufe in einem Mehrspurbereich zusammen
		Mehrspurbereich vorlaufBereich = new Mehrspurbereich(vorlaeufe[0]);
		for(int i = 1; i < anzahl; i++) {
			vorlaufBereich.fahrspurHinzufuegen(vorlaeufe[i], vorlaeufe[i-1], true);
		}
		
	}

}

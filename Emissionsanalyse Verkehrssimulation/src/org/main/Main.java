package org.main;

import org.Streckennetz.*;
/**Main Klasse zum starten des Projekts*/
public class Main {
	
	public static final double tempolimit = 200;

	public static void main(String[] args) {
		beispiel10km();
	}
	
	/**Experiment für die Zwischenpräsentation*/
	public static void beispiel10km() {
		Gerade gerade1 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade2 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade3 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade4 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade5 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade6 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade7 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade8 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade9 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade10 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade11 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade12 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade13 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade14 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade15 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade16 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade17 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade18 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade19 = new Gerade(1000, 2, tempolimit, false);
		Gerade gerade20 = new Gerade(1000, 2, tempolimit, false);
		
		
		Senke autobahnEnde = new Senke();
		
		Fahrspur.verbinde(gerade1, gerade2);
		Fahrspur.verbinde(gerade2, gerade3);
		Fahrspur.verbinde(gerade3, gerade4);
		Fahrspur.verbinde(gerade4, gerade5);
		Fahrspur.verbinde(gerade5, gerade6);
		Fahrspur.verbinde(gerade6, gerade7);
		Fahrspur.verbinde(gerade7, gerade8);
		Fahrspur.verbinde(gerade8, gerade9);
		Fahrspur.verbinde(gerade9, gerade10);
		Fahrspur.verbinde(gerade10, autobahnEnde);
		
		Fahrspur.verbinde(gerade11, gerade12);
		Fahrspur.verbinde(gerade12, gerade13);
		Fahrspur.verbinde(gerade13, gerade14);
		Fahrspur.verbinde(gerade14, gerade15);
		Fahrspur.verbinde(gerade15, gerade16);
		Fahrspur.verbinde(gerade16, gerade17);
		Fahrspur.verbinde(gerade17, gerade18);
		Fahrspur.verbinde(gerade18, gerade19);
		Fahrspur.verbinde(gerade19, gerade20);
		Fahrspur.verbinde(gerade20, autobahnEnde);
		
		Mehrspurbereich autobahn1 = new Mehrspurbereich(gerade1);
		autobahn1.fahrspurHinzufuegen(gerade11, gerade1, true);
		Mehrspurbereich autobahn2 = new Mehrspurbereich(gerade2);
		autobahn2.fahrspurHinzufuegen(gerade12, gerade2, true);
		Mehrspurbereich autobahn3 = new Mehrspurbereich(gerade3);
		autobahn3.fahrspurHinzufuegen(gerade13, gerade3, true);
		Mehrspurbereich autobahn4 = new Mehrspurbereich(gerade4);
		autobahn4.fahrspurHinzufuegen(gerade14, gerade4, true);
		Mehrspurbereich autobahn5 = new Mehrspurbereich(gerade5);
		autobahn5.fahrspurHinzufuegen(gerade15, gerade5, true);
		Mehrspurbereich autobahn6 = new Mehrspurbereich(gerade6);
		autobahn6.fahrspurHinzufuegen(gerade16, gerade6, true);
		Mehrspurbereich autobahn7 = new Mehrspurbereich(gerade7);
		autobahn7.fahrspurHinzufuegen(gerade17, gerade7, true);
		Mehrspurbereich autobahn8 = new Mehrspurbereich(gerade8);
		autobahn8.fahrspurHinzufuegen(gerade18, gerade8, true);
		Mehrspurbereich autobahn9 = new Mehrspurbereich(gerade9);
		autobahn9.fahrspurHinzufuegen(gerade19, gerade9, true);
		Mehrspurbereich autobahn10 = new Mehrspurbereich(gerade10);
		autobahn10.fahrspurHinzufuegen(gerade20, gerade10, true);
		
		Quelle autobahnBeginn = new Quelle(autobahn1, 0.1, tempolimit);
		
		Fahrspur[] spuren = new Fahrspur[22];
		
		spuren[0] = gerade1;
		spuren[1] = gerade2;
		spuren[2] = gerade3;
		spuren[3] = gerade4;
		spuren[4] = gerade5;
		spuren[5] = gerade6;
		spuren[6] = gerade7;
		spuren[7] = gerade8;
		spuren[8] = gerade9;
		spuren[9] = gerade10;
		spuren[10] = gerade11;
		spuren[11] = gerade12;
		spuren[12] = gerade13;
		spuren[13] = gerade14;
		spuren[14] = gerade15;
		spuren[15] = gerade16;
		spuren[16] = gerade17;
		spuren[17] = gerade18;
		spuren[18] = gerade19;
		spuren[19] = gerade20;
		spuren[20] = autobahnBeginn;
		spuren[21] = autobahnEnde;
		
		Netz netz = new Netz(spuren);
		autobahnBeginn.streckeGeben(0).verkehrsstaerkeSetzen(2000);
		netz.tempolimitSetzen(tempolimit);
		
		Simulation.netzSetzen(netz);
		
		
		while(netz.anzahlFahrzeuge() > 0 || Senke.anzahlFahrzeugeEntfernt() == 0) {
			Simulation.zeitschritt();
			System.out.println("GESAMT: " + Quelle.fahrzeugeErzeugt());
			if(Quelle.fahrzeugeErzeugt() >= 500) {
				autobahnBeginn.aktivSetzen(false);
			}
		}
		
		Simulation.beenden();
		
	}

}

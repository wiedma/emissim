package org.main;

import org.Streckennetz.*;

public class Main {

	public static void main(String[] args) {
		
		Gerade gerade1 = new Gerade(100, 2, 120, false);
		Gerade gerade2 = new Gerade(100, 2, 120, false);
		Gerade ausfahrt = new Gerade(10, 2, 100, false);
		Gerade weiter = new Gerade (100, 2, 120, false);
		
		Fahrspur.verbinde(gerade1, ausfahrt);
		Fahrspur.verbinde(gerade2, weiter);
		
		Mehrspurbereich autobahn = new Mehrspurbereich(gerade1);
		autobahn.fahrspurHinzufuegen(gerade2, gerade1, true);
		
		Quelle autobahnBeginn = new Quelle(autobahn, 0.1);
		
		Senke ausfahrtSenke = new Senke();
		Senke autobahnEnde = new Senke();
		
		Fahrspur.verbinde(ausfahrt, ausfahrtSenke);
		Fahrspur.verbinde(weiter, autobahnEnde);
		
		Fahrspur[] spuren = new Fahrspur[7];
		
		spuren[0] = gerade1;
		spuren[1] = gerade2;
		spuren[2] = weiter;
		spuren[3] = ausfahrt;
		spuren[4] = autobahnBeginn;
		spuren[5] = autobahnEnde;
		spuren[6] = ausfahrtSenke;
		
		Netz netz = new Netz(spuren);
		
		Routenplaner.planeRouten(netz);
	}

}

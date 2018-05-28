package org.main;

import org.Streckennetz.*;

public class Main {

	public static void main(String[] args) {
		Quelle quelle1 = new Quelle();
		Quelle quelle2 = new Quelle();
		
		Gerade gerade1 = new Gerade(10, 2, 200, false);
		Gerade einfahrt = new Gerade(10, 2, 200, false);
		
		Gerade ausfahrt1 = new Gerade(100, 2, 200, false);
		Gerade ausfahrt2 = new Gerade(100, 2, 200, false);
		
		Senke senke1 = new Senke();
		Senke senke2 = new Senke();
		
		Fahrspur.verbinde(quelle1, gerade1);
		Fahrspur.verbinde(quelle2, einfahrt);
		
		Fahrspur.verbinde(gerade1, ausfahrt1);
		Fahrspur.verbinde(einfahrt, ausfahrt2);
		
		Mehrspurbereich mehr1 = new Mehrspurbereich(ausfahrt1);
		mehr1.fahrspurHinzufuegen(ausfahrt2, ausfahrt1, false);
		
		Fahrspur.verbinde(ausfahrt1, senke1);
		Fahrspur.verbinde(ausfahrt2, senke2);
		
		Fahrspur[] spuren = new Fahrspur[8];
		
		spuren[0] = gerade1;
		spuren[1] = einfahrt;
		spuren[2] = ausfahrt1;
		spuren[3] = ausfahrt2;
		spuren[4] = quelle1;
		spuren[5] = quelle2;
		spuren[6] = senke1;
		spuren[7] = senke2;
		
		Netz netz = new Netz(spuren);
		
		Routenplaner.planeRouten(netz);
	}

}

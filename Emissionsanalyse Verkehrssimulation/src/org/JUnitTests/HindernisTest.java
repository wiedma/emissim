package org.JUnitTests;

import static org.junit.jupiter.api.Assertions.*;
import org.Streckennetz.Fahrspur;
import org.Streckennetz.Gerade;
import org.Streckennetz.Mehrspurbereich;
import org.Streckennetz.Quelle;
import org.Streckennetz.Senke;
import org.Verkehr.HindernisRichtung;
import org.Verkehr.PKW;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**JUnit Testcase für die Hinderniserkennung*/
class HindernisTest {
	//Szenario bei dem die Hindernissuche mit Spiegelung geprüft wird
	static Gerade gerade1, gerade2, gerade3;
	static PKW vorne, vorneLinks, vorneRechts, hinten, hintenLinks, hintenRechts, messung;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		gerade1 = new Gerade(1000, 2, 120, false);
		gerade2 = new Gerade(1000, 2, 120, false);
		gerade3 = new Gerade(1000, 2, 120, false);
		
		Mehrspurbereich mehr = new Mehrspurbereich(gerade1);
		mehr.fahrspurHinzufuegen(gerade2, gerade1, true);
		mehr.fahrspurHinzufuegen(gerade3, gerade2, true);
		
		vorneLinks = new PKW();
		vorneRechts = new PKW();
		vorne = new PKW();
		messung = new PKW();
		hintenLinks = new PKW();
		hintenRechts = new PKW();
		hinten = new PKW();
		
		hintenLinks.posSetzen(0);
		hintenLinks.spurSetzen(gerade3);
		gerade3.fahrzeugHinzufuegen(hintenLinks);
		
		hintenRechts.posSetzen(10);
		hintenRechts.spurSetzen(gerade1);
		gerade1.fahrzeugHinzufuegen(hintenRechts);
		
		hinten.posSetzen(20);
		hinten.spurSetzen(gerade2);
		gerade2.fahrzeugHinzufuegen(hinten);
		
		messung.posSetzen(30);
		messung.spurSetzen(gerade2);
		gerade2.fahrzeugHinzufuegen(messung);
		
		vorneLinks.posSetzen(40);
		vorneLinks.spurSetzen(gerade3);
		gerade3.fahrzeugHinzufuegen(vorneLinks);
		
		vorneRechts.posSetzen(50);
		vorneRechts.spurSetzen(gerade1);
		gerade1.fahrzeugHinzufuegen(vorneRechts);
		
		vorne.posSetzen(60);
		vorne.spurSetzen(gerade2);
		gerade2.fahrzeugHinzufuegen(vorne);
		
		Senke senke = new Senke();
		@SuppressWarnings("unused")
		Quelle quelle = new Quelle(mehr, 0, 200);
		
		Fahrspur.verbinde(gerade1, senke);
		
		Fahrspur.verbinde(gerade2, senke);
		
		Fahrspur.verbinde(gerade3, senke);
		
		vorne.alleHindernisseSuchen();
		vorneLinks.alleHindernisseSuchen();
		vorneRechts.alleHindernisseSuchen();
		messung.alleHindernisseSuchen();
		hinten.alleHindernisseSuchen();
		hintenLinks.alleHindernisseSuchen();
		hintenRechts.alleHindernisseSuchen();
		
	}

	@Test
	void testAlleHindernisseSuchen() {
		//Hindernis vorne
		assertEquals(messung.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug(), vorne);
		assertEquals(vorne.hindernisGeben(HindernisRichtung.HINTEN).zielFahrzeug(), messung);
		
		//Hindernis hinten
		assertEquals(messung.hindernisGeben(HindernisRichtung.HINTEN).zielFahrzeug(), hinten);
		assertEquals(hinten.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug(), messung);
		
		//Hindernis vorne links
		assertEquals(messung.hindernisGeben(HindernisRichtung.VORNE_LINKS).zielFahrzeug(), vorneLinks);
		assertEquals(vorneLinks.hindernisGeben(HindernisRichtung.HINTEN_RECHTS).zielFahrzeug(), messung);
		
		//Hindernis vorne rechts
		assertEquals(messung.hindernisGeben(HindernisRichtung.VORNE_RECHTS).zielFahrzeug(), vorneRechts);
		assertEquals(vorneRechts.hindernisGeben(HindernisRichtung.HINTEN_LINKS).zielFahrzeug(), messung);
		
		//Hindernis hinten links
		assertEquals(messung.hindernisGeben(HindernisRichtung.HINTEN_LINKS).zielFahrzeug(), hintenLinks);
		assertEquals(hintenLinks.hindernisGeben(HindernisRichtung.VORNE_RECHTS).zielFahrzeug(), hinten);
		
		//Hindernis hinten rechts
		assertEquals(messung.hindernisGeben(HindernisRichtung.HINTEN_RECHTS).zielFahrzeug(), hintenRechts);
		assertEquals(hintenRechts.hindernisGeben(HindernisRichtung.VORNE_LINKS).zielFahrzeug(), hinten);
		
		//Restliche Hindernisbeziehungen
		assertEquals(vorneLinks.hindernisGeben(HindernisRichtung.HINTEN).zielFahrzeug(), hintenLinks);
		assertEquals(hintenLinks.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug(), vorneLinks);
		
		assertEquals(vorneRechts.hindernisGeben(HindernisRichtung.HINTEN).zielFahrzeug(), hintenRechts);
		assertEquals(hintenRechts.hindernisGeben(HindernisRichtung.VORNE).zielFahrzeug(), vorneRechts);
		
		//Überprüfung der Distanzmessungen
		assertEquals(messung.hindernisGeben(HindernisRichtung.VORNE_LINKS).entfernungGeben(),
				(vorneLinks.posGeben() - messung.posGeben()) - 
				(vorneLinks.laengeGeben()/2.0 + messung.laengeGeben()/2.0));
		
		assertEquals(messung.hindernisGeben(HindernisRichtung.VORNE_RECHTS).entfernungGeben(),
				(vorneRechts.posGeben() - messung.posGeben()) - 
				(vorneRechts.laengeGeben()/2.0 + messung.laengeGeben()/2.0));
		
		assertEquals(messung.hindernisGeben(HindernisRichtung.VORNE).entfernungGeben(),
				(vorne.posGeben() - messung.posGeben()) - 
				(vorne.laengeGeben()/2.0 + messung.laengeGeben()/2.0));
		
		assertEquals(messung.hindernisGeben(HindernisRichtung.HINTEN_LINKS).entfernungGeben(),
				(hintenLinks.posGeben() - messung.posGeben()) + 
				(hintenLinks.laengeGeben()/2.0 + messung.laengeGeben()/2.0));
		
		assertEquals(messung.hindernisGeben(HindernisRichtung.HINTEN_RECHTS).entfernungGeben(),
				(hintenRechts.posGeben() - messung.posGeben()) + 
				(hintenRechts.laengeGeben()/2.0 + messung.laengeGeben()/2.0));
		
		assertEquals(messung.hindernisGeben(HindernisRichtung.HINTEN).entfernungGeben(),
				(hinten.posGeben() - messung.posGeben()) + 
				(hinten.laengeGeben()/2.0 + messung.laengeGeben()/2.0));
	}

}

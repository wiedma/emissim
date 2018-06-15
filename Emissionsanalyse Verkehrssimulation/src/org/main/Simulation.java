package org.main;

import org.PhysicEngine.Physics;
import org.Streckennetz.Netz;
import org.Verkehr.Fahrzeug;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Simulation {
	
	//TODO Simulations-Klasse modellieren
	
	//Momentane Simulationszeit
	private static double zeit;
	//Das Netz der Simulation
	private static Netz netz;
	//Excel Workbook zum Ablegen der Daten
	public static final XSSFWorkbook WORKBOOK = workbookErstellen();
	//Excel Worksheet zum Ablegen der CO2-Sensordaten
	public static final XSSFSheet CO2SHEET = WORKBOOK.createSheet();
	//OutputStrem zum Schreiben in die Excel-Datei
	private static FileOutputStream outputStream;
	//Die Reihe in der Tabelle, mit die diesem Zeitschritt zugeordnet ist
	private static XSSFRow currentCO2Row;
	//Initialisiere die Excel-Tabelle
	static {
		init();
	}
	//Sammelt den Ausstoß von C02 von allen aktiven Sensoren
	private static double momentanCO2Ausstoß = 0;
	
//Getter und Setter ------------------------------------------------------------------------------
	
	public static double zeitGeben() {
		return zeit;
	}
	
	public static void netzSetzen(Netz netz) {
		Simulation.netz = netz;
	}
	
	public static XSSFRow currentRowGeben() {
		return currentCO2Row;
	}
//------------------------------------------------------------------------------------------------
	
	public static void zeitschritt() {
		long millis = System.currentTimeMillis();
		System.out.println("Fahrzeuge: " + netz.anzahlFahrzeuge());
		zeit = Math.round((zeit+Physics.DELTA_TIME)*10)/10.0;
		netz.zeitschritt();
		System.out.println("Dauer: " + (System.currentTimeMillis() - millis));
		System.out.println("Zeit: " + zeit);
		
		//Schreibe die gesammelten Daten in der Excel-Tabelle
		XSSFCell zeitZelle = currentCO2Row.createCell(0);
		zeitZelle.setCellValue(zeit);
		XSSFCell wertZelle = currentCO2Row.createCell(1);
		wertZelle.setCellValue(momentanCO2Ausstoß*1000);
		XSSFCell anzahlZelle = currentCO2Row.createCell(2);
		anzahlZelle.setCellValue(netz.anzahlFahrzeuge());
		//Setze den momentanen CO2-Ausstoß wieder zurück
		momentanCO2Ausstoß = 0;
		//Gehe zu neuer Reihe in der Tabelle über
		currentCO2Row = CO2SHEET.createRow(currentCO2Row.getRowNum() + 1);
	}
	
	public static void fahrzeugHinzufuegen(Fahrzeug fahr) {
		netz.fahrzeugHinzufuegen(fahr);
	}
	
	public static void fahrzeugEntfernen(Fahrzeug fahr) {
		netz.fahrzeugEntfernen(fahr);
	}
	
	//ExcelWorkbook erstellen und zuweisen
	private static XSSFWorkbook workbookErstellen() {
		try {
			File outputFile = new File("output.xlsx");
			if(outputFile.exists()) {
				outputFile.delete();
			}
			outputFile.createNewFile();
			return new XSSFWorkbook();
		} catch(IOException e) {
			throw new Error(e);
		}
	}
	
	
	//Schreibt den gesammelten Datensatz auf die Festplatte
	public static void beenden() {
		try {
			outputStream = new FileOutputStream(new File("output.xlsx"));
			WORKBOOK.write(outputStream);
			WORKBOOK.close();
			outputStream.flush();
			outputStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//Mache das Set-Up für die Excel-Tabelle
	private static void init() {
		currentCO2Row = CO2SHEET.createRow(0);
		XSSFCell beschriftungZeit = currentCO2Row.createCell(0);
		beschriftungZeit.setCellValue("Zeit in s");
		XSSFCell beschriftungWert = currentCO2Row.createCell(1);
		beschriftungWert.setCellValue("CO2-Emission in g");
		XSSFCell beschriftungAnzahl = currentCO2Row.createCell(2);
		beschriftungAnzahl.setCellValue("Anzahl der Fahrzeuge");
		currentCO2Row = CO2SHEET.createRow(1);
	}
	
	//Sammelt die Emissionen in diesem Zeitschritt von allen aktiven Sensoren ein
	public static void sammleCO2Daten(double daten) {
		momentanCO2Ausstoß+=daten;
	}
}

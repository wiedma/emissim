package org.main;

import org.GUI.ConfigFrame;
import org.PhysicEngine.Physics;
import org.Streckennetz.Netz;
import org.Streckennetz.Senke;
import org.Verkehr.Fahrzeug;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.ArrayList;

/**Stellt alle Methoden zum steuern der Simulation bereit*/
public final class Simulation {
	
	/**Momentane Simulationszeit*/
	private static double zeit;
	/**Das Netz der Simulation*/
	private static Netz netz;
	/**Gibt an, ob die Simulation zum momentanen Zeitpunkt eine Messung durchführen soll*/
	private static boolean messung = true;
	/**Excel Workbook zum Ablegen der Daten*/
	public static final XSSFWorkbook WORKBOOK = workbookErstellen();
	/**Excel Worksheet zum Ablegen der CO2-Sensordaten*/
	public static final XSSFSheet CO2SHEET = WORKBOOK.createSheet();
	/**OutputStrem zum Schreiben in die Excel-Datei*/
	private static FileOutputStream outputStream;
	/**Die Reihe in der Tabelle, mit die diesem Zeitschritt zugeordnet ist*/
	private static XSSFRow currentCO2Row;
	//Initialisiere die Excel-Tabelle
	static {
		init();
	}
	/**Sammelt den Ausstoß von C02 von allen aktiven Sensoren*/
	private static double momentanCO2Ausstoß = 0;
	private static ArrayList<Double> vMittelList = new ArrayList<Double>();
	
//Getter und Setter ------------------------------------------------------------------------------
	/**Getter für die Simulationszeit*/
	public static double zeitGeben() {
		return zeit;
	}
	
	/**Setter für das Simulationsnetz*/
	public static void netzSetzen(Netz netz) {
		Simulation.netz = netz;
	}
	
	/**Getter für die momentan beschriebene Reihe in der Excel-Tabelle*/
	public static XSSFRow currentRowGeben() {
		return currentCO2Row;
	}
//------------------------------------------------------------------------------------------------
	
	public static void simulationStartenZeit(int tempolimit, int verkehrsstaerke, int zeitlimit) {
		Main.CONFIG_FRAME.deactivateButtons();
		
		//Netz setzen
		netz.reset();
		netz.tempolimitSetzen(tempolimit);
		netz.gesamtVerkehrsstaerkeSetzen(verkehrsstaerke);
		
		//Simulation durchführen
		double messzeit = 0;
		messungSetzten(false);
		while(messzeit == 0 || (zeit - messzeit) < zeitlimit) {
			zeitschritt();
			if(Senke.anzahlFahrzeugeEntfernt() > 0 && messzeit == 0) {
				messzeit = zeit;
				messungSetzten(true);
			}
			if(zeit > 10000) {
				reset(false);
			}
			if(zeit < messzeit) {
				messzeit = 0;
				messungSetzten(false);
			}
			if(messzeit != 0) {
				int progress = (int) (((zeit - messzeit + 0.0)/(zeitlimit+0.0)) * 100);
				ConfigFrame.PROGRESS.setValue(progress);
			}
		}
		
		JOptionPane.showMessageDialog(Main.CONFIG_FRAME, "Es wurden " + (int) momentanCO2Ausstoß +
				"kg Kohlenstoffdioxid ausgestoßen");
		
		Simulation.reset(true);
		
		Main.CONFIG_FRAME.activateButtons();
	}
	
	public static void simulationStartenLeistung(int tempolimit, int verkehrsstaerke, int fahrzeugLimit) {
		Main.CONFIG_FRAME.deactivateButtons();
		
		//Netz setzen
		netz.reset();
		netz.tempolimitSetzen(tempolimit);
		netz.gesamtVerkehrsstaerkeSetzen(verkehrsstaerke);
		
		while(netz.anzahlFahrzeuge() > 0 || Senke.anzahlFahrzeugeEntfernt() == 0) {
			Simulation.zeitschritt();
			if(netz.gesamtFahrzeugeErzeugt() >= fahrzeugLimit) {
				netz.quellenDektivieren();
			}
			if(Simulation.zeitGeben() > 10000) {
				Simulation.reset(false);
			}
			int progress = (int) (((netz.gesamtFahrzeugeErzeugt() + 0.0)/(fahrzeugLimit + 0.0)) * 100);
			ConfigFrame.PROGRESS.setValue(progress);
		}
		
		JOptionPane.showMessageDialog(Main.CONFIG_FRAME, "Es wurden " + (int) momentanCO2Ausstoß +
				"kg Kohlenstoffdioxid ausgestoßen");
		
		Simulation.reset(true);
		
		Main.CONFIG_FRAME.activateButtons();
	}
	
	/**Führe einen Zeitschritt in der Simulation durch*/
	public static void zeitschritt() {
		zeit = Math.round((zeit+Physics.DELTA_TIME)*10)/10.0;
		netz.zeitschritt();
	}
	
	/**Füge der Simulation ein Fahrzeug hinzu*/
	public static void fahrzeugHinzufuegen(Fahrzeug fahr) {
		netz.fahrzeugHinzufuegen(fahr);
	}
	
	/**Entferne ein Fahrzueg aus der Simulation*/
	public static void fahrzeugEntfernen(Fahrzeug fahr) {
		netz.fahrzeugEntfernen(fahr);
	}
	
	/**ExcelWorkbook erstellen und zuweisen*/
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
	
	
	/**Schreibt den gesammelten Datensatz auf die Festplatte*/
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
	
	public static void reset(boolean speichern) {
		if(speichern) {
			//Schreibe die gesammelten Daten in der Excel-Tabelle
			XSSFCell tempoZelle = currentCO2Row.createCell(0);
			tempoZelle.setCellValue(netz.tempolimitGeben());
			XSSFCell wertZelle = currentCO2Row.createCell(1);
			wertZelle.setCellValue(momentanCO2Ausstoß);
			XSSFCell verkehrsstaerkeZelle = currentCO2Row.createCell(2);
			verkehrsstaerkeZelle.setCellValue(netz.gesamtVerkehrsstaerke());
			XSSFCell geschwindigkeitsZelle = currentCO2Row.createCell(3);
			double sum = 0;
			for(double d : vMittelList) {
				sum += d;
			}
			geschwindigkeitsZelle.setCellValue(sum/vMittelList.size());
			//Gehe zu neuer Reihe in der Tabelle über
			currentCO2Row = CO2SHEET.createRow(currentCO2Row.getRowNum() + 1);
		}
		
		//Setze den momentanen CO2-Ausstoß wieder zurück
		momentanCO2Ausstoß = 0;
		//Setze die Liste der mittleren Geschwindigkeiten zurück
		vMittelList = new ArrayList<Double>();
		//Setze die static Variablen alle auf 0 und entferne alle restlichen Fahrzeuge aus der Simulation
		zeit = 0.0;
		netz.reset();
	}
	
	/**Mache das Set-Up für die Excel-Tabelle*/
	private static void init() {
		currentCO2Row = CO2SHEET.createRow(0);
		XSSFCell beschriftungZeit = currentCO2Row.createCell(0);
		beschriftungZeit.setCellValue("Tempolimit in km/h");
		XSSFCell beschriftungWert = currentCO2Row.createCell(1);
		beschriftungWert.setCellValue("CO2-Emission in kg");
		XSSFCell beschriftungAnzahl = currentCO2Row.createCell(2);
		beschriftungAnzahl.setCellValue("Verkehrsstärke in Fz/h");
		XSSFCell beschriftungV = currentCO2Row.createCell(3);
		beschriftungV.setCellValue("Mittlere Geschwindigkeit in m/s");
		currentCO2Row = CO2SHEET.createRow(1);
	}
	
	/**Sammelt die Emissionen in diesem Zeitschritt von allen aktiven Sensoren ein*/
	public static void sammleCO2Daten(double daten) {
		if(messung) {
			momentanCO2Ausstoß+=daten;	
		}
	}
	
	public static void sammleGeschwindigkeitsDaten(double daten) {
		vMittelList.add(daten);
	}
	
	public static void messungSetzten(boolean messungNeu) {
		messung = messungNeu;
	}
	
}

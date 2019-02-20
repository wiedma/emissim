package org.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.text.NumberFormatter;

import org.main.Simulation;

public class ConfigFrame extends JFrame {

	private static final long serialVersionUID = -2167898202000818868L;
	private JPanel 	rootPanel, inputRootPanel, tempolimitLabelPanel, tempolimitInputPanel,
					fahrzeuglimitLabelPanel, fahrzeuglimitInputPanel,
					zeitlimitLabelPanel, zeitlimitInputPanel,
					verkehrsstaerkeLabelPanel, verkehrsstaerkeInputPanel,
					buttonPanel, messagePanel, masterPanelSouth, progressPanel;
	private JLabel tempolimitLabel, fahrzeuglimitLabel, zeitlimitLabel, verkehrsstaerkeLabel, message;
	private JComboBox<String> tempolimitInput;
	private JFormattedTextField fahrzeuglimitInput, zeitlimitInput, verkehrsstaerkeInput;
	private JButton simulationZeit, simulationLeistung;
	private String[] tempos;
	
	public static final JProgressBar PROGRESS = new JProgressBar(0, 100);
	
	public ConfigFrame() {
		super("Emissim - Konfiguration");
		
		//WindowListener exportiert bei Schließung des Fensters
		this.addWindowListener(new WindowListener(){
			public void windowClosing(WindowEvent e){
				Simulation.beenden();
			}

			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowClosed(WindowEvent e) {	}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
		});
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setSize(new Dimension(650, 250));
		this.setResizable(false);
		
		//Formatter für Integer Inputs
	    NumberFormat format = NumberFormat.getInstance();
	    NumberFormatter formatter = new NumberFormatter(format);
	    formatter.setValueClass(Integer.class);
	    formatter.setMinimum(0);
	    formatter.setMaximum(Integer.MAX_VALUE);
	    formatter.setAllowsInvalid(false);
	    formatter.setCommitsOnValidEdit(true);
		
		//RootPanel BorderLayout
		rootPanel = new JPanel();
		rootPanel.setLayout(new BorderLayout());
		
		//InputRootPanel GridLayout ---> RootPanel center
		inputRootPanel = new JPanel();
		inputRootPanel.setLayout(new GridLayout(4, 2));
		rootPanel.add(inputRootPanel, BorderLayout.CENTER);
		
//-------------------------------------------------------------------------------------------------------
		//Zeile 1: Tempolimit eingeben
		
		//TempoLimitLabelPanel FlowLayout ---> InputRootPanel Grid 1
		tempolimitLabelPanel = new JPanel();
		tempolimitLabelPanel.setLayout(new FlowLayout());
			
			//TempolimitLabel
			tempolimitLabel = new JLabel("Tempolimit:");
			tempolimitLabelPanel.add(tempolimitLabel);
			
		inputRootPanel.add(tempolimitLabelPanel);
		
		//TempoLimitInputPanel FlowLayout ---> InputRootPanel Grid 2
		tempolimitInputPanel = new JPanel();
		tempolimitInputPanel.setLayout(new FlowLayout());
			
			//TempolimitInput
			tempos = new String[] {"Ohne Tempolimit", "120", "100"};
			tempolimitInput = new JComboBox<String>(tempos);
			tempolimitInputPanel.add(tempolimitInput);
			
		inputRootPanel.add(tempolimitInputPanel);
		
//-------------------------------------------------------------------------------------------------------
		//Zeile 2: Fahrzeuglimit eingeben
		
		//FahrzeuglimitLabelPanel FlowLayout ---> inputRootPanel Grid 3
		fahrzeuglimitLabelPanel = new JPanel();
		fahrzeuglimitLabelPanel.setLayout(new FlowLayout());
		
			//FahrzeuglimitLabel
			fahrzeuglimitLabel = new JLabel("Fahrzeuglimit (nur bei konstanter Verkehrsleistung):");
			fahrzeuglimitLabelPanel.add(fahrzeuglimitLabel);
			
		inputRootPanel.add(fahrzeuglimitLabelPanel);
		
		//FahrzeuglimitInputPanel FlowLayout ---> inputRootPanel Grid 4
		fahrzeuglimitInputPanel = new JPanel();
		fahrzeuglimitInputPanel.setLayout(new FlowLayout());
		
			//Fahrzeuglimit Input
			fahrzeuglimitInput = new JFormattedTextField(formatter);
			fahrzeuglimitInput.setColumns(10);
			fahrzeuglimitInput.setText("1.000");
			fahrzeuglimitInputPanel.add(fahrzeuglimitInput);
			
		inputRootPanel.add(fahrzeuglimitInputPanel);
		
//-------------------------------------------------------------------------------------------------------
		//Zeile 3: Zeitlimit eingeben
		
		//ZeitlimitLabelPanel FlowLayout --> inputRootPanel Grid 5
		zeitlimitLabelPanel = new JPanel();
		zeitlimitLabelPanel.setLayout(new FlowLayout());
		
			//ZeitlimitLabel
			zeitlimitLabel = new JLabel("Zeitlimit in s (nur bei konstanter Zeit):");
			zeitlimitLabelPanel.add(zeitlimitLabel);
			
		inputRootPanel.add(zeitlimitLabelPanel);
		
		//ZeitlimitInputPanel FlowLayout ---> inputRootPanel Grid 6
		zeitlimitInputPanel = new JPanel();
		zeitlimitInputPanel.setLayout(new FlowLayout());
		
			//ZeitlimitInput
			zeitlimitInput = new JFormattedTextField(formatter);
			zeitlimitInput.setColumns(10);
			zeitlimitInput.setText("600");
			zeitlimitInputPanel.add(zeitlimitInput);
			
		inputRootPanel.add(zeitlimitInputPanel);
		
//------------------------------------------------------------------------------------------------------
		//Zeile 4: Verkehrsstärke eingeben
		
		//VerkehrsstärkeLabelPanel FlowLayout ---> inputRootPanel Grid 7
		verkehrsstaerkeLabelPanel = new JPanel();
		verkehrsstaerkeLabelPanel.setLayout(new FlowLayout());
		
			//VerkehrsstärkeLabel
			verkehrsstaerkeLabel = new JLabel("Verkehrsstärke in Fz/h:");
			verkehrsstaerkeLabelPanel.add(verkehrsstaerkeLabel);
			
		inputRootPanel.add(verkehrsstaerkeLabelPanel);
		
		//VerkehrsstärkeInputPanel FlowLayout ---> inputRootPanel Grid 8
		verkehrsstaerkeInputPanel = new JPanel();
		verkehrsstaerkeInputPanel.setLayout(new FlowLayout());
		
			//VerkehrsstärkeInput
			verkehrsstaerkeInput = new JFormattedTextField(formatter);
			verkehrsstaerkeInput.setColumns(10);
			verkehrsstaerkeInput.setText("2.500");
			verkehrsstaerkeInputPanel.add(verkehrsstaerkeInput);
			
		inputRootPanel.add(verkehrsstaerkeInputPanel);
		
//------------------------------------------------------------------------------------------------------
		//MessagePanel FlowLayout ---> rootPanel north
		messagePanel = new JPanel();
		messagePanel.setLayout(new FlowLayout());
		message = new JLabel();
		message.setForeground(Color.red);
		messagePanel.add(message);
		rootPanel.add(messagePanel, BorderLayout.NORTH);
//------------------------------------------------------------------------------------------------------
		//MasterPanelSouth GridLayout ---> rootPanel south
		masterPanelSouth = new JPanel();
		masterPanelSouth.setLayout(new GridLayout(2,1));
		rootPanel.add(masterPanelSouth, BorderLayout.SOUTH);
//------------------------------------------------------------------------------------------------------
		//ButtonPanel FlowLayout ---> masterPanelSouth Grid 1
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		
			simulationZeit = new JButton("Zeitbegrenzte Simulation");
			simulationZeit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					int[] inputs = getInputs();
					if(inputs == null) {
						return;
					}
					PROGRESS.setValue(0);
					new Thread() {
						public void run() {
							Simulation.simulationStartenZeit(inputs[0], inputs[3], inputs[1]);	
						}
					}.start();
				}
			});
			
			buttonPanel.add(simulationZeit);
			
			simulationLeistung = new JButton("Leistungsbegrenzte Simulation");
			simulationLeistung.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int[] inputs = getInputs();
					if(inputs == null) {
						return;
					}
					PROGRESS.setValue(0);
					new Thread() {
						public void run() {
							Simulation.simulationStartenLeistung(inputs[0], inputs[3], inputs[2]);
						}
					}.start();
				}
			});
			
			buttonPanel.add(simulationLeistung);

		masterPanelSouth.add(buttonPanel);
//-----------------------------------------------------------------------------------------------------
		//Progresspanel FlowLayout ---> masterPanelSouth Grid 2
		progressPanel = new JPanel();
		progressPanel.setLayout(new FlowLayout());
		progressPanel.add(PROGRESS);
		
		masterPanelSouth.add(progressPanel);
		
		this.add(rootPanel);
		this.setVisible(true);
		
	}
	
	public int[] getInputs(){
		message.setText("");
		//Lese Tempolimit
		String tempolimitString = tempolimitInput.getSelectedItem().toString();
		int tempolimit, zeit, fahrzeuge, verkehrsstaerke;
		if(tempolimitString.equals(tempos[0])) {
			tempolimit = 200;
		}
		else if(tempolimitString.equals(tempos[1])) {
			tempolimit = 120;
		}
		else if(tempolimitString.equals(tempos[2])) {
			tempolimit = 100;
		}
		else {
			tempolimit = 200;
		}
		
		
		try {
			//Lese Leistungsbegrenzung
			String fahrzeugString = fahrzeuglimitInput.getText();
			fahrzeugString = fahrzeugString.replace(".", "");
			fahrzeuge = Integer.parseInt(fahrzeugString);
		} catch(Exception e) {
			message.setText("Fahrzeuglimit ist ungültig!");
			return null;
		}
		
		try {
			//Lese Zeitbegrenzung
			String zeitString = zeitlimitInput.getText();
			zeitString = zeitString.replace(".", "");
			zeit = Integer.parseInt(zeitString);
		} catch(Exception e) {
			message.setText("Zeitlimit ist ungültig!");
			return null;
		}
		
		try {
			//Lese Verkehrsstärke
			String verkehrsstaerkeString = verkehrsstaerkeInput.getText();
			verkehrsstaerkeString = verkehrsstaerkeString.replace(".", "");
			verkehrsstaerke = Integer.parseInt(verkehrsstaerkeString);
		} catch(Exception e) {
			message.setText("Verkehrsstärke ist ungültig!");
			return null;
		}
		
		return new int[] {tempolimit, zeit, fahrzeuge, verkehrsstaerke};
		
	}
	
	public void deactivateButtons() {
		simulationZeit.setEnabled(false);
		simulationLeistung.setEnabled(false);
	}
	
	public void activateButtons() {
		simulationZeit.setEnabled(true);
		simulationLeistung.setEnabled(true);
	}
}

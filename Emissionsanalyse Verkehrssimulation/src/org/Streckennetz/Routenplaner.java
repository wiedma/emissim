package org.Streckennetz;
import java.util.ArrayList;
import org.Graphen.*;

public class Routenplaner {
	
	public static Strecke[] planeRouten(Netz netz) {
		//Array aller Fahrspuren im Netz
		Fahrspur[] spuren = netz.spurenGeben();
		
		ArrayList<Strecke> strecken = new ArrayList<Strecke>();
		
		//Listen mit allen Quellen und Senken im Netz
		ArrayList<Quelle> quellen = new ArrayList<Quelle>();
		ArrayList<Senke> senken = new ArrayList<Senke>();
		
		
		//F�r jede Fahrspur im spuren-Array
		for(Fahrspur spur : spuren){
			//Wenn die Fahrspur eine Quelle ist
			if(spur.getClass().equals(Quelle.class)){
				//F�ge sie zu den Quellen hinzu
				quellen.add((Quelle)spur);
			}
			//Wenn die Fahrspur eine Senke ist
			if(spur.getClass().equals(Senke.class)){
				//F�ge sie den Senken hinzu
				senken.add((Senke)spur);
			}
		}
		
		
		//Graph generieren
		Graph graph = new Graph();
		inGraphenEintragen(spuren[0], graph);
		
		//F�r jede Quelle aus der quellen-Liste
		for(Quelle quelle : quellen){
			for(Senke senke : senken) {
				//F�hre den Dijkstra-Algorithmus durch
				Datenelement[] datenelemente = graph.dijkstra(quelle.knoten, senke.knoten);
				//Caste das Ergebnis zur�ck in Fahrspuren und generiere daraus die Strecken
				Fahrspur[] strecke = new Fahrspur[datenelemente.length];
				for(int i = 0; i < datenelemente.length; i++) {
					if(datenelemente[i] instanceof Fahrspur) {
						strecke[i] = (Fahrspur) datenelemente[i];
					}
				}
				strecken.add(new Strecke(strecke, netz));
			}
		}
		return strecken.toArray(new Strecke[0]);
	}
	
	//Tr�gt ein zusammenh�ngendes Streckennetz mithilfe des Tiefensuche-Algorithmus in einen Graphen
	//ein. Begonnen wird bei der �bergebenen Fahrspur-Referenz
	private static void inGraphenEintragen(Fahrspur spur, Graph graph) {
		//�berpfr�fe, dass kein Argument null ist
		if(spur == null || graph == null) {
			return;
		}
		
		//Erzeuge neuen Knoten
		Knoten knoten = new Knoten(spur);
		spur.knoten = knoten;
		
		//F�ge den neuen Knoten dem Graphen hinzu
		graph.knotenHinzufuegen(knoten);
		
		//F�hre Tiefensuche durch um alle Nachbarn einzutragen
		
		//Markiere diese Fahrspur als eingetragen
		spur.eintragen();
		
		//FIXME Bugfix!: Tiefensuche in das Datenelement kapseln um Quellen und Senken besser ber�cksichtigen zu k�nnen
		
		//Linker Nachbar
		if(spur.linkeFahrspur != null) {
			if(!spur.linkeFahrspur.istEingetragen()) {
				inGraphenEintragen(spur.linkeFahrspur, graph);
			}
			knoten.kanteHinzufuegen(new Kante(spur.linkeFahrspur.knotenGeben(),
					(spur.breite/2) + (spur.linkeFahrspur.breite/2)));
		}
		
		//Rechter Nachbar
		if(spur.rechteFahrspur != null) {
			if(!spur.rechteFahrspur.istEingetragen()) {
				inGraphenEintragen(spur.rechteFahrspur, graph);
			}
			knoten.kanteHinzufuegen(new Kante(spur.rechteFahrspur.knotenGeben(),
					(spur.breite/2) + (spur.rechteFahrspur.breite/2)));
		}
		
		//Vorderer Nachbar
		if(spur.naechsteFahrspur != null) {
			if(!spur.naechsteFahrspur.istEingetragen()) {
				inGraphenEintragen(spur.naechsteFahrspur, graph);
			}
			knoten.kanteHinzufuegen(new Kante(spur.naechsteFahrspur.knotenGeben(),
					(spur.laenge/2) + (spur.naechsteFahrspur.laenge/2)));
		}
		
		//Hinterer Nachbar (ist kein Nachbar im Graphen, da die Autos nicht r�ckw�rts fahren sollen)
		if(spur.vorherigeFahrspur != null && !spur.vorherigeFahrspur.istEingetragen()) {
			inGraphenEintragen(spur.vorherigeFahrspur, graph);
		}
		
	}
}

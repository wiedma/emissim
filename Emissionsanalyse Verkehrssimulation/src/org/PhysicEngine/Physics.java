package org.PhysicEngine;

public abstract class Physics {
	
	//Erdbeschleunigung g in m*(s^-2)
	public static final double ERDBESCHLEUNIGUNG = 9.81;
	//Kleinste messbare Zeiteinheit in s
	public static final double DELTA_TIME = 0.1;
	//Dichte der Luft bei Normdruck (1013 bar) und 10°C (Jahresmittel in Deutschland) in kg/(m^3)
	public static final double DICHTE_DER_LUFT = 1.2466;
	
	
	//Bewegung mit konstanter Geschwindigkeit
	public static double bewege(double pos, double geschwindigkeit) {
		return pos + (geschwindigkeit * DELTA_TIME);
	}
	
	//Bewegung mit konstanter Beschleunigung
	public static double bewege(double pos, double geschwindigkeit, double beschleunigung) {
		return bewege(pos, geschwindigkeit) + 0.5*beschleunigung*Math.pow(DELTA_TIME, 2);
	}
	
	//Berechne die Rollreibung eines rollenden Körpers in N
	public static double rollreibung(double rollreibungszahl, double masse) {
		return rollreibungszahl * masse * ERDBESCHLEUNIGUNG;
	}
	
	//Berechne die Luftreibung eines Körpers in N
	public static double luftreibung(double luftreibungszahl, double frontflaeche,
			double geschwindigkeit) {
		return 0.5 * frontflaeche * luftreibungszahl * DICHTE_DER_LUFT 
				* Math.pow(geschwindigkeit, 2);
	}
	
	//Berechne den Beschleunigungswiderstand eines Fahrzeuges in N
	//Drehmassenzuschlagsfaktoren nach Gang aus [HA08]
	public static double beschleunigungswiderstand(double masse, double beschleunigung,
			int gang) {
		//Drehmassenzuschlagsfaktor bestimmen
		double lamda;
		switch (gang) {
		case 1: lamda = 1.375; break;
		case 2: lamda = 1.16; break;
		case 3: lamda = 1.085; break;
		case 4: lamda = 1.06; break;
		case 5: lamda = 1.05; break;
		default: lamda = 1; break;
		}
		
		return lamda * masse * beschleunigung;
	}

}

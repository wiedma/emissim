package org.Verkehr;
import java.io.*;

public interface Sensor<T> {
	
	public void sammleDaten(T daten);
	
	public void schreibeDaten(File file);
	
}

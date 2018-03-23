package org.main;

import org.Verkehr.PKW;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(int i = 0; i < 1000; i++) {
			PKW auto = new PKW();
			System.out.println(auto.getWirkungsgrad());
		}
		
		System.out.println("Hello World!");
	}

}

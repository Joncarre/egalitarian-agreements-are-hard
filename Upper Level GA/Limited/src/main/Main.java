package main;

import java.io.IOException;

import AGS.AGS_Engine;


public class Main {

	/**
	 * @author J. Carrero
	 * @param args
	 */
    public static void main(String[] args) {
		try {
			AGS_Engine engine = new AGS_Engine();
			engine.start();
		} catch (IOException e) {
			System.out.println("Error - Main: " + e.getMessage());
		}
    }
}

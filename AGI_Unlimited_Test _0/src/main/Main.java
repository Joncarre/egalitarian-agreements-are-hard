package main;

import java.io.IOException;

public class Main {

	/**
	 * @author J. Carrero
	 * @param args
	 */
    public static void main(String[] args) {
    	
    	/*
    	WeightedRandomSelect<String> itemDrops = new WeightedRandomSelect<>();
    	
    	itemDrops.addEntry("10 Gold",  5.0);
    	itemDrops.addEntry("Sword",   20.0);
    	itemDrops.addEntry("Shield",  45.0);
    	itemDrops.addEntry("Armor",   20.0);
    	itemDrops.addEntry("Potion",  10.0);

    	// drawing random entries from it
    	int g = 0;
    	int s = 0;
    	int sh = 0;
    	int a = 0;
    	int p = 0;
    	
    	for (int i = 0; i < 100; i++) {
    		if(itemDrops.getRandom() == "10 Gold")
    			g++;
    		else if(itemDrops.getRandom() == "Sword")
    			s++;
    		else if(itemDrops.getRandom() == "Shield")
    			sh++;
    		else if(itemDrops.getRandom() == "Armor")
    			a++;
    		else if(itemDrops.getRandom() == "Potion")
    			p++;
    	}
    	
    	System.out.println("10 Gold: " + g);
    	System.out.println("Sword: " + s);
    	System.out.println("Shield: " + sh);
    	System.out.println("Armor: " + a);
    	System.out.println("Potion: " + p);
    	*/
    			try {
			// ----- Opción 1: Con monitoreo completo (por defecto) -----
			//Engine engine = new Engine();
			
			// ----- Opción 2: Monitoreo personalizado -----
			//Engine engine = new Engine(true);
			//engine.getMonitor().setIntervals(1000, 500); // Detallado cada 1000, rápido cada 250

			// ----- Opción 3: Sin monitoreo (para ejecución silenciosa) -----
			Engine engine = new Engine(false);
			
			engine.start();
		} catch (IOException e) {
			System.out.println("Error - Main: " + e.getMessage());
		}
    }
}






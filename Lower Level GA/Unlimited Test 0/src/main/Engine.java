package main;

import java.io.IOException;
import java.util.Scanner;

public class Engine {
	private final int avgIterations = 1; // Average Iterations
	private final int generations = 100000; // Evolutions of population
	private final int popSize = 30; // Population Size
	private final int numUsers = 8; // Number of agents
	private int pfcIterations = 20; // Preference Iterations
	private FitnessCalc fitness = new FitnessCalc(this.numUsers);
	private Population myPop;
	Scanner sc = new Scanner(System.in);
	
	/**
	 * This method controls the flow of execution
	 * @throws IOException
	 */
	public void start() throws IOException {	
		System.out.println("    (1) Read data \n" + "    (2) Gerenate new data");
		int op = this.sc.nextInt();
		if(op == 1) {
			System.out.println("Do you wanna analyze the data? \n" +  "    (1) Yes \n" +  "    (2) No");
			op = this.sc.nextInt();
			if(op == 1)
				readPopulation();
			else
				System.out.println("Bye!");
		}else 
			generatePopulation();
	}
	
	/**
	 * Read population from file and execute IGA
	 * @throws IOException
	 */
	public void readPopulation() throws IOException {
		this.fitness.readMPreferences();
		this.fitness.readOPreferences();
		boolean seguir = true;
		
		/* Save resources to modify */
		// this.fitness.initialResources();
		// this.fitness.saveResources(0);
		// this.fitness.saveResources(1);
		// this.fitness.saveResources(2);
		// this.fitness.saveResources(3);
		// this.fitness.saveResources(4);
		// this.fitness.saveResources(5);
		// this.fitness.saveResources(6);
		// this.fitness.saveResources(7);
		// this.fitness.saveResources(8);
		// this.fitness.saveResources(9);
		// this.fitness.saveResources(10);
		// this.fitness.saveResources(11);
		// this.fitness.saveResources(12);
		// this.fitness.saveResources(13);
		// this.fitness.saveResources(14);
		// this.fitness.saveResources(15);
		// this.fitness.saveResources(16);
		// this.fitness.saveResources(17);
		// this.fitness.saveResources(18);
		// this.fitness.saveResources(19);
		// this.fitness.saveResources(20);
		// this.fitness.saveResources(21);
		// this.fitness.saveResources(22);
		// this.fitness.saveResources(23);
		// this.fitness.saveResources(24);
		// this.fitness.saveResources(25);
		// this.fitness.saveResources(26);

		
		for(int i = 0; i < pfcIterations && seguir == true; i++) { // Loop to increment or decrement preferences	
			this.fitness.resetValues(avgIterations);
			this.fitness.resetPopulations(popSize);
			this.fitness.saveTemporalPreferences();

			for(int j = 0; j < avgIterations; j++) { // Loop to select our best egalitarian result
		    	this.myPop = new Population(this.popSize, this.numUsers, false);
		    	int generationCount = 0;
				while (generationCount < this.generations) {
			        generationCount++;
					//System.out.println("Iteration: " + generationCount + " Fitness(lowest): " + myPop.getIndividual(0).getOnlyFitness()  + " Genes: " + myPop.getIndividual(0).toString());
			        this.myPop = Algorithm.evolvePopulation(this.myPop, this.popSize, generationCount);
				}
				this.fitness.saveIndividuals(myPop.getIndividual(0));
			}
			this.fitness.updateBestFitness();
			this.fitness.showBestFitness();

			/* Resource modification */
			// this.fitness.setValueBaja(0, 0);
			// this.fitness.setValueBaja(0, 1);
			// this.fitness.setValueBaja(0, 2);
			// this.fitness.setValueBaja(0, 3);
			// this.fitness.setValueBaja(0, 4);
			// this.fitness.setValueBaja(0, 5);
			// this.fitness.setValueBaja(0, 6);
			// this.fitness.setValueBaja(0, 7);
			// this.fitness.setValueBaja(0, 8);
			// this.fitness.setValueBaja(0, 9);
			// this.fitness.setValueBaja(0, 10);
			// this.fitness.setValueBaja(0, 11);
			// this.fitness.setValueBaja(0, 12);
			// this.fitness.setValueBaja(0, 13);
			// this.fitness.setValueBaja(0, 14);
			// this.fitness.setValueBaja(0, 15);
			// this.fitness.setValueBaja(0, 16);
			// this.fitness.setValueBaja(0, 17);
			// this.fitness.setValueBaja(0, 18);
			// this.fitness.setValueBaja(0, 19);
			// this.fitness.setValueBaja(0, 20);
			// this.fitness.setValueBaja(0, 21);
			// this.fitness.setValueBaja(0, 22);
			// this.fitness.setValueBaja(0, 23);
			// this.fitness.setValueBaja(0, 24);
			// this.fitness.setValueBaja(0, 25);
			// this.fitness.setValueBaja(0, 26);
 
			//seguir = this.fitness.distributionVariationAlza();
		}
		System.out.println("To be continued...");
	}
	
	/**
	 * Generate a new Population to files
	 * @throws IOException
	 */
	public void generatePopulation() throws IOException {
		System.out.println("ATENTION, new data will be generate. Press ENTER to continue");
		this.sc.nextLine(); this.sc.nextLine();
	    Population myPop = new Population(this.popSize, this.fitness.getNumUsers(), true);
		this.fitness.randomPreferences();
		this.fitness.writePreferences();
		System.out.println("Data generated");
	}
}


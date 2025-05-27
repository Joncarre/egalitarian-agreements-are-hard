package main;

import java.io.IOException;
import java.util.Scanner;

public class Engine {	private final int avgIterations = 20; // Average Iterations
	private final int generations = 3000; // Aumentado drásticamente para mejor convergencia
	private final int popSize = 90; // Aumentado hasta casi el límite máximo (99)
	private final int numUsers = 8; // Number of agents
	private int pfcIterations = 20; // Preference Iterations
	private FitnessCalc fitness = new FitnessCalc(this.numUsers);
	private Population myPop;
	Scanner sc = new Scanner(System.in);
	// Monitor para seguimiento de la evolución (opcional)
	private AlgorithmMonitor monitor;
	
	/**
	 * Constructor - inicializa el monitor
	 */
	public Engine() {
		// Crear monitor habilitado por defecto
		// Para deshabilitar: cambiar 'true' por 'false'
		this.monitor = new AlgorithmMonitor(true, 500, 100);
	}
	
	/**
	 * Constructor con opción de monitoreo personalizado
	 */
	public Engine(boolean enableMonitoring) {
		this.monitor = new AlgorithmMonitor(enableMonitoring, 500, 100);
	}
	
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
		
		/* Guardar recursos a modificar */
		//this.fitness.initialResources();
		//this.fitness.saveResources(0);
		//this.fitness.saveResources(1);
		//this.fitness.saveResources(2);
		//this.fitness.saveResources(3);
		//this.fitness.saveResources(4);
		//this.fitness.saveResources(5);
		//this.fitness.saveResources(6);
		//this.fitness.saveResources(7);
		//this.fitness.saveResources(8);
		//this.fitness.saveResources(9);
		//this.fitness.saveResources(10);
		//this.fitness.saveResources(11);
		//this.fitness.saveResources(12);
		//this.fitness.saveResources(13);
		//this.fitness.saveResources(14);
		//this.fitness.saveResources(15);
		//this.fitness.saveResources(16);
		//this.fitness.saveResources(17);
		//this.fitness.saveResources(18);
		//this.fitness.saveResources(19);
		//this.fitness.saveResources(20);
		//this.fitness.saveResources(21);
		//this.fitness.saveResources(22);
		//this.fitness.saveResources(23);
		//this.fitness.saveResources(24);
		//this.fitness.saveResources(25);
		//this.fitness.saveResources(26);
		//this.fitness.saveResources(27);
		//this.fitness.saveResources(28);
		//this.fitness.saveResources(29);
		//this.fitness.saveResources(30);
		//this.fitness.saveResources(31);
		//this.fitness.saveResources(32);
		//this.fitness.saveResources(33);
		//this.fitness.saveResources(34);
		//this.fitness.saveResources(35);
		//this.fitness.saveResources(36);
		//this.fitness.saveResources(37);
		//this.fitness.saveResources(38);
		//this.fitness.saveResources(39);

		
		for(int i = 0; i < pfcIterations && seguir == true; i++) { // Loop to increment or decrement preferences	
			this.fitness.resetValues(avgIterations);
			this.fitness.resetPopulations(popSize);
			this.fitness.saveTemporalPreferences();			for(int j = 0; j < avgIterations; j++) { // Loop to select our best egalitarian result
		    	monitor.showRunStart(j+1, avgIterations);
		    	this.myPop = new Population(this.popSize, this.numUsers, false);
		    	int generationCount = 0;
		    	
		    	// Show initial population
		    	monitor.monitorEvolution(this.myPop, 0, Algorithm.getCurrentMutationRate(), 
		    	                        Algorithm.getBestFitnessEver(), Algorithm.getNoImprovementCounter(), 
		    	                        Algorithm.getExplorePhase());
		    	
				while (generationCount < this.generations) {
			        generationCount++;
					//System.out.println("Iteration: " + generationCount + " Fitness(lowest): " + myPop.getIndividual(0).getOnlyFitness()  + " Genes: " + myPop.getIndividual(0).toString());
			        this.myPop = Algorithm.evolvePopulation(this.myPop, this.popSize, generationCount);
			        
			        // Monitor evolution progress
			        monitor.monitorEvolution(this.myPop, generationCount, Algorithm.getCurrentMutationRate(), 
			                               Algorithm.getBestFitnessEver(), Algorithm.getNoImprovementCounter(), 
			                               Algorithm.getExplorePhase());
				}
				
				// Show final results for this run
				Individual best = this.myPop.getFittest();
				monitor.showRunEnd(j+1, best);
				
				this.fitness.saveIndividuals(myPop.getIndividual(0));
			}
			this.fitness.updateBestFitness();
			this.fitness.showBestFitness();

			/* Modificaci�n de recursos */
			//this.fitness.setValueBaja(0, 0);
			//this.fitness.setValueBaja(0, 1);
			//this.fitness.setValueBaja(0, 2);
			//this.fitness.setValueBaja(0, 3);
			//this.fitness.setValueBaja(0, 4);
			//this.fitness.setValueBaja(0, 5);
			//this.fitness.setValueBaja(0, 6);
			//this.fitness.setValueBaja(0, 7);
			//this.fitness.setValueBaja(0, 8);
			//this.fitness.setValueBaja(0, 9);
			//this.fitness.setValueBaja(0, 10);
			//this.fitness.setValueBaja(0, 11);
			//this.fitness.setValueBaja(0, 12);
			//this.fitness.setValueBaja(0, 13);
			//this.fitness.setValueBaja(0, 14);
			//this.fitness.setValueBaja(0, 15);
			//this.fitness.setValueBaja(0, 16);
			//this.fitness.setValueBaja(0, 17);
			//this.fitness.setValueBaja(0, 18);
			//this.fitness.setValueBaja(0, 19);
			//this.fitness.setValueBaja(0, 20);
			//this.fitness.setValueBaja(0, 21);
			//this.fitness.setValueBaja(0, 22);
			//this.fitness.setValueBaja(0, 23);
			//this.fitness.setValueBaja(0, 24);
			//this.fitness.setValueBaja(0, 25);
			//this.fitness.setValueBaja(0, 26);
			//this.fitness.setValueBaja(0, 27);
			//this.fitness.setValueBaja(0, 28);
			//this.fitness.setValueBaja(0, 29);
			//this.fitness.setValueBaja(0, 30);
			//this.fitness.setValueBaja(0, 31);
			//this.fitness.setValueBaja(0, 32);
			//this.fitness.setValueBaja(0, 33);
			//this.fitness.setValueBaja(0, 34);
			//this.fitness.setValueBaja(0, 35);
			//this.fitness.setValueBaja(0, 36);
			//this.fitness.setValueBaja(0, 37);
			//this.fitness.setValueBaja(0, 38);
			//this.fitness.setValueBaja(0, 39);
			
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
	
	/**
	 * Permite acceso al monitor para configuración personalizada
	 */
	public AlgorithmMonitor getMonitor() {
		return this.monitor;
	}
}


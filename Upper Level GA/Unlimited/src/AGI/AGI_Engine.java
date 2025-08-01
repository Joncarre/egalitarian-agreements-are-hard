package AGI;

import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class AGI_Engine {
	private final int avgIterations = 1; // Average Iterations
	private final int generations = 100000; // Evolutions of population
	private final int popSize = 30; // Population Size
	private final int numUsers = 8; // Number of agents
	private AGI_FitnessCalc fitness = new AGI_FitnessCalc(this.numUsers);
	private AGI_Population myPop;
	Scanner sc = new Scanner(System.in);

	/**
	 * 
	 * Execute IGA
	 * @return
	 * @throws IOException
	 */
	public AGI_Individual executeIGA(int indiv_i, boolean firstTime) throws IOException {	
		/* change 'op' to chose read data or generate population */
		int op = 1; 
		if(op == 1) {
			this.fitness.resetValues(avgIterations);
			for(int j = 0; j < avgIterations; j++) { // Loop to select our best egalitarian result
		    	this.myPop = new AGI_Population(this.popSize, this.numUsers, false);
		    	int generationCount = 0;				while (generationCount < this.generations) {
			        generationCount++;
					//System.out.println("Ite: " + generationCount + " Fitness(lowest): " + myPop.getIndividual(0).getOnlyFitness()  + " Genes: " + myPop.getIndividual(0).toString());
			        this.myPop = AGI_Algorithm.evolvePopulation(this.myPop, this.popSize, generationCount);
				}
				this.fitness.saveIndividuals(myPop.getIndividual(0));
			}
		}else if(op == 2) {
			generatePopulation();
			System.exit(0);
		}
		else 
			System.out.println("Bye, Bye!");
		return this.fitness.getBestIndividual();
	}
	
	/**
	 * Generate a new Population to files
	 * @throws IOException
	 */	public void generatePopulation() throws IOException {
		System.out.println("ATENTION, new Population for IGA will be generate. Press ENTER to continue");
		this.sc.nextLine();
	    // Create new population - constructor handles file generation
	    @SuppressWarnings("unused")
	    AGI_Population myPop = new AGI_Population(this.popSize, this.fitness.getNumUsers(), true);
		System.out.println("Data generated");
	}
	
	/**
	 * Used to read preferences from AGS
	 * @param M_preferences
	 */
	public void setM_Preferences(Vector<Vector<Double>> M_preferences) {
		this.fitness.setM_Preferences(M_preferences);
	}
	
	/**
	 * Used to read preferences from AGS
	 * @param O_preferences
	 */
	public void setO_Preferences(Vector<Vector<Double>> O_preferences) {
		this.fitness.setO_Preferences(O_preferences);
	}
}


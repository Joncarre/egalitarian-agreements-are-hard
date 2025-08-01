package AGS;

import java.io.IOException;
import java.util.Scanner;
import AGI.AGI_Engine;
import files.CustomWriteFile;

public class AGS_Engine {
	private final int maxGenerations;
	private final int populationSize;
	private final int numUsers;
	private AGS_FitnessCalc fitness;
	private AGS_Population myPop;
	Scanner sc;
	private CustomWriteFile writeFile;
	public static String textFittest = "";
	public String textSolutions = "";
	public String textFitnessAgent0 = "";
	
	/**
	 * Constructor
	 */
	public AGS_Engine() {
		this.maxGenerations = 100000;
		this.populationSize = 20;
		this.numUsers = 8;
		this.fitness = new AGS_FitnessCalc(this.numUsers);
		this.sc = new Scanner(System.in);
	}
	
	/**
	 * This method controls the flow of execution
	 * @throws IOException
	 */
	public void start() throws IOException {
		System.out.println("    (1) Execute SGA \n" + 
						   "    (2) Gerenate new Preferences \n" +  
						   "    (3) Gerenate new Population \n" + 
						   "    (*) Exit");
		int op = this.sc.nextInt();
		if(op == 1) 
			executeSGA();
		else if(op == 2) {
			//generatePreferences();
			System.exit(0);
		}else  if(op == 3) {
			generatePopulation();
			System.exit(0);
		}else
			System.out.println("Bye, Bye!");
	}
	
	/**
	 * Read population from file and execute GA
	 * @throws IOException
	 */
	public void executeSGA() throws IOException {
		this.fitness.readPreferences_O();
		this.fitness.readPreferences_M();
		this.myPop = new AGS_Population();
		this.myPop.readPopulation(this.populationSize, this.numUsers);
		int generationCount = 0;
		AGI_Engine engine = new AGI_Engine();
        engine.setO_Preferences(this.fitness.getO_Preferences());
        engine.setM_Preferences(this.fitness.getO_Preferences());
		while (generationCount < this.maxGenerations) {
			//double t_1 = System.currentTimeMillis(); 
			generationCount++;
			this.myPop = AGS_Algorithm.evolvePopulation(this.myPop, engine, populationSize, generationCount);
			printBest();
			printSolution();
			printFitnessAgent0();
			if(generationCount == 1000) {
				System.out.println("-------------------------------------------------------------------------------------------------");
				for(int i = 0; i < populationSize; i++) {
					System.out.println(this.myPop.getIndividual(i).toString());
				}
				System.out.println("-------------------------------------------------------------------------------------------------");
			}
			//System.out.println("time: " + (System.currentTimeMillis() - t_1));
		}
		System.out.println("End.");
	}
	
	/**
	 * Almacena los genes de los individuos fittest del AGS
	 * @throws IOException
	 */
	public void printBest() throws IOException{
    	this.writeFile = new CustomWriteFile("result_Fittest.txt");
    	String textConsole = this.myPop.getIndividual(0).toString() + "\n";
    	AGS_Engine.setText(textConsole);
    	this.writeFile.writeVector(this.writeFile, AGS_Engine.getText().replace('.', ','));
    	this.writeFile.closeWriteFile(this.writeFile);
	}
	
	/**
	 * Almacena los genes de los individuos fittest del AGI y la utilidad del Agente 0 con las preferencias reales
	 * @throws IOException
	 */
	public void printSolution() throws IOException{
    	this.writeFile = new CustomWriteFile("result_Solution.txt");
    	int[] solution = new int[AGS_Individual.defaultGeneLength];
    	solution = this.myPop.getIndividual(0).getSolution();
		// Almacenamos el fitness del Agente 0
		double fitness = 0.0;
		fitness = this.fitness.fitnessAgent0(solution);
		String text1 = Double. toString(fitness);
		// Almacenamos la solucion del AGI (conjunto de leyes aprobadas o no aprobadas)
    	String text2 = "";
    	for(int i = 0; i < solution.length; i++) {
    		text2 += solution[i] + " ";
    	}
    	text2 += "\n";
    	String textoFinal = text1 + "     " + text2;
    	this.textSolutions += textoFinal;
    	System.out.print(text1.replace('.', ',') + "     " + this.myPop.getIndividual(0).toString() + "\n");
    	this.writeFile.writeVector(this.writeFile, textSolutions.replace('.', ','));
    	this.writeFile.closeWriteFile(this.writeFile);
	}
	
	/**
	 * Almacena la utilidad del Agente 0 con las preferencias modificadas
	 * @throws IOException
	 */
	public void printFitnessAgent0() throws IOException{
    	this.writeFile = new CustomWriteFile("result_FitnessAgent0_M.txt");
    	int[] solution = new int[AGS_Individual.defaultGeneLength];
    	solution = this.myPop.getBest().getSolution();
		// Almacenamos el fitness del Agente 0
		double fitness = 0.0;
		fitness = this.fitness.fitnessAgent0_M(solution, this.myPop.getIndividual(0));
		String text1 = Double.toString(fitness);
    	text1 += "\n";
    	String textoFinal = text1;
    	this.textFitnessAgent0 += textoFinal;
    	this.writeFile.writeVector(this.writeFile, textFitnessAgent0.replace('.', ','));
    	this.writeFile.closeWriteFile(this.writeFile);
	}
	
	/**
	 * Generate a new Population to files
	 * @throws IOException
	 */
	private void generatePopulation() throws IOException {
		System.out.println("ATENTION, new Population will be generate. Press ENTER to continue");
		this.sc.nextLine(); this.sc.nextLine();
	    this.myPop = new AGS_Population(this.populationSize, this.numUsers);
		System.out.println("Data generated");
	}
	
	public static void setText(String text){
		AGS_Engine.textFittest += text;
	}
	
	public static String getText(){
		return AGS_Engine.textFittest;
	}
}


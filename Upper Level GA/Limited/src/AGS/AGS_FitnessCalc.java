package AGS;


import java.io.IOException;

import java.util.Scanner;
import java.util.Vector;



import AGI.AGI_Engine;
import AGI.AGI_Individual;
import files.CustomReadFile;
import files.CustomWriteFile;

public class AGS_FitnessCalc {
	private static Vector<Vector<Double>> O_preferences;
	private static Vector<Vector<Double>> M_preferences;
	private static Vector<Integer> positive;
	private static Vector<Integer> negative;
	static int numUsers;
	private int maxValuePreference;
	private CustomReadFile readFile;
	private CustomWriteFile writeFile;
	Scanner sc;
	
	/**
	 * Constructor
	 * @param numUser
	 * @param maxValuePreference
	 */
	public AGS_FitnessCalc(int _numUser) {
		this.numUsers = _numUser;
		this.O_preferences = new Vector<Vector<Double>>(this.numUsers);
		this.M_preferences = new Vector<Vector<Double>>(this.numUsers);
		this.positive = new Vector<Integer>(AGS_Individual.defaultGeneLength);
		this.negative = new Vector<Integer>(AGS_Individual.defaultGeneLength);
		this.maxValuePreference = 99;
	}
	
	/**
	 * Read O preferences for each user
	 * @throws IOException
	 */
    public void readPreferences_O() throws IOException {
        for (int i = 0; i < numUsers; i++) {
    		this.readFile = new CustomReadFile("agent" + i + ".txt");
        	this.sc = new Scanner(this.readFile);
        	Vector<Double> newPreference = this.readFile.readVector(sc);
        	// Formalizar las preferencias leidas
            O_preferences.add(i, formalizeSets(newPreference));
        }
    }
    
    /**
     * Read M preferences for each user
     * @throws IOException
     */
    public void readPreferences_M() throws IOException {
        for (int i = 0; i < numUsers; i++) {
    		this.readFile = new CustomReadFile("agent" + i + ".txt");
        	this.sc = new Scanner(this.readFile);
        	Vector<Double> newPreference = this.readFile.readVector(sc);
        	// Formalizar las preferencias leidas
        	M_preferences.add(i, formalizeSets(newPreference));
        }
    }
    
    /**
     * Write population on files
     * @param indiv
     * @throws IOException
     */
    public void writePopulation(AGS_Individual indiv) throws IOException {
		for(int i = 0; i < numUsers; i++) {
			String text = "";
			this.writeFile = new CustomWriteFile("final_indiv_" + i + ".txt");
			for(int j = 0; j < AGS_Individual.defaultGeneLength; j++)
				text += indiv.getGene(j) + " ";
			text += -1.0;
			this.writeFile.writeVector(this.writeFile, text);
			this.writeFile.closeWriteFile(this.writeFile);
		}
    }
    /*
    /**
     * Write O preference for each user
     * @throws IOException
     */
    /*public void writePreferences_O() throws IOException {
		for(int i = 0; i < numUsers; i++) {
			String text = "";
			this.writeFile = new CustomWriteFile("user_U_" + i + ".txt");
			for(int j = 0; j < AGS_Individual.defaultGeneLength; j++)
				text += O_preferences.get(i).get(j) + " ";
			text += -1.0;
			this.writeFile.writeVector(this.writeFile, text);
			this.writeFile.closeWriteFile(this.writeFile);
		}
    }*/
    
    /**
     * Write M preference for each user
     * @throws IOException
     */
    public void writePreferences_M() throws IOException {
		for(int i = 0; i < numUsers; i++) {
			String text = "";
			this.writeFile = new CustomWriteFile("agent" + i + ".txt");
			for(int j = 0; j < AGS_Individual.defaultGeneLength; j++)
				text += M_preferences.get(i).get(j) + " ";
			text += -1.0;
			this.writeFile.writeVector(this.writeFile, text);
			this.writeFile.closeWriteFile(this.writeFile);
		}
    }

    /**
     * Calculate individuals fitness
     * @param individual
     * @return
     * @throws IOException 
     */
    public static double getFitness(AGI_Engine engine, AGS_Individual individual, int indiv_i) throws IOException {
        // Sustituimos al individuo i-�simo (recordemos que cada individuo del SGA es una asignaci�n de recursos del U_0)
    	M_preferences.set(0, individual.getGenes());
    	engine.setM_Preferences(M_preferences);
    	// El fitness se calcula antes para que el primero de todos (cuando U_0 = R_0) no se modifique
    	AGI_Individual bestAGI = engine.executeIGA(indiv_i, false);
    	individual.setFitness(fitnessAgent0(bestAGI.getGenes())); // Guardar el fittest del AGI pero calculado con las preferencias reales del Agente 0
    	individual.setFitnessAGI(bestAGI.getFitness()); // Guardar el fittest del AGI
    	individual.setSolution(bestAGI.getGenes());
    	return individual.getOnlyFitness();
    } 
    
    /**
     * Get number of users
     * @return
     */
    public int getNumUsers() {
    	return this.numUsers;
    }
    
	/**
	 * Used to get preferences to send it to AGI
     * @return
     */
    public Vector<Vector<Double>> getM_Preferences(){
    	return this.M_preferences;
    }
    
	/**
	 * Used to get preferences to send it to AGI
     * @return
     */
    public Vector<Vector<Double>> getO_Preferences(){
    	return this.O_preferences;
    }
    
    /**
     * Normaliza el vector haciendo que el valor absoluto sea igual a 1000
     */
    public Vector<Double> formalizeSets(Vector<Double> preference) {
    	double auxPositive = 0;
    	double auxNegative = 0;
    	for(int i = 0; i < preference.size(); i++) {
    		if(preference.get(i) > 0)
    			auxPositive += Math.abs(preference.get(i));
    		else
    			auxNegative += Math.abs(preference.get(i));
    	}
    	auxPositive = 1000/auxPositive;
    	auxNegative = 1000/auxNegative;
    	for(int j = 0; j < preference.size(); j++) {
    		if(preference.get(j) > 0)
    			preference.set(j, preference.get(j)*auxPositive);
    		else
    			preference.set(j, preference.get(j)*auxNegative);
    	}
    	return preference;
    }
    
    /**
     * Metodo para aniadir el fitness del agent 0 al fichero result_solution.txt
     * @param solution
     * @return
     */
    public static double fitnessAgent0(int[] solution) {
    	double fitness = 0.0;
    	for(int i = 0; i < solution.length; i++) {
    		fitness += (solution[i]*O_preferences.get(0).get(i));
    	}
    	return fitness;
    }
    
    /**
     * Metodo para aniadir el fitness del agent 0 al fichero result_solution.txt
     * @param solution
     * @return
     */
    public double fitnessAgent0_M(int[] solution, AGS_Individual individual) {
    	double fitness = 0.0;
    	for(int i = 0; i < solution.length; i++) {
    		fitness += (solution[i]*individual.getGene(i));
    	}
    	return fitness;
    }
}
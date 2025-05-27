package main;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import data.CustomReadFile;
import data.CustomWriteFile;

public class FitnessCalc {
	private static Vector<Vector<Double>> M_preferences;
	private static Vector<Vector<Double>> O_preferences;
	private static Vector<Double> Temporal_preferences; 
	private static Vector<Integer> changedResources;
	static int numUsers;
	private int maxValuePreference;
	private int minValuePreference;
	private Vector<Individual> indiv_Results;
	private Vector<Individual> M_population;
	private CustomReadFile readFile;
	private CustomWriteFile writeFile;
	Scanner sc;
	
	/**
	 * Constructor
	 * @param _numUser
	 * @param _numPacks
	 */
	public FitnessCalc(int _numUser) {
		this.numUsers = _numUser;
		this.maxValuePreference = 999;
		this.minValuePreference = -999;
		this.M_preferences = new Vector<Vector<Double>>(this.numUsers);
		this.O_preferences = new Vector<Vector<Double>>(this.numUsers);
		this.changedResources = new Vector<Integer>(Individual.defaultGeneLength);
	}
	
	/**
	 * Reads M preferences
	 * @param index
	 * @param newPreference
	 * @throws IOException 
	 */
    public void readMPreferences() throws IOException {
        // Set a user preferences for each resource
        for (int i = 0; i < numUsers; i++) {
    		this.readFile = new CustomReadFile("agent" + i + ".txt");
        	this.sc = new Scanner((Readable) this.readFile);
        	Vector<Double> newPreference = this.readFile.readVector(sc);
        	M_preferences.add(i, newPreference);
        }
    }
    
    /**
     * Reads O preferences
     * @throws IOException
     */
    public void readOPreferences() throws IOException {
    	this.readFile = new CustomReadFile("agent0_real.txt");
        this.sc = new Scanner((Readable) this.readFile);
        Vector<Double> newPreference = this.readFile.readVector(sc);
        O_preferences.add(0, newPreference);
    }
   
    /**
     * Writes preferences for each user
     * @throws IOException
     */
    public void writePreferences() throws IOException {
		for(int i = 0; i < numUsers; i++) {
			String text = "";
			this.writeFile = new CustomWriteFile("agent" + i + ".txt");
			for(int j = 0; j < Individual.defaultGeneLength; j++)
				text += M_preferences.get(i).get(j) + " ";
			text += -1000;
			this.writeFile.writeVector(this.writeFile, text);
			this.writeFile.closeWriteFile(this.writeFile);
		}
    }
    
    /**
     * Generates random preferences for each user
     */
    public void randomPreferences() {
    	Random random = new Random();
        for (int i = 0; i < numUsers; i++) {
        	Vector<Double> newPreference = new Vector<Double>(Individual.defaultGeneLength);
        	for(int j = 0; j < Individual.defaultGeneLength; j++)  
        		newPreference.add(j, (double) (random.nextInt(this.maxValuePreference - this.minValuePreference) + this.minValuePreference));	
        	M_preferences.add(i, newPreference);
        }
    }

    /**
     * Calculate individuals fitness
     * @param individual
     * @return
     */
    public static double getFitness(Individual individual) {
    	// The assignments for each resource is a vector of numUsers positions
    	double[] assignments = new double[numUsers];
        // Loop through our individuals genes
        for (int i = 0; i < Individual.defaultGeneLength; i++) {
        	 for(int j = 0; j < numUsers; j++)
        		 assignments[j] += individual.getGene(i) * M_preferences.get(j).get(i); 
        }
        return getMinValue(assignments); 
	}

	
    /**
     * Return of minimum value
     * @param vector
     * @return
     */
    public static double getMinValue(double[] vector) {
    	double min = Integer.MAX_VALUE;
    	for(int i = 0; i < vector.length; i++) {
    		if(vector[i] < min)
    			min = vector[i];
    	}
    	return min;
    }
    
    /**
     * Get number of users
     * @return
     */
    public int getNumUsers() {
    	return numUsers;
    }
    
    /**
     * resetValues
     */
    public void resetValues(int avgIterations) {
    	this.indiv_Results = new Vector<Individual>(avgIterations);
    }
    
    /**
     * Reset matrix of populations
     * @param cols
     */
    public void resetPopulations(int popSize) {
    	this.M_population = new Vector<Individual>(popSize);
    }
    
    public void showBestFitness() {
    	Individual best = new Individual();
    	best = getBestIndividual();
    	String fitness = "";
    	fitness += best.getOnlyFitness();
    	System.out.println(" " + fitness.replace(".", ",") + " " + best.toString());
    }
    
    /** 
     * Write to file all results
     * @param numIterations
     * @param preference
     * @throws 
     */
    public void updateBestFitness() throws IOException {
    	String TotalFitness = "";
		// Elegimos el mejor individuo de la poblaci�n
    	Individual best = new Individual();
    	best = getBestIndividual();
		// Calculamos el fitness del Agente 0 con las preferencias originales
    	double fitness = 0.0;
        for(int i = 0; i < O_preferences.get(0).size(); i++) {
			fitness += best.getGene(i) * O_preferences.get(0).get(i); 
        }
        TotalFitness += fitness;
		System.out.print(TotalFitness.replace(".", ",") + " " + best.toString());
    }
    
    /**
     * 
     */
    public void initialResources() {
    	for(int i = 0; i < Individual.defaultGeneLength; i++) {
        	changedResources.add(i);
    	}
    }
    
    /**
     * 
     * @param resource
     */
    public void saveResources(Integer resource) {
    	changedResources.remove(resource);
    }
    
    /**
     * 
     */
    public void saveTemporalPreferences() {
    	Temporal_preferences = new Vector<Double>(Individual.defaultGeneLength);
    	for(int i = 0; i < Individual.defaultGeneLength; i++) {
    		Temporal_preferences.add(i, M_preferences.get(0).get(i));
    	}
    }
    
    /**
     * 
     * @param resources
     * @return
     */
    public boolean distributionVariationAlza() {
    	double totalVariation = 0.0;
    	for(int k = 0; k < M_preferences.get(0).size(); k++) {
    		if(!changedResources.contains(k)) // S�lo se acumula la distribuci�n de los n-m recursos
    			totalVariation += M_preferences.get(0).get(k) - Temporal_preferences.get(k);
    	}
    	
    	double sumaTotal = 0.0;
    	for(int r = 0; r < changedResources.size(); r++) {
    		sumaTotal += M_preferences.get(0).get(changedResources.get(r));
    	}

    	boolean seguir = true;
	    for(int i = 0; i < (changedResources.size()); i++) {
	    	double oldValue = M_preferences.get(0).get(changedResources.get(i));
	    	double newValue = oldValue-(oldValue/sumaTotal)*totalVariation;
	    	if(newValue > 0) {
	    		M_preferences.get(0).set(changedResources.get(i), newValue);
	    	}else {
				seguir = false;
	    	}
	    }
	    return seguir;
    }
    
    /**
     * 
     * @param resources
     * @return
     */
    public boolean distributionVariationBaja() {
    	double totalVariation = 0.0;
    	// Acumulamos la distribuci�n 
    	for(int k = 0; k < M_preferences.get(0).size(); k++) {
    		if(!changedResources.contains(k)) // S�lo se acumula la distribuci�n de los n-m recursos
    			totalVariation += Temporal_preferences.get(k) - M_preferences.get(0).get(k);
    	}
    	
    	// Calculamos cu�nto vale la diferencia entre el valor de preferencia de cada m recurso con 100 (sumatorio de preferencia 100 - m_i)
    	double sumaTotal = 0.0;
    	for(int r = 0; r < changedResources.size(); r++) {
    		sumaTotal += (100 - M_preferences.get(0).get(changedResources.get(r)));
    	}
    	
    	boolean seguir = true;
    	// Repartimos la variaci�n y se lo a�adimos un poquito a los m recursos
	    for(int i = 0; i < (changedResources.size()); i++) {
	    	// Cogemos el valor act�al que tiene el recurso m_i
 	    	double oldValue = M_preferences.get(0).get(changedResources.get(i));
 	    	// Hacemos que ese valor sea el que ten�a, m�s un % que se le a�ade en funci�n de cu�nto le queda para llegar a 100
	    	double newValue = oldValue+((100-oldValue)/sumaTotal)*totalVariation;
	    	if(newValue < 100) { // Si el nuevo valor inferior a 100, se modifica
	    		M_preferences.get(0).set(changedResources.get(i), newValue);
	    	}else { // Si el nuevo valor es superior a 100, ya no es posible a�adir m�s valor a los m recursos
				seguir = false;
			}
	    }
	    return seguir;
    }
    
    public boolean setValueAlza(int row, int col) {
    	//System.out.println(M_preferences.get(row));
    	
    	boolean result = false;
    	double oldValue = M_preferences.get(row).get(col);
    	if(oldValue > 0) { // Si he votado positivamente la ley
    		double newValue = (int) (oldValue+((1000-oldValue)*0.5)); // 50% del valor que le queda por crecer (mientras < 1000)
    		if(newValue < 1000) {
        		M_preferences.get(row).set(col, newValue);
        		result = true;
    		}
    	}else if(oldValue < 0) { // Si he votado negativamente la ley
    		double valuePositive = oldValue*-1; // Paso el valor a positivo
    		double newValue = (int) (oldValue+((valuePositive-1)*0.5)); // 50% del valor que le queda por crecer (mientras < 0)
    		if(newValue < 0) {
        		M_preferences.get(row).set(col, newValue);
        		result = true;
    		}
    	}else {
    		result = false;
    	}
		return result;
    }
    
    public boolean setValueBaja(int row, int col) {
    	//if(col == 19)
        	//System.out.println(M_preferences.get(0));
    		
    	boolean result = false;
    	double oldValue = M_preferences.get(row).get(col);
    	if(oldValue > 0) { // Si he votado positivamente la ley
    		double newValue = (int) (oldValue-(oldValue*0.5)); // 50% del valor que le queda por decrecer (mientras > 0)
    		if(newValue > 0) {
        		M_preferences.get(row).set(col, newValue);
        		result = true;
    		}
    	}else if(oldValue < 0) { // Si he votado negativamente la ley
    		double newValue = (int) (oldValue-((1000+oldValue)*0.5)); // 50% del valor que le queda por decrecer (mientras > -1000)
    		if(newValue > -1000) {
        		M_preferences.get(row).set(col, newValue);
        		result = true;
    		}		
    	}else {
    		result = false;
    	}
		return result;
    }
	
    public void saveIndividuals(Individual individual) {
    	this.indiv_Results.add(individual);
    }
    
    public Individual getBestIndividual() {
    	Individual best = new Individual();
    	best = this.indiv_Results.get(0);
    	for(int i = 0; i < this.indiv_Results.size(); i++) {
    		if(this.indiv_Results.get(i).getOnlyFitness() > best.getOnlyFitness()) {
    			best = this.indiv_Results.get(i);
    		}
    	}
    	return best;
    }
    
    public void printMPreferences(){
    	System.out.println(M_preferences.get(0).toString());
    }
    
    /**
     * This method formalizes the preferences by making them all add a number n (in our case it will be 100)
     */
    /*
    public Vector<Double> formalizePreferences(Vector<Double> preference) {
    	double aux = 0;
    	for(int i = 0; i < preference.size(); i++)
    		aux += preference.get(i);
    	aux = 100/aux;
    	for(int j = 0; j < preference.size(); j++)
    		preference.set(j, preference.get(j)*aux);
    	return preference;
    }
    
    public void formalizeMutationPreferences() {
    	M_preferences.set(0, formalizePreferences(M_preferences.get(0)));
    }
    */
}

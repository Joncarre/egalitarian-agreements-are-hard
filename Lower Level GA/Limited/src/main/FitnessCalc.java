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
        	// Formalizar las preferencias leidas
        	M_preferences.add(i, formalizePreferences(newPreference));
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
    	// Formalizar las preferencias leidas
        O_preferences.add(0, formalizePreferences(newPreference));
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
    
    public boolean distributionVariation() {
    	double totalVariation = 0.0;
    	// Acumulamos la distribución 
    	for(int k = 0; k < M_preferences.get(0).size(); k++) {
    		if(!changedResources.contains(k)) { // Sólo se acumula la distribución absoluta de los n-m recursos#
    			totalVariation += Math.abs(M_preferences.get(0).get(k)) - (Math.abs(Temporal_preferences.get(k)));
    		}
    	}
    	boolean variacionNegativa = false;
    	// Como la variación puede salir negativa, la transformamos en positiva
    	if(totalVariation < 0) {
    		totalVariation *= -1;
    		variacionNegativa = true;
    	}

    	// Calculamos el margen que tenemos para la distribucion
    	double margenModificacion = 0.0; // Usado para distribuir la acumulacion entre los n-m recursos (toma el valor de preferencia de la ley i-esima)
    	double margenReal = 0.0; // Usado para ver si es posible distribuir la acumulacion o si nos pasamos (toma el valor hasta 0 o a hasta -1000)
    	for(int r = 0; r < M_preferences.get(0).size(); r++) {
    		if(changedResources.contains(r)) {
    			if(M_preferences.get(0).get(r) > 0) {
    				margenReal += 1000-(Math.abs(M_preferences.get(0).get(r)));
    				margenModificacion += (Math.abs(M_preferences.get(0).get(r)));
    			}else {
    				margenReal += (Math.abs(M_preferences.get(0).get(r)));
    				margenModificacion += (Math.abs(M_preferences.get(0).get(r)));
    			}
    		}
    	}
    	double proporcionalidadk = totalVariation/margenModificacion;
    	boolean seguir = true;
    	double test = 0.0;
	    double testNegative = 0;
    	if(Math.abs(totalVariation) >= margenReal) // Si no puedo distribuir la totalVariation entre los m recursos, terminamos
    		seguir = false;
    	else { // Si puedo, entonces lo distribuyo
    		if(variacionNegativa == false) {
            	// Repartimos la variación y se lo añadimos un poquito a los m recursos segun la proporcionalidadK
        		for(int i = 0; i < M_preferences.get(0).size(); i++) {
        			if(changedResources.contains(i)) {
            	    	// Cogemos el valor actúal que tiene el recurso m_i
             	    	double oldValue = M_preferences.get(0).get(i);
             	    	double incremento = proporcionalidadk*(Math.abs(oldValue)); // Calculo la proporcionalidad en valor absoluto
             	    	test += incremento;

             	    	if(oldValue > 0) {
             	    		double newValue = oldValue - incremento;
            	 	   	    M_preferences.get(0).set(i, (double) newValue);
             	    	}else {
             	    		testNegative += incremento;
             	    		double newValue = oldValue + incremento;
            	 	   	    M_preferences.get(0).set(i, (double) newValue);
             	    	}
        			}
        	    }	
    		}else {
            	// Repartimos la variación y se lo añadimos un poquito a los m recursos segun la proporcionalidadK
        		for(int i = 0; i < M_preferences.get(0).size(); i++) {
        			if(changedResources.contains(i)) {
            	    	// Cogemos el valor actúal que tiene el recurso m_i
             	    	double oldValue = M_preferences.get(0).get(i);
             	    	double incremento = proporcionalidadk*(Math.abs(oldValue)); // Calculo la proporcionalidad en valor absoluto
             	    	test += incremento;

             	    	if(oldValue > 0) {
             	    		double newValue = oldValue + incremento;
            	 	   	    M_preferences.get(0).set(i, (double) newValue);
             	    	}else {
             	    		testNegative += incremento;
             	    		double newValue = oldValue - incremento;
            	 	   	    M_preferences.get(0).set(i, (double) newValue);
             	    	}
        			}
        	    }
    		}
    	}
    	
    	for(int i = 0; i < M_preferences.get(0).size(); i++) {
    		if(Temporal_preferences.get(i) > 0) {
    			if(M_preferences.get(0).get(i) < 0)
    				seguir = false;
    		}else {
    			if(M_preferences.get(0).get(i) > 0)
        			seguir = false;
    		}
    	}
    	//System.out.println(M_preferences.get(0));
    	return seguir;
    } 
    
    public boolean setValueAlza(int row, int col) {
    	boolean result = false;
    	double oldValue = M_preferences.get(row).get(col);
    	if(oldValue > 0) { // Si he votado positivamente la ley
    		double newValue = (oldValue+(oldValue*0.1)); // 10% del valor absoluto
    		if(newValue < 1000) {
        		M_preferences.get(row).set(col, newValue);
        		result = true;
    		}
    	}else if(oldValue < 0) { // Si he votado negativamente la ley
    		double valuePositive = (oldValue*-1); // Paso el valor a positivo
    		double newValue = (oldValue-((valuePositive-1)*0.1)); // 10% del valor absoluto
    		if(newValue < 0) {
        		M_preferences.get(row).set(col, (double) newValue);
        		result = true;
    		}
    	}else {
    		result = false;
    	}
		return result;
    }
    
    public boolean setValueBaja(int row, int col) {
    	boolean result = false;
    	double oldValue = M_preferences.get(row).get(col);
    	if(oldValue > 0) { // Si he votado positivamente la ley
    		double newValue = (oldValue-(oldValue*0.1)); // 10% del valor que le queda por decrecer (mientras > 0)
    		if(newValue > 0) {
        		M_preferences.get(row).set(col, newValue);
        		result = true;
    		}
    	}else if(oldValue < 0) { // Si he votado negativamente la ley
    		double newValue = (oldValue+(Math.abs(oldValue*0.1))); // 10% del valor que tiene actualmente (mientras > -1000)
    		if(newValue < 0) {
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
    public Vector<Double> formalizePreferences(Vector<Double> preference) {
    	double aux = 0;
    	for(int i = 0; i < preference.size(); i++)
    		aux += Math.abs(preference.get(i));
    	aux = 1000/aux;
    	for(int j = 0; j < preference.size(); j++)
    		preference.set(j, preference.get(j)*aux);
    	return preference;
    }
}

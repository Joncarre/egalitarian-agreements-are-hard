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
	private static Vector<Integer> positive;
	private static Vector<Integer> negative;
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
		this.positive = new Vector<Integer>(Individual.defaultGeneLength);
		this.negative = new Vector<Integer>(Individual.defaultGeneLength);
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
        	M_preferences.add(i, formalizeSets(newPreference));
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
        O_preferences.add(0, formalizeSets(newPreference));
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
		// Elegimos el mejor individuo de la poblaci n
    	Individual best = new Individual();
    	best = getBestIndividual();
		//System.out.println("----> Fittest of AGI: " + best.getOnlyFitness() + "  |  " + best.toString());
		// Calculamos el fitness del Agente 0 con las preferencias originales
    	double fitness = 0.0;
        for(int i = 0; i < O_preferences.get(0).size(); i++) {
			fitness += best.getGene(i) * O_preferences.get(0).get(i); 
        }
        TotalFitness += fitness;
		System.out.print(TotalFitness.replace(".", ",") + " " + best.toString());
        //System.out.println(TotalFitness.replace(".", ","));
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
    
    public boolean distributionVariationPositive() {
    	double totalVariation = 0.0;
    	// Acumulamos la distribuci n 
    	for(int k = 0; k < M_preferences.get(0).size(); k++) {
    		if(!changedResources.contains(k)) // Si lo se acumula la distribucion absoluta de los n-m recursos
    			if(M_preferences.get(0).get(k) > 0)
    				totalVariation += Math.abs(Temporal_preferences.get(k)) - Math.abs(M_preferences.get(0).get(k));
    	}
    	// Como la variacion puede salir negativa, la transformamos en positiva
    	if(totalVariation < 0)
    		totalVariation *= -1;
    	// Calculamos el margen que tenemos para la distribucion
    	double margenModificacion = 0.0;
    	double margenReal = 0.0;
    	for(int r = 0; r < M_preferences.get(0).size(); r++)
			if(changedResources.contains(r))
    			if(M_preferences.get(0).get(r) > 0) {
    				margenReal += 1000-(Math.abs(M_preferences.get(0).get(r)));
    				margenModificacion += (Math.abs(M_preferences.get(0).get(r)));
    			}
    				
    	double proporcionalidadk = totalVariation/margenModificacion;
    	boolean seguir = true;
    	
    	double test = 0.0;
    	
    	if(totalVariation >= margenModificacion) // Si no puedo distribuir la totalVariation entre los m recursos, terminamos
    		seguir = false;
    	else { // Si si puedo, entonces lo distribuyo
        	// Repartimos la variaci n y se lo a adimos un poquito a los m recursos segun la proporcionalidadK
    	    for(int i = 0; i < M_preferences.get(0).size(); i++) {
    			if(changedResources.contains(i)) {
        			if(M_preferences.get(0).get(i) > 0) {
            	    	// Cogemos el valor act al que tiene el recurso m_i
             	    	double oldValue = M_preferences.get(0).get(i);
             	    	double incremento = proporcionalidadk*(Math.abs(oldValue)); // Calculo la proporcionalidad en valor absoluto
             	    	test += incremento;
             	    	if(oldValue > 0) {
             	    		double newValue = oldValue + incremento;
            	 	   	    M_preferences.get(0).set(i, (double) newValue);
             	    	}
        			}
    			}
    	    }
    	}
    	double total = 0;
	    for(int i = 0; i < M_preferences.get(0).size(); i++) {
			if(M_preferences.get(0).get(i) > 0) {
				total += Math.abs(M_preferences.get(0).get(i));
			}
	    }
    	//System.out.println(M_preferences.get(0).toString());
    	return seguir;
    }
    
    public boolean distributionVariationNegative() {
    	double totalVariation = 0.0;
    	// Acumulamos la distribuci n 
    	for(int k = 0; k < M_preferences.get(0).size(); k++) {
    		if(!changedResources.contains(k)) // S lo se acumula la distribuci n absoluta de los n-m recursos
    			if(M_preferences.get(0).get(k) < 0)
    				totalVariation += Math.abs(M_preferences.get(0).get(k)) - (Math.abs(Temporal_preferences.get(k)));
    	}
    	// Como la variaci n puede salir negativa, la transformamos en positiva
    	if(totalVariation < 0)
    		totalVariation *= -1;
    	// Calculamos el margen que tenemos para la distribucion
    	double margenModificacion = 0.0; // Usado para distribuir la acumulacion entre los n-m recursos (toma el valor de preferencia de la ley i-esima)
    	double margenReal = 0.0; // Usado para ver si es posible distribuir la acumulacion o si nos pasamos (toma el valor hasta 0 o a hasta -1000)
    	for(int r = 0; r < M_preferences.get(0).size(); r++)
			if(changedResources.contains(r))
    			if(M_preferences.get(0).get(r) < 0) {
    				margenReal += (Math.abs(M_preferences.get(0).get(r)));
    				margenModificacion += (Math.abs(M_preferences.get(0).get(r)));
    			}
    				
    	double proporcionalidadk = totalVariation/margenModificacion;
    	boolean seguir = true;
    	double test = 0.0;
    	
    	if(totalVariation >= margenModificacion) // Si no puedo distribuir la totalVariation entre los m recursos, terminamos
    		seguir = false;
    	else { // Si puedo, entonces lo distribuyo
        	// Repartimos la variaci n y se lo a adimos un poquito a los m recursos segun la proporcionalidadK
    	    for(int i = 0; i < M_preferences.get(0).size(); i++) {
    			if(changedResources.contains(i)) {
        			if(M_preferences.get(0).get(i) < 0) {
            	    	// Cogemos el valor act al que tiene el recurso m_i
             	    	double oldValue = M_preferences.get(0).get(i);
             	    	double incremento = proporcionalidadk*(Math.abs(oldValue)); // Calculo la proporcionalidad en valor absoluto
             	    	test += incremento;
             	    	if(oldValue < 0) {
             	    		double newValue = oldValue + incremento;
            	 	   	    M_preferences.get(0).set(i, (double) newValue);
             	    	}
        			}
    			}
    	    }
    	}
    	double total = 0;
	    for(int i = 0; i < M_preferences.get(0).size(); i++) {
			if(M_preferences.get(0).get(i) < 0) {
				total += Math.abs(M_preferences.get(0).get(i));
			}
	    }
    	//System.out.println(M_preferences.get(0).toString());
    	return seguir;
    } 
    
    public boolean setValueBaja(int row, int col) {
    	boolean result = false;
    	double oldValue = M_preferences.get(row).get(col);
    	if(oldValue > 0) { // Si he votado positivamente la ley
    		double newValue = (oldValue-(oldValue*0.03)); // 30% del valor que le queda por decrecer (mientras > 0)
    		if(newValue > 0) {
        		M_preferences.get(row).set(col, newValue);
        		result = true;
    		}
    	}else if(oldValue < 0) { // Si he votado negativamente la ley
    		double newValue = (oldValue-(Math.abs(oldValue*0.03))); // 30% del valor que tiene actualmente (mientras > -1000)
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
    
    // Clasifica en vectores los recursos segun sean positivos o negativos
    public void setLaws() {
    	for(int i = 0; i < O_preferences.get(0).size(); i++) {
    		if(O_preferences.get(0).get(i) > 0)
    			positive.add(i);
    		else
       			negative.add(i);
    	}
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
}

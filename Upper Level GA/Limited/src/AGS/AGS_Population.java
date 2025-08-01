package AGS;

import java.io.IOException;

import java.util.Scanner;
import java.util.Vector;



import AGI.AGI_Engine;
import files.CustomReadFile;
import files.CustomWriteFile;

public class AGS_Population {

	Vector<AGS_Individual> individuals;
	private AGS_Individual bestIndiv;
	private CustomReadFile readFile;
	private CustomWriteFile writeFile;
	Scanner sc;
	
	/**
	 * Empty constructor
	 */
	public AGS_Population() {

	}
	
	/**
	 * Constructor
	 * @param populationSize
	 */
	public AGS_Population(int populationSize) {
    	this.individuals = new Vector<AGS_Individual>(populationSize);
	}

    /**
     * Constructor
     * @param populationSize
     * @param numUsers
     * @param generate
     * @throws IOException
     */
    public AGS_Population(int populationSize, int numUsers) throws IOException{
    	this.individuals = new Vector<AGS_Individual>(populationSize);
        // Loop and create individuals
        for (int i = 0; i < populationSize; i++) {
              AGS_Individual newIndividual = new AGS_Individual();
              newIndividual.generateIndividual(i, numUsers);
        }
    }
    
    /**
     * Read population from file
     * @param populationSize
     * @param numUsers
     * @return 
     * @throws IOException
     */
    public AGS_Population readPopulation(int populationSize, int numUsers) throws IOException{
    	this.individuals = new Vector<AGS_Individual>(populationSize);
        // Loop and create individuals
        for (int i = 0; i < populationSize; i++) {
              AGS_Individual newIndividual = new AGS_Individual();
              newIndividual.readIndividual(i, numUsers);
              saveIndividual(i, newIndividual);
        }
        return this;
    }

    /**
     * Get individual
     * @param index
     * @return
     */
    public AGS_Individual getIndividual(int index) {
        return this.individuals.get(index);
    }
    
    /**
     * Get the best individual
     * @return
     * @throws IOException 
     */
    public AGS_Individual getFittest(AGI_Engine engine, boolean printValue) throws IOException {
     	double fitness_old = Double.NEGATIVE_INFINITY;	
    	for (int i = 0; i < this.individuals.size(); i++) {
         	double fitness_act = getIndividual(i).getFitness(i, engine);
            if (fitness_act > fitness_old) {
            	this.bestIndiv = getIndividual(i);
                fitness_old = fitness_act;
            }
    	}
        return this.bestIndiv;
    }
    
    /**
     * Get population size
     * @return
     */
    public int size() {
        return individuals.size();
    }

    /**
     * Save individual
     * @param index
     * @param indiv
     */
    public void saveIndividual(int index, AGS_Individual indiv) {
        this.individuals.add(index, indiv);
    }
    
    /**
     * 
     * @param index
     * @param indiv
     */
    public void sustituteIndividual(int index, AGS_Individual indiv) {
        this.individuals.set(index, indiv);
    }
    
    public AGS_Individual getBest() {
    	return this.individuals.get(0);
    }
    
    public void setBest(AGS_Individual best) {
    	this.bestIndiv = best;
    }

    /**
     * Normaliza el vector haciendo que el valor absoluto sea igual a 1000
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

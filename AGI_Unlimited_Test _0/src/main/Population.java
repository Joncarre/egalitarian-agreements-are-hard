package main;

import java.io.IOException;

public class Population {

    Individual[] individuals;
	private Individual bestIndiv;

    /**
     * Constructor
     * @param popSize
     * @param numUsers
     * @param generate
     * @throws IOException
     */
    public Population(int popSize, int numUsers, boolean generate) throws IOException {
    	this.individuals = new Individual[popSize]; 
        // Loop and create individuals
        for (int i = 0; i < popSize; i++) {
              Individual newIndividual = new Individual();
              if(generate)
            	  newIndividual.generateIndividual(i, numUsers);
              else
            	  newIndividual.readIndividual(i, numUsers);
              saveIndividual(i, newIndividual);
        }
    }
    
    /**
     * Constructor
     * @param populationSize
     */
    public Population(int populationSize) {
    	this.individuals = new Individual[populationSize];
    }

    /**
     * Get individual
     * @param index
     * @return
     */
    public Individual getIndividual(int index) {
        return individuals[index];
    }
    
    /**
     * Get Fittest
     * @return
     */
    public Individual getFittest() {
        Individual fittest = individuals[0];
        // Loop through individuals to find fittest
        for (int i = 0; i < size(); i++) {
            if (fittest.getFitness() <= getIndividual(i).getFitness())
                fittest = getIndividual(i);
        }
        return fittest;
    }

    /**
     * Get population size
     * @return
     */
    public int size() {
        return individuals.length;
    }

    /**
     * Save individual
     * @param index
     * @param indiv
     */
    public void saveIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }
    
    public void setBest(Individual best) {
    	this.bestIndiv = best;
    }
}

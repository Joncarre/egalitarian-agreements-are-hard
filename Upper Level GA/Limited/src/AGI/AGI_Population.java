package AGI;

import java.io.IOException;

public class AGI_Population {

    AGI_Individual[] individuals;
	private AGI_Individual bestIndiv;

    /**
     * Constructor
     * @param populationSize
     * @param numUsers
     * @param generate
     * @throws IOException
     */
    public AGI_Population(int popSize, int numUsers, boolean generate) throws IOException {
    	this.individuals = new AGI_Individual[popSize]; 
        // Loop and create individuals
        for (int i = 0; i < popSize; i++) {
        	AGI_Individual newIndividual = new AGI_Individual();
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
    public AGI_Population(int populationSize) {
    	this.individuals = new AGI_Individual[populationSize];
    }

    /**
     * Get individual
     * @param index
     * @return
     */
    public AGI_Individual getIndividual(int index) {
        return individuals[index];
    }
    
    /**
     * Get Fittest
     * @return
     */
    public AGI_Individual getFittest() {
        AGI_Individual fittest = individuals[0];
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
    public void saveIndividual(int index, AGI_Individual indiv) {
        individuals[index] = indiv;
    }
    
    /**
     * Save fitness
     * @param i
     * @param onlyFitness
     */
	public void saveFitness(int index, int fitness) {
		this.individuals[index].setFitness(fitness);
	}
	
    public AGI_Individual getBest() {
    	return this.bestIndiv;
    }
    
    public void setBest(AGI_Individual best) {
    	this.bestIndiv = best;
    }
}

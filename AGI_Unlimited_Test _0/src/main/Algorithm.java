package main;

import java.util.Random;


public class Algorithm {
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.05;
    private static final boolean elitism = true;
    private static final double value = 50;

    /**
     * Evolves population
     * @param popEvolved
     * @param popSize
     * @return
     */
    public static Population evolvePopulation(Population popEvolved, int popSize, int generationCount) {
        popEvolved.getFittest(); 
        
        // Is it elitism?
        int elitismOffset;
        if (elitism)
            elitismOffset = 1;
        else
            elitismOffset = 0;
        
        /*System.out.println("Poblaci�n de padres con fitness calculado");
        for(int i = 0; i < popSize; i++){
        	System.out.println("Ite: " + i + " Fitness: " + popEvolved.getIndividual(i).getOnlyFitness() + " � Genes: " + popEvolved.getIndividual(i).toString());
        }
        System.out.println();*/
        
        // Bubble Sort
    	Individual temp1 = new Individual();
        for(int i = 0; i < popSize-1; i++){
        	for(int j = 0; j < (popSize-i-1); j++) {
        		if(popEvolved.getIndividual(j+1).getOnlyFitness() > popEvolved.getIndividual(j).getOnlyFitness()) {
        			temp1 = popEvolved.getIndividual(j);
        			popEvolved.saveIndividual(j, popEvolved.getIndividual(j+1));
        			popEvolved.saveIndividual(j+1, temp1);
        		}
        	}
        }

        // Almacenar �ndices y probabilidades asociadas
    	WeightedRandomSelect<String> indexes = new WeightedRandomSelect<>();
        double newValue = value;
        for(int i = 0; i < popSize; i++){
        	newValue = newValue-(newValue*0.05);
         	indexes.addEntry(i,  newValue);
        }
      
        // Construir la nueva poblaci�n ya cruzada
    	Random random = new Random();
        Population newPop = new Population(popSize);
        for(int i = 0; i < popEvolved.size(); i++){
        	//Individual indiv1 = popEvolved.getIndividual(random.nextInt((popSize-1) - 0) + 0); // Selecci�n random se la vieja poblaci�n
        	Individual indiv1 = popEvolved.getIndividual(indexes.getRandom()); // Selecci�n ponderada seg�n valor fitness
        	Individual indiv2 = popEvolved.getIndividual(indexes.getRandom()); // Selecci�n ponderada seg�n valor fitness
            Individual newIndiv = crossover(indiv1, indiv2);
        	newPop.saveIndividual(i, newIndiv);
        }
   
        /*
        Population definitivePop = new Population(popSize);
        // Por orden, hacemos que compitan (esto no sesga nada porque la elecci�n del cruzamiento ha sido aleatoria)
        for (int i = 0; i < newPop.size(); i++) {
        	if(newPop.getIndividual(i).getOnlyFitness() > popEvolved.getIndividual(i).getOnlyFitness())
        		definitivePop.saveIndividual(i, newPop.getIndividual(i));
        	else
        		definitivePop.saveIndividual(i, popEvolved.getIndividual(i));
        }
        */
        // Mutate population
        for (int i = elitismOffset; i < newPop.size(); i++) {
        	if(Math.random() <= mutationRate) {
        		mutate(newPop.getIndividual(i));
        	}
        }
        
        // Calcular valores fitness de la poblaci�n cruzada
        newPop.getFittest(); 
        
        if(elitism) {
            if(popEvolved.getIndividual(0).getOnlyFitness() > newPop.getIndividual(0).getOnlyFitness())
            	newPop.saveIndividual(0, popEvolved.getIndividual(0));
        }

        // Bubble Sort
    	Individual temp2 = new Individual();
        for(int i = 0; i < newPop.size()-1; i++){
        	for(int j = 0; j < (newPop.size()-i-1); j++) {
        		if(newPop.getIndividual(j+1).getOnlyFitness() > newPop.getIndividual(j).getOnlyFitness()) {
        			temp2 = newPop.getIndividual(j);
        			newPop.saveIndividual(j, newPop.getIndividual(j+1));
        			newPop.saveIndividual(j+1, temp2);
        		}
        	}
        }
        /*
        if(true) {
            System.out.println("Poblaci�n de hijos con fitness calculado y ordenado");
            for(int i = 0; i < newPop.size(); i++){
            	System.out.println("Ite: " + i + " Fitness: " + newPop.getIndividual(i).getOnlyFitness() + " � Genes: " + newPop.getIndividual(i).toString());
            }
            System.out.println();
        }
        */
        return newPop;
    }
    
    /**
     * It makes the copy of an individual
     * @param fittest
     * @return
     */
    public static Individual copyIndiv(Individual fittest){
    	Individual best = new Individual();
        best.setFitness(fittest.getOnlyFitness());
        for(int i = 0; i < fittest.size(); i++)
        	best.setGeneCopy(i, fittest.getGene(i));
    	return best;
    }

    /**
     * Crossover individuals
     * @param indiv1
     * @param indiv2
     * @return
     */
    private static Individual crossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual();
        // Loop through genes
        for (int i = 0; i < indiv1.size(); i++) {
            // Crossover
            if (Math.random() <= uniformRate)
                newSol.setGene(i, indiv1.getGene(i));
            else
                newSol.setGene(i, indiv2.getGene(i));
        }
        return newSol;
    }

    /**
     * Mutate an individual
     * @param indiv
     */
    private static void mutate(Individual indiv) {
    	Random random = new Random();
    	int randomNumber = random.nextInt((indiv.size()-1) - 0) + 0;
        indiv.setGene(randomNumber, (1-indiv.getGene(randomNumber)));
    }
}
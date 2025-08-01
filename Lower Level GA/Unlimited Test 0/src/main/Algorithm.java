package main;

import java.util.Random;

public class Algorithm {
    // Ajustar parámetros para mejor convergencia y evitar estancamiento
    private static final double uniformRate = 0.5;
    private static final double baseMutationRate = 0.03; // Tasa base de mutación
    private static final double mutationRateDecay = 0.9999; // Decaimiento más lento para mantener exploración
    private static final boolean elitism = true;
    private static final int elitismCount = 3; // Equilibrar entre estabilidad y diversidad
    private static final double value = 50;
    private static final double selectionPressure = 1.8; // Ajustado para balancear exploración/explotación
    private static double currentMutationRate = baseMutationRate; // Tasa de mutación dinámica
    private static final double stagThreshold = 0.0001; // Umbral para detectar estancamiento
    private static final int catastropheInterval = 3000; // Cada cuántas generaciones se produce un "cataclismo" parcial
    private static final int explorePhase = 1000; // Fase inicial de exploración agresiva
    private static int noImprovementCounter = 0; // Contador para detectar estancamiento prolongado
    private static double bestFitnessEver = -Double.MAX_VALUE; // Mejor fitness global encontrado

    /**
     * Evolves population
     * @param popEvolved
     * @param popSize
     * @return
     */    public static Population evolvePopulation(Population popEvolved, int popSize, int generationCount) {
        // Obtener el mejor fitness actual
        Individual fittest = popEvolved.getFittest();
        double currentBestFitness = fittest.getOnlyFitness();
        
        // Actualizar el mejor fitness histórico si corresponde
        if (currentBestFitness > bestFitnessEver) {
            bestFitnessEver = currentBestFitness;
            noImprovementCounter = 0; // Reiniciar contador de estancamiento
        } else {
            noImprovementCounter++; // Incrementar contador si no hay mejora
        }
        
        // Control de la tasa de mutación según la fase del algoritmo
        if (generationCount < explorePhase) {
            // Fase inicial: alta exploración
            currentMutationRate = baseMutationRate * 1.5;
        } else {
            // Fase normal: tasa de mutación que disminuye gradualmente
            double progressRatio = Math.min(1.0, generationCount / 20000.0); // Progreso normalizado
            currentMutationRate = baseMutationRate * (Math.pow(mutationRateDecay, generationCount) + 
                                 (1.0 - progressRatio) * 0.015); 
        }
        
        // Determinar cuántos individuos de élite preservar
        int elitismOffset;
        if (elitism)
            elitismOffset = elitismCount;
        else
            elitismOffset = 0;
        
        // Bubble Sort para ordenar la población por fitness (de mayor a menor)
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

        // Sistema avanzado de detección y ruptura de estancamiento
        boolean inCatastropheMode = false;
        
        // Detección de estancamiento basada en variación en la población elite
        if (generationCount > 500) {
            double avgFitness = 0;
            for(int i = 0; i < Math.min(10, popSize); i++) {
                avgFitness += popEvolved.getIndividual(i).getOnlyFitness();
            }
            avgFitness /= Math.min(10, popSize);
            
            double stdDev = 0;
            for(int i = 0; i < Math.min(10, popSize); i++) {
                stdDev += Math.pow(popEvolved.getIndividual(i).getOnlyFitness() - avgFitness, 2);
            }
            stdDev = Math.sqrt(stdDev / Math.min(10, popSize));
            
            // Estancamiento detectado por baja variación o largo periodo sin mejoras
            if (stdDev < stagThreshold || noImprovementCounter > 1000) {
                // Incrementar significativamente la tasa de mutación
                currentMutationRate = baseMutationRate * 3.0;
                
                // Activar modo catástrofe si el estancamiento es severo o periódico
                if (noImprovementCounter > 2000 || generationCount % catastropheInterval == 0) {
                    inCatastropheMode = true;
                }
            }
        }
        
        // "Catástrofe" periódica para escapar de óptimos locales
        if (inCatastropheMode || generationCount % catastropheInterval == 0) {
            // Mantener unos pocos de los mejores individuos (élite) y randomizar parcialmente el resto
            for(int i = elitismOffset; i < popSize; i++) {
                Individual indiv = popEvolved.getIndividual(i);
                
                // Determinar nivel de disrupción según la posición
                double disruptionLevel = 0.1 + (i / (double)popSize) * 0.3; // 10-40% de los genes
                int genesToChange = (int)(indiv.size() * disruptionLevel);
                
                // Alterar aleatoriamente genes para escapar del óptimo local
                for (int j = 0; j < genesToChange; j++) {
                    int pos = (int)(Math.random() * indiv.size());
                    indiv.setGene(pos, (1 - indiv.getGene(pos)));
                }
            }
        }
        
        // Almacenar índices y probabilidades asociadas con presión de selección optimizada
    	WeightedRandomSelect<String> indexes = new WeightedRandomSelect<>();
        double newValue = value * selectionPressure; // Mayor presión de selección
        
        // Escala de ranking con decaimiento exponencial para favorecer fuertemente a los mejores
        for(int i = 0; i < popSize; i++){
            // Fórmula mejorada de distribución de probabilidades con mayor sesgo hacia los mejores
            double weight = newValue * Math.pow(0.92, i); // Decaimiento más pronunciado (0.92 en vez de 0.95)
         	indexes.addEntry(i, weight);
        }
        
        // Construir la nueva población ya cruzada
        Population newPop = new Population(popSize);
        
        // Conservar la élite sin modificación
        for(int i = 0; i < elitismOffset; i++){
            newPop.saveIndividual(i, copyIndiv(popEvolved.getIndividual(i)));
        }
        
        // La selección se hace más determinista con el tiempo
        double determinismFactor = Math.min(0.7, generationCount / 8000.0); // Hasta 70% determinista
        
        // Generar el resto de la población mediante cruce y mutación
        for(int i = elitismOffset; i < popEvolved.size(); i++){
            Individual indiv1;
            
            // Usar selección determinista o probabilística según el factor
            if (Math.random() < determinismFactor) {
                // Selección determinista - elegir entre los N mejores
                int topN = Math.max(3, (int)(popSize * 0.1)); // Los mejores 10% o al menos 3
                indiv1 = popEvolved.getIndividual((int)(Math.random() * topN));
            } else {
                // Selección ponderada normal
                indiv1 = popEvolved.getIndividual(indexes.getRandom());
            }
            
            // Sistema de torneo mejorado para el segundo padre
            Individual indiv2;
            if (Math.random() < determinismFactor) {
                // Torneo de tres candidatos - más selectivo
                Individual candidate1 = popEvolved.getIndividual(indexes.getRandom());
                Individual candidate2 = popEvolved.getIndividual(indexes.getRandom());
                Individual candidate3 = popEvolved.getIndividual(indexes.getRandom());
                // Elegir el mejor de los tres
                indiv2 = candidate1;
                if (candidate2.getOnlyFitness() > indiv2.getOnlyFitness())
                    indiv2 = candidate2;
                if (candidate3.getOnlyFitness() > indiv2.getOnlyFitness())
                    indiv2 = candidate3;
            } else {
                // Torneo normal de dos candidatos
                Individual candidate1 = popEvolved.getIndividual(indexes.getRandom());
                Individual candidate2 = popEvolved.getIndividual(indexes.getRandom());
                indiv2 = (candidate1.getOnlyFitness() > candidate2.getOnlyFitness()) ? 
                       candidate1 : candidate2;
            }
            
            Individual newIndiv = crossover(indiv1, indiv2);
            newPop.saveIndividual(i, newIndiv);
        }
        
        // Aplicar mutación a la población con esquema de recocido simulado
        for (int i = elitismOffset; i < newPop.size(); i++) {
            // Aplicar mutación adaptativa con recocido simulado
            double mutationChance = currentMutationRate;
            
            // Factor de "temperatura" decreciente (recocido simulado)
            double temperature = 1.0 - Math.min(0.95, generationCount / 8000.0);
            
            // Aumentar la probabilidad de mutación para individuos más alejados del óptimo
            if (i > popSize/2) {
                mutationChance *= (1.5 + temperature); // Mayor probabilidad para peores individuos, más al inicio
            }
            
            // Aplicar múltiples mutaciones posibles según el caso y la "temperatura"
            if(Math.random() <= mutationChance) {
                // El tipo de mutación depende de la fase: exploratoria o refinamiento
                if (Math.random() <= temperature) {
                    // Fase exploratoria: mutación más disruptiva
                    mutateLarge(newPop.getIndividual(i), temperature);
                } else {
                    // Fase de refinamiento: mutación más precisa
                    mutate(newPop.getIndividual(i));
                }
                
                // Posibilidad de mutación adicional que decrece con el tiempo
                if (Math.random() <= mutationChance * 0.2 * temperature) {
                    mutate(newPop.getIndividual(i));
                }
            }
        }
        
        // Calcular valores fitness de la población cruzada
        newPop.getFittest(); 
        
        // Asegurar que el elitismo se mantiene correctamente
        if(elitism) {
            // Comparar con los mejores de la población anterior
            for (int i = 0; i < elitismOffset; i++) {
                int compareIndex = Math.min(i, newPop.size()-1);
                if(popEvolved.getIndividual(i).getOnlyFitness() > newPop.getIndividual(compareIndex).getOnlyFitness()) {
                    newPop.saveIndividual(compareIndex, copyIndiv(popEvolved.getIndividual(i)));
                }
            }
        }

        // Bubble Sort final
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
        
        return newPop;
    }
    
    /**
     * It mades the copy of an individual
     * @param fittest
     * @return
     */
    public static Individual copyIndiv(Individual fittest){
    	Individual best = new Individual();
        best.setFitness(fittest.getOnlyFitness());
        for(int i = 0; i < fittest.size(); i++)
        	best.setGeneCopy(i, fittest.getGene(i));
    	return best;
    }    /**
     * Crossover individuals
     * @param indiv1
     * @param indiv2
     * @return
     */
    private static Individual crossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual();
        Random random = new Random();
        
        // Elegir tipo de cruce
        int crossoverType = random.nextInt(10);
        
        if (crossoverType < 6) {
            // Tipo 1: Cruce uniforme (60% de probabilidad)
            for (int i = 0; i < indiv1.size(); i++) {
                if (Math.random() <= uniformRate)
                    newSol.setGene(i, indiv1.getGene(i));
                else
                    newSol.setGene(i, indiv2.getGene(i));
            }
        } 
        else if (crossoverType < 9) {
            // Tipo 2: Cruce de un punto (30% de probabilidad)
            int crossoverPoint = random.nextInt(indiv1.size());
            
            for (int i = 0; i < indiv1.size(); i++) {
                if (i < crossoverPoint)
                    newSol.setGene(i, indiv1.getGene(i));
                else
                    newSol.setGene(i, indiv2.getGene(i));
            }
        }
        else {
            // Tipo 3: Cruce de dos puntos (10% de probabilidad)
            int point1 = random.nextInt(indiv1.size() - 2);
            int point2 = point1 + 1 + random.nextInt(indiv1.size() - point1 - 1);
            
            for (int i = 0; i < indiv1.size(); i++) {
                if (i < point1 || i >= point2)
                    newSol.setGene(i, indiv1.getGene(i));
                else
                    newSol.setGene(i, indiv2.getGene(i));
            }
        }
        
        return newSol;
    }    /**
     * Mutate an individual
     * @param indiv
     */
    private static void mutate(Individual indiv) {
    	Random random = new Random();
    	
    	// Elegir entre tres tipos de mutación
    	int mutationType = random.nextInt(10);
    	
    	if (mutationType < 7) {
    	    // Tipo 1: Mutación tradicional de un solo gen (70% de probabilidad)
    	    int randomNumber = random.nextInt(indiv.size());
    	    indiv.setGene(randomNumber, (1-indiv.getGene(randomNumber)));
    	} 
    	else if (mutationType < 9) {
    	    // Tipo 2: Mutación de dos genes consecutivos (20% de probabilidad)
    	    int startPos = random.nextInt(indiv.size() - 1);
    	    indiv.setGene(startPos, (1-indiv.getGene(startPos)));
    	    indiv.setGene(startPos + 1, (1-indiv.getGene(startPos + 1)));
    	}
    	else {
    	    // Tipo 3: Inversión de segmento (10% de probabilidad)
    	    int len = indiv.size();
    	    int startPos = random.nextInt(len / 2);
    	    int endPos = startPos + 1 + random.nextInt(Math.min(5, len - startPos - 1));
    	    
    	    // Invertir segmento
    	    for (int i = startPos, j = endPos; i < j; i++, j--) {
    	        int temp = indiv.getGene(i);
    	        indiv.setGene(i, indiv.getGene(j));
    	        indiv.setGene(j, temp);
    	    }
    	}
    }
    
    /**
     * Mutación más disruptiva para exploración (recocido simulado)
     * @param indiv
     * @param temperature Factor de temperatura (1.0 = caliente=inicio, 0.0=frío=final)
     */
    private static void mutateLarge(Individual indiv, double temperature) {
        Random random = new Random();
        
        // El nivel de disrupción depende de la temperatura
        int mutationType = random.nextInt(10);
        
        if (mutationType < 5) {
            // Tipo 1: Mutación múltiple (50% probabilidad)
            int numMutations = 1 + (int)(temperature * 5); // Entre 1 y 6 mutaciones
            for (int i = 0; i < numMutations; i++) {
                int pos = random.nextInt(indiv.size());
                indiv.setGene(pos, (1-indiv.getGene(pos)));
            }
        }
        else if (mutationType < 8) {
            // Tipo 2: Inversión de segmento grande (30% probabilidad)
            int len = indiv.size();
            // Segmento más grande cuando la temperatura es alta
            int segmentSize = 3 + (int)(temperature * 10);
            
            int startPos = random.nextInt(len - segmentSize + 1);
            int endPos = Math.min(startPos + segmentSize - 1, len - 1);
            
            // Invertir segmento
            for (int i = startPos, j = endPos; i < j; i++, j--) {
                int temp = indiv.getGene(i);
                indiv.setGene(i, indiv.getGene(j));
                indiv.setGene(j, temp);
            }
        }
        else {
            // Tipo 3: Cambio total de un bloque (20% probabilidad)
            int len = indiv.size();
            int blockSize = 2 + (int)(temperature * 5); // Tamaño del bloque
            int startPos = random.nextInt(len - blockSize + 1);
            
            // Valor aleatorio para todo el bloque
            int newValue = random.nextInt(2); // 0 o 1
            
            for (int i = 0; i < blockSize; i++) {
                if (startPos + i < len) {
                    indiv.setGene(startPos + i, newValue);
                }
            }
        }
    }
    
    // Getters para permitir monitoreo externo
    public static double getCurrentMutationRate() {
        return currentMutationRate;
    }
    
    public static double getBestFitnessEver() {
        return bestFitnessEver;
    }
    
    public static int getNoImprovementCounter() {
        return noImprovementCounter;
    }
    
    public static int getExplorePhase() {
        return explorePhase;
    }
}
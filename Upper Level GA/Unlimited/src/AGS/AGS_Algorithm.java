package AGS;

import java.io.IOException;
import java.util.Random;

import AGI.AGI_Engine;

public class AGS_Algorithm {
    private static final double uniformRate = 0.6;
    private static double mutationRate = 0.20; // Aumentado para mayor exploración inicial
    private static final boolean elitism = true;
    private static int tournamentSize = 3; // Ahora variable para control dinámico
    private static final double DIVERSITY_THRESHOLD = 0.08; // Aumentado para mejor detección
    
    // Variables para tracking de evolución mejorado
    private static double lastBestFitness = Double.NEGATIVE_INFINITY;
    private static int stagnationCounter = 0;
    private static final int MAX_STAGNATION = 15; // Reducido para reaccionar más rápido
    private static final int SEVERE_STAGNATION = 30; // Para restart completo
    private static int severeStagnationCounter = 0;
    
    // Variables para control de diversidad y nichos
    private static double[] fitnessHistory = new double[10]; // Historial de fitness
    private static int historyIndex = 0;
    private static final double NICHE_RADIUS = 150.0; // Radio para formación de nichos
    private static final double IMMIGRATION_RATE = 0.1; // Tasa de inmigración

    /**
     * Evolve Population with improvements
     * @param popEvolved the population to evolve
     * @param engine the AGI engine
     * @param popSize population size
     * @param generation current generation number
     * @return evolved population
     * @throws IOException if file operations fail
     */
    public static AGS_Population evolvePopulation(AGS_Population popEvolved, AGI_Engine engine, int popSize, int generation) throws IOException {
        popEvolved.getFittest(engine, true);
        
        // Actualizar historial de fitness
        updateFitnessHistory(popEvolved.getIndividual(0).getOnlyFitness());
        
        // Detectar estancamiento mejorado
        double currentBestFitness = popEvolved.getIndividual(0).getOnlyFitness();
        boolean hasImproved = Math.abs(currentBestFitness - lastBestFitness) > 0.001;
        
        if(!hasImproved) {
            stagnationCounter++;
            severeStagnationCounter++;
        } else {
            stagnationCounter = 0;
            severeStagnationCounter = 0;
            lastBestFitness = currentBestFitness;
        }
        
        // Restart completo si hay estancamiento severo
        if(severeStagnationCounter >= SEVERE_STAGNATION) {
            return performCompleteRestart(popEvolved, engine, popSize);
        }
        
        // Diversificación por estancamiento moderado
        if(stagnationCounter >= MAX_STAGNATION) {
            aggressiveDiversification(popEvolved, popSize);
            stagnationCounter = 0;
        }
        
        // Adaptación dinámica avanzada
        advancedAdaptation(popEvolved, generation);
        
        // Inmigración periódica para mantener diversidad
        if(generation % 25 == 0 && generation > 0) {
            introduceImmigrants(popEvolved, popSize);
        }
        
        int elitismOffset = elitism ? 3 : 0; // Preservar top 3 para mejor convergencia
  
        // Ordenamiento mejorado (mantener el bubble sort pero optimizado)
        sortPopulationByFitness(popEvolved, popSize);

        // Crear nueva población con estrategias mejoradas
        AGS_Population newPop = new AGS_Population(popSize);
        
        // Elitismo mejorado con preservación de diversidad
        if(elitism) {
            // Preservar los 3 mejores únicos (evitar duplicados)
            newPop.saveIndividual(0, copyIndiv(popEvolved.getIndividual(0)));
            int eliteCount = 1;
            
            for(int i = 1; i < popSize && eliteCount < 3; i++) {
                if(!areIndividualsSimilar(popEvolved.getIndividual(i), popEvolved.getIndividual(0), 50.0)) {
                    newPop.saveIndividual(eliteCount, copyIndiv(popEvolved.getIndividual(i)));
                    eliteCount++;
                }
            }
            elitismOffset = eliteCount;
        }
        
        // Generar nueva población con múltiples estrategias
        for(int i = elitismOffset; i < popSize; i++){
            AGS_Individual newIndiv;
            
            double strategyChoice = Math.random();
            if(strategyChoice < 0.6) {
                // Selección por torneo estándar (60%)
                AGS_Individual parent1 = advancedTournamentSelection(popEvolved, popSize);
                AGS_Individual parent2 = advancedTournamentSelection(popEvolved, popSize);
                newIndiv = enhancedCrossover(parent1, parent2, generation);
            } else if(strategyChoice < 0.8) {
                // Selección basada en nichos (20%)
                newIndiv = nicheBasedReproduction(popEvolved, popSize);
            } else {
                // Generación aleatoria controlada (20%)
                newIndiv = generateControlledRandom(popEvolved.getIndividual(0));
            }
            
            newPop.saveIndividual(i, newIndiv);
        }
        
        // Mutación adaptativa mejorada
        for (int i = elitismOffset; i < newPop.size(); i++) {
            double mutationProbability = calculateAdaptiveMutationRate(i, newPop.size(), generation);
            if(Math.random() <= mutationProbability) {
                enhancedMutate(newPop.getIndividual(i), generation);
            }
        }

        // Calcular fitness de la nueva población
        newPop.getFittest(engine, false); 
        
        // Búsqueda local mejorada más frecuente
        if(generation % 5 == 0) {
            enhancedLocalSearch(newPop, engine, generation);
        }
        
        // Ordenar nueva población
        sortPopulationByFitness(newPop, newPop.size());
        
        return newPop;
    }
    
    /**
     * It makes the copy of an individual
     * @param fittest
     * @return
     */    public static AGS_Individual copyIndiv(AGS_Individual fittest){
        AGS_Individual best = new AGS_Individual();
        best.setFitness(fittest.getOnlyFitness());
        if(fittest.getOnlyFitnessAGI() != 0) {
            best.setFitnessAGI(fittest.getOnlyFitnessAGI());
        }
        int[] solution = fittest.getSolution();
        best.setSolution(solution);
        
        // Copiar genes - usar setGene para individuos nuevos, changeGene para existentes
        for(int i = 0; i < fittest.size(); i++) {
            if(i < best.size()) {
                best.changeGene(i, fittest.getGene(i));
            } else {
                best.setGene(i, fittest.getGene(i));
            }
        }
        
        return best;
    }

    /**
     * Crossover mejorado con múltiples puntos
     * @param indiv1
     * @param indiv2
     * @return
     */
    private static AGS_Individual crossover(AGS_Individual indiv1, AGS_Individual indiv2) {
        Random random = new Random();
        AGS_Individual newSol = new AGS_Individual();
        
        // Seleccionar tipo de crossover dinámicamente
        double crossoverChoice = Math.random();
          if(crossoverChoice < 0.4) {
            // Crossover de un punto (40%)
            int crossoverPoint = random.nextInt(indiv1.size());
            for (int i = 0; i < indiv1.size(); i++) {
                if (i < crossoverPoint) {
                    newSol.setGene(i, indiv1.getGene(i));
                } else {
                    newSol.setGene(i, indiv2.getGene(i));
                }
            }
        } else if(crossoverChoice < 0.7) {
            // Crossover de dos puntos (30%)
            int point1 = random.nextInt(indiv1.size() / 3);
            int point2 = point1 + random.nextInt(indiv1.size() / 2) + 1;
            if(point2 >= indiv1.size()) point2 = indiv1.size() - 1;
            
            for (int i = 0; i < indiv1.size(); i++) {
                if(i <= point1 || i > point2) {
                    newSol.setGene(i, indiv1.getGene(i));
                } else {
                    newSol.setGene(i, indiv2.getGene(i));
                }
            }
        } else {
            // Crossover uniforme mejorado (30%)
            for (int i = 0; i < indiv1.size(); i++) {
                if (Math.random() <= uniformRate) {
                    newSol.setGene(i, indiv1.getGene(i));
                } else {
                    newSol.setGene(i, indiv2.getGene(i));
                }
            }
        }
        
        return newSol;
    }
    
    /**
     * Mutación mejorada con diferentes estrategias
     * @param indiv
     */
    private static void mutate(AGS_Individual indiv) {
        Random random = new Random();
        
        // Mutación más conservadora - mutar solo 1-2 genes máximo
        int numMutations = 1;
        if(Math.random() < 0.3) { // Solo 30% de probabilidad de mutar 2 genes
            numMutations = 2;
        }
        
        for(int m = 0; m < numMutations; m++) {
            int randomGene = random.nextInt(indiv.size());
            Double gene;
            
            // 80% mutación normal, 20% mutación más agresiva (más conservador)
            if(Math.random() <= 0.8) {
                gene = aproxValue(indiv.getGene(randomGene));
            } else {
                // Mutación más agresiva para escapar de óptimos locales
                gene = aproxValueAggressive(indiv.getGene(randomGene));
            }
            
            indiv.changeGene(randomGene, gene);
        }
    }

    /**
     * Aproximación de valor original (mantener compatibilidad)
     * @param value
     * @return
     */
    public static double aproxValue(double value) {
        double variation;
        double oldValue = value;
        do {
            Random r = new Random();
            variation = (r.nextInt(2)==0?-1:1)*200*r.nextDouble(); // random interval (-200, 200)
            value = oldValue + variation;
        } while (value < -999 || value > 1000);
        return value;
    }
    
    /**
     * Mutación más agresiva para escapar de óptimos locales
     * @param value
     * @return
     */
    public static double aproxValueAggressive(double value) {
        double variation;
        double oldValue = value;
        do {
            Random r = new Random();
            variation = (r.nextInt(2)==0?-1:1)*400*r.nextDouble(); // rango más amplio (-400, 400)
            value = oldValue + variation;
        } while (value < -999 || value > 1000);
        return value;
    }
    
    // ========== NUEVOS MÉTODOS MEJORADOS ==========
    
    /**
     * Actualiza el historial de fitness para análisis de tendencias
     */
    private static void updateFitnessHistory(double fitness) {
        fitnessHistory[historyIndex] = fitness;
        historyIndex = (historyIndex + 1) % fitnessHistory.length;
    }
    
    /**
     * Restart completo de la población manteniendo solo el mejor
     */
    private static AGS_Population performCompleteRestart(AGS_Population population, AGI_Engine engine, int popSize) throws IOException {
        AGS_Population newPop = new AGS_Population(popSize);
        
        // Mantener solo el mejor individuo
        newPop.saveIndividual(0, copyIndiv(population.getIndividual(0)));
        
        // Generar el resto completamente aleatorio
        Random random = new Random();        for(int i = 1; i < popSize; i++) {
            AGS_Individual newIndiv = new AGS_Individual();
            for(int j = 0; j < 25; j++) { // 40 genes por individuo
                double value = (random.nextDouble() * 1999) - 999;
                newIndiv.setGene(j, value);
            }
            newPop.saveIndividual(i, newIndiv);
        }
        
        // Resetear contadores
        severeStagnationCounter = 0;
        stagnationCounter = 0;
        
        // Calcular fitness de la nueva población
        newPop.getFittest(engine, false);
        
        return newPop;
    }
    
    /**
     * Diversificación más agresiva que la estándar
     */
    private static void aggressiveDiversification(AGS_Population population, int popSize) {
        Random random = new Random();
        
        // Mantener solo el mejor individuo
        for(int i = 1; i < popSize; i++) {
            AGS_Individual individual = population.getIndividual(i);
            
            // Mutar entre 8-15 genes (más agresivo)
            int numGenesToMutate = 8 + random.nextInt(8);
            for(int j = 0; j < numGenesToMutate; j++) {
                int geneIndex = random.nextInt(individual.size());
                
                // 50% completamente aleatorio, 50% mutación agresiva
                if(random.nextBoolean()) {
                    double newValue = (random.nextDouble() * 1999) - 999;
                    individual.changeGene(geneIndex, newValue);
                } else {
                    double currentValue = individual.getGene(geneIndex);
                    double variation = (random.nextDouble() - 0.5) * 800; // ±400
                    double newValue = currentValue + variation;
                    newValue = Math.max(-999, Math.min(1000, newValue));
                    individual.changeGene(geneIndex, newValue);
                }
            }
        }
    }
    
    /**
     * Adaptación avanzada de parámetros
     */
    private static void advancedAdaptation(AGS_Population population, int generation) {
        // Calcular diversidad
        double diversity = calculateDiversity(population);
        
        // Ajustar tamaño de torneo según diversidad
        if(diversity < DIVERSITY_THRESHOLD) {
            tournamentSize = Math.min(5, tournamentSize + 1);
            mutationRate = Math.min(0.35, mutationRate * 1.3);
        } else if(diversity > DIVERSITY_THRESHOLD * 4) {
            tournamentSize = Math.max(2, tournamentSize - 1);
            mutationRate = Math.max(0.08, mutationRate * 0.9);
        }
        
        // Adaptación por generación
        if(generation > 50) {
            mutationRate = Math.max(0.08, mutationRate * 0.995);
        }
    }
    
    /**
     * Introducir individuos inmigrantes para mantener diversidad
     */
    private static void introduceImmigrants(AGS_Population population, int popSize) {
        Random random = new Random();
        int numImmigrants = (int)(popSize * IMMIGRATION_RATE);
        
        // Reemplazar los peores individuos con inmigrantes
        for(int i = 0; i < numImmigrants; i++) {
            int replaceIndex = popSize - 1 - i;
            if(replaceIndex > 2) { // No reemplazar a los top 3
                AGS_Individual immigrant = new AGS_Individual();
                
                // Generar inmigrante híbrido (50% aleatorio, 50% basado en el mejor)
                AGS_Individual best = population.getIndividual(0);                for(int j = 0; j < 25; j++) {
                    if(random.nextBoolean()) {
                        // Gen completamente aleatorio
                        double value = (random.nextDouble() * 1999) - 999;
                        immigrant.setGene(j, value);
                    } else {
                        // Gen basado en el mejor con variación
                        double baseValue = best.getGene(j);
                        double variation = (random.nextDouble() - 0.5) * 400;
                        double value = baseValue + variation;
                        value = Math.max(-999, Math.min(1000, value));
                        immigrant.setGene(j, value);
                    }
                }
                
                population.sustituteIndividual(replaceIndex, immigrant);
            }
        }
    }
    
    /**
     * Ordenamiento de población por fitness (optimizado)
     */
    private static void sortPopulationByFitness(AGS_Population population, int popSize) {
        AGS_Individual temp = new AGS_Individual();
        for(int i = 0; i < popSize-1; i++){
            for(int j = 0; j < (popSize-i-1); j++) {
                if(population.getIndividual(j+1).getOnlyFitness() > population.getIndividual(j).getOnlyFitness()) {
                    temp = copyIndiv(population.getIndividual(j));
                    population.sustituteIndividual(j, copyIndiv(population.getIndividual(j+1)));
                    population.sustituteIndividual(j+1, temp);
                }
            }
        }
    }
    
    /**
     * Verificar si dos individuos son similares
     */
    private static boolean areIndividualsSimilar(AGS_Individual ind1, AGS_Individual ind2, double threshold) {
        return calculateDistance(ind1, ind2) < threshold;
    }
    
    /**
     * Calcular distancia entre dos individuos
     * @param ind1
     * @param ind2
     * @return
     */
    private static double calculateDistance(AGS_Individual ind1, AGS_Individual ind2) {
        double distance = 0.0;
        int minSize = Math.min(ind1.size(), ind2.size());
        
        // Verificar que los individuos tengan genes válidos
        if(minSize <= 0) return 0.0;
        
        for(int i = 0; i < minSize; i++) {
            try {
                distance += Math.abs(ind1.getGene(i) - ind2.getGene(i));
            } catch(Exception e) {
                // Si hay error accediendo a un gen, continuar con el siguiente
                continue;
            }
        }
        return distance / minSize;
    }
    
    /**
     * Calcular diversidad de la población
     * @param population
     * @return
     */
    private static double calculateDiversity(AGS_Population population) {
        if(population.size() < 2) return 1.0;
        
        double totalDistance = 0.0;
        int comparisons = 0;
        
        for(int i = 0; i < population.size() - 1; i++) {
            for(int j = i + 1; j < population.size(); j++) {
                try {
                    AGS_Individual ind1 = population.getIndividual(i);
                    AGS_Individual ind2 = population.getIndividual(j);
                    
                    // Verificar que los individuos no sean nulos
                    if(ind1 != null && ind2 != null) {
                        totalDistance += calculateDistance(ind1, ind2);
                        comparisons++;
                    }
                } catch(Exception e) {
                    // Si hay error con algún individuo, continuar con el siguiente
                    continue;
                }
            }
        }
        
        // Evitar división por cero
        if(comparisons == 0) return 1.0;
        
        return totalDistance / comparisons;
    }
    
    /**
     * Selección por torneo avanzada con presión adaptativa
     */
    private static AGS_Individual advancedTournamentSelection(AGS_Population population, int popSize) {
        Random random = new Random();
        AGS_Individual best = null;
        
        // Tamaño de torneo adaptativo
        int adaptiveTournamentSize = Math.min(tournamentSize, popSize);
        
        for(int i = 0; i < adaptiveTournamentSize; i++) {
            int randomIndex = random.nextInt(popSize);
            AGS_Individual candidate = population.getIndividual(randomIndex);
            
            if(best == null || candidate.getOnlyFitness() > best.getOnlyFitness()) {
                best = candidate;
            }
        }
        
        return copyIndiv(best);
    }
    
    /**
     * Crossover mejorado con información de generación
     */    private static AGS_Individual enhancedCrossover(AGS_Individual parent1, AGS_Individual parent2, int generation) {
        Random random = new Random();
        AGS_Individual offspring = new AGS_Individual();
          // Manejar caso cuando los padres tienen tamaño 0 (primera iteración)
        if(parent1.size() == 0 || parent2.size() == 0) {
            // Generar individuo aleatorio con 40 genes
            for(int i = 0; i < 25; i++) {
                double value = (random.nextDouble() * 1999) - 999;
                offspring.setGene(i, value);
            }
            return offspring;
        }
        
        // Seleccionar estrategia de crossover según la generación
        double strategy = Math.random();
        
        if(generation < 30) {
            // Generaciones tempranas: más exploración
            strategy = Math.random();
        } else {
            // Generaciones tardías: más explotación
            strategy = Math.random() * 0.7; // Favorecer crossover uniforme
        }
          if(strategy < 0.3) {
            // Crossover uniforme inteligente
            for(int i = 0; i < parent1.size(); i++) {
                // Seleccionar el gen del padre con mejor fitness local
                if(Math.random() < 0.6) {
                    offspring.setGene(i, parent1.getGene(i));
                } else {
                    offspring.setGene(i, parent2.getGene(i));
                }
            }
        } else if(strategy < 0.6) {
            // Crossover aritmético
            for(int i = 0; i < parent1.size(); i++) {
                double alpha = random.nextDouble();
                double value = alpha * parent1.getGene(i) + (1 - alpha) * parent2.getGene(i);
                value = Math.max(-999, Math.min(1000, value));
                offspring.setGene(i, value);
            }
        } else {
            // Crossover de múltiples puntos
            int[] points = new int[3];
            for(int i = 0; i < 3; i++) {
                points[i] = random.nextInt(parent1.size());
            }
            java.util.Arrays.sort(points);
            
            boolean useParent1 = true;
            int pointIndex = 0;
            
            for(int i = 0; i < parent1.size(); i++) {
                if(pointIndex < points.length && i == points[pointIndex]) {
                    useParent1 = !useParent1;
                    pointIndex++;
                }
                
                if(useParent1) {
                    offspring.setGene(i, parent1.getGene(i));
                } else {
                    offspring.setGene(i, parent2.getGene(i));
                }
            }
        }
        
        return offspring;
    }
    
    /**
     * Reproducción basada en nichos
     */
    private static AGS_Individual nicheBasedReproduction(AGS_Population population, int popSize) {
        Random random = new Random();
        
        // Seleccionar un individuo aleatorio como referencia
        AGS_Individual reference = population.getIndividual(random.nextInt(popSize));
        
        // Encontrar individuos en el mismo nicho
        java.util.List<AGS_Individual> niche = new java.util.ArrayList<>();
        for(int i = 0; i < popSize; i++) {
            AGS_Individual candidate = population.getIndividual(i);
            if(calculateDistance(reference, candidate) <= NICHE_RADIUS) {
                niche.add(candidate);
            }
        }
        
        // Si el nicho es muy pequeño, usar toda la población
        if(niche.size() < 2) {
            return advancedTournamentSelection(population, popSize);
        }
        
        // Seleccionar dos padres del nicho
        AGS_Individual parent1 = niche.get(random.nextInt(niche.size()));
        AGS_Individual parent2 = niche.get(random.nextInt(niche.size()));
        
        return enhancedCrossover(parent1, parent2, 0);
    }
    
    /**
     * Generar individuo aleatorio controlado
     */    private static AGS_Individual generateControlledRandom(AGS_Individual template) {
        Random random = new Random();
        AGS_Individual newIndiv = new AGS_Individual();
        
        // Manejar caso cuando el template tiene tamaño 0 (primera iteración)
        if(template.size() == 0) {
            // Generar individuo completamente aleatorio con 40 genes
            for(int i = 0; i < 25; i++) {
                double value = (random.nextDouble() * 1999) - 999;
                newIndiv.setGene(i, value);
            }
            return newIndiv;
        }
        
        // 70% de los genes aleatorios, 30% basados en el template
        for(int i = 0; i < 25; i++) {
            if(random.nextDouble() < 0.7) {
                // Gen completamente aleatorio
                double value = (random.nextDouble() * 1999) - 999;
                newIndiv.setGene(i, value);
            } else {
                // Gen basado en template con variación
                double baseValue = template.getGene(i);
                double variation = (random.nextDouble() - 0.5) * 600;
                double value = baseValue + variation;
                value = Math.max(-999, Math.min(1000, value));
                newIndiv.setGene(i, value);
            }
        }
        
        return newIndiv;
    }
    
    /**
     * Calcular tasa de mutación adaptativa
     */
    private static double calculateAdaptiveMutationRate(int individualIndex, int popSize, int generation) {
        // Tasa base
        double baseRate = mutationRate;
        
        // Aumentar mutación para individuos peores
        double positionFactor = (double)individualIndex / popSize;
        double positionBonus = positionFactor * 0.15;
        
        // Reducir mutación en generaciones tardías
        double generationFactor = generation > 100 ? 0.8 : 1.0;
        
        return Math.min(0.4, (baseRate + positionBonus) * generationFactor);
    }
    
    /**
     * Mutación mejorada con múltiples estrategias
     */
    private static void enhancedMutate(AGS_Individual individual, int generation) {
        Random random = new Random();
        
        // Número de genes a mutar (adaptativo)
        int numMutations = 1;
        if(generation < 50) {
            // Más mutaciones en generaciones tempranas
            numMutations = 1 + random.nextInt(3); // 1-3 genes
        } else {
            // Menos mutaciones en generaciones tardías
            if(random.nextDouble() < 0.4) {
                numMutations = 2;
            }
        }
        
        for(int m = 0; m < numMutations; m++) {
            int geneIndex = random.nextInt(individual.size());
            double currentValue = individual.getGene(geneIndex);
            double newValue;
            
            double mutationType = random.nextDouble();
            
            if(mutationType < 0.4) {
                // Mutación gaussiana (40%)
                double stdDev = generation < 50 ? 200 : 100;
                double gaussian = random.nextGaussian() * stdDev;
                newValue = currentValue + gaussian;
            } else if(mutationType < 0.7) {
                // Mutación uniforme estándar (30%)
                newValue = aproxValue(currentValue);
            } else if(mutationType < 0.9) {
                // Mutación agresiva (20%)
                newValue = aproxValueAggressive(currentValue);
            } else {
                // Mutación completamente aleatoria (10%)
                newValue = (random.nextDouble() * 1999) - 999;
            }
            
            // Mantener dentro de límites
            newValue = Math.max(-999, Math.min(1000, newValue));
            individual.changeGene(geneIndex, newValue);
        }
    }
    
    /**
     * Búsqueda local mejorada
     */
    private static void enhancedLocalSearch(AGS_Population population, AGI_Engine engine, int generation) throws IOException {
        Random random = new Random();
        
        // Aplicar búsqueda local a los mejores individuos
        int numToImprove = Math.min(3, population.size());
        
        for(int i = 0; i < numToImprove; i++) {
            AGS_Individual original = population.getIndividual(i);
            AGS_Individual improved = copyIndiv(original);
            
            // Número de intentos basado en la generación
            int attempts = Math.max(3, 8 - generation/20);
            
            for(int attempt = 0; attempt < attempts; attempt++) {
                // Seleccionar 1-2 genes para ajustar
                int numGenes = random.nextInt(2) + 1;
                
                for(int g = 0; g < numGenes; g++) {
                    int geneIndex = random.nextInt(improved.size());
                    double originalValue = improved.getGene(geneIndex);
                    
                    // Probar pequeños ajustes en ambas direcciones
                    double[] adjustments = {-50, -20, -5, 5, 20, 50};
                    
                    for(double adj : adjustments) {
                        double newValue = originalValue + adj;
                        if(newValue >= -999 && newValue <= 1000) {
                            improved.changeGene(geneIndex, newValue);
                            
                            // Aceptar el cambio con cierta probabilidad (hill climbing estocástico)
                            if(random.nextDouble() < 0.3) {
                                break;
                            } else {
                                improved.changeGene(geneIndex, originalValue); // Revertir
                            }
                        }
                    }
                }
            }
            
            // Reemplazar si es diferente
            if(!areIndividualsEqual(original, improved)) {
                population.sustituteIndividual(i, improved);
            }
        }
    }
    
    /**
     * Verificar si dos individuos son iguales
     * @param ind1
     * @param ind2
     * @return
     */
    private static boolean areIndividualsEqual(AGS_Individual ind1, AGS_Individual ind2) {
        for(int i = 0; i < ind1.size(); i++) {
            if(Math.abs(ind1.getGene(i) - ind2.getGene(i)) > 0.001) {
                return false;
            }
        }
        return true;
    }
}

package main;

/**
 * Clase para monitorear la evolución del algoritmo genético
 * Permite activar/desactivar el monitoreo fácilmente sin modificar Algorithm.java
 * 
 * @author Jonathan Carrero
 */
public class AlgorithmMonitor {
    private boolean enabled;
    private int detailedReportInterval;
    private int quickStatusInterval;
    
    /**
     * Constructor con configuración por defecto
     */
    public AlgorithmMonitor() {
        this.enabled = true;
        this.detailedReportInterval = 500;
        this.quickStatusInterval = 100;
    }
    
    /**
     * Constructor con configuración personalizada
     * @param enabled Si el monitoreo está activo
     * @param detailedInterval Cada cuántas generaciones mostrar reporte detallado
     * @param quickInterval Cada cuántas generaciones mostrar estado rápido
     */
    public AlgorithmMonitor(boolean enabled, int detailedInterval, int quickInterval) {
        this.enabled = enabled;
        this.detailedReportInterval = detailedInterval;
        this.quickStatusInterval = quickInterval;
    }
    
    /**
     * Activa o desactiva el monitoreo
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Configura los intervalos de reporte
     */
    public void setIntervals(int detailedInterval, int quickInterval) {
        this.detailedReportInterval = detailedInterval;
        this.quickStatusInterval = quickInterval;
    }
    
    /**
     * Monitorea la evolución de la población
     * @param population La población actual
     * @param generation Número de generación actual
     * @param currentMutationRate Tasa de mutación actual
     * @param bestFitnessEver Mejor fitness histórico
     * @param noImprovementCounter Contador de generaciones sin mejora
     * @param explorePhase Fase de exploración (número de generaciones)
     */
    public void monitorEvolution(Population population, int generation, double currentMutationRate, 
                               double bestFitnessEver, int noImprovementCounter, int explorePhase) {
        if (!enabled) return;
        
        // Reporte detallado cada cierto intervalo
        if (generation % detailedReportInterval == 0 || generation <= 10) {
            System.out.println("\n=== GENERATION " + generation + " EVOLUTION REPORT ===");
            printTopIndividuals(population, 5);
            printEvolutionStats(population, generation, currentMutationRate, bestFitnessEver, 
                              noImprovementCounter, explorePhase);
            System.out.println("=========================================\n");
        }
        // Estado rápido cada cierto intervalo
        else if (generation % quickStatusInterval == 0) {
            Individual best = population.getFittest();
            System.out.println("Gen " + generation + " - Best fitness: " + 
                             String.format("%.6f", best.getOnlyFitness()) + 
                             " | MutRate: " + String.format("%.5f", currentMutationRate));
        }
    }
    
    /**
     * Muestra información del inicio de una nueva ejecución
     */
    public void showRunStart(int runNumber, int totalRuns) {
        if (!enabled) return;
        System.out.println("\n=== STARTING EVOLUTION RUN " + runNumber + "/" + totalRuns + " ===");
    }
    
    /**
     * Muestra información del final de una ejecución
     */
    public void showRunEnd(int runNumber, Individual bestIndividual) {
        if (!enabled) return;
        System.out.println("\n=== FINAL RESULT FOR RUN " + runNumber + " ===");
        System.out.println("Best individual fitness: " + String.format("%.6f", bestIndividual.getOnlyFitness()));
        System.out.println("Best individual genes: " + bestIndividual.toString());
        System.out.println("==========================================\n");
    }
    
    /**
     * Muestra los mejores N individuos de la población
     */
    private void printTopIndividuals(Population population, int topCount) {
        System.out.println("TOP " + topCount + " INDIVIDUALS:");
        
        for (int i = 0; i < Math.min(topCount, population.size()); i++) {
            Individual indiv = population.getIndividual(i);
            System.out.println("  #" + (i+1) + " - Fitness: " + 
                             String.format("%.6f", indiv.getOnlyFitness()) + 
                             " | Genes: " + getGenesPreview(indiv));
        }
    }
    
    /**
     * Muestra estadísticas de evolución para la generación actual
     */
    private void printEvolutionStats(Population population, int generation, double currentMutationRate,
                                   double bestFitnessEver, int noImprovementCounter, int explorePhase) {
        double totalFitness = 0;
        double minFitness = Double.MAX_VALUE;
        double maxFitness = -Double.MAX_VALUE;
        
        // Calcular estadísticas
        for (int i = 0; i < population.size(); i++) {
            double fitness = population.getIndividual(i).getOnlyFitness();
            totalFitness += fitness;
            minFitness = Math.min(minFitness, fitness);
            maxFitness = Math.max(maxFitness, fitness);
        }
        
        double avgFitness = totalFitness / population.size();
        double diversityScore = calculateDiversityScore(population);
        
        System.out.println("EVOLUTION STATISTICS:");
        System.out.println("  Best fitness ever: " + String.format("%.6f", bestFitnessEver));
        System.out.println("  Current best: " + String.format("%.6f", maxFitness));
        System.out.println("  Average fitness: " + String.format("%.6f", avgFitness));
        System.out.println("  Worst fitness: " + String.format("%.6f", minFitness));
        System.out.println("  Population diversity: " + String.format("%.4f", diversityScore));
        System.out.println("  Mutation rate: " + String.format("%.5f", currentMutationRate));
        System.out.println("  No improvement for: " + noImprovementCounter + " generations");
        
        // Información de fase
        if (generation < explorePhase) {
            System.out.println("  Phase: EXPLORATION (high mutation)");
        } else {
            System.out.println("  Phase: EXPLOITATION (adaptive mutation)");
        }
        
        // Aviso de estancamiento
        if (noImprovementCounter > 1000) {
            System.out.println("  WARNING: Population may be stagnating!");
        }
    }
    
    /**
     * Crea una vista previa de los genes de un individuo
     */
    private String getGenesPreview(Individual individual) {
        StringBuilder preview = new StringBuilder();
        int previewLength = Math.min(20, individual.size());
        
        for (int i = 0; i < previewLength; i++) {
            preview.append(individual.getGene(i));
        }
        
        if (individual.size() > previewLength) {
            preview.append("... (").append(individual.size()).append(" total)");
        }
        
        return preview.toString();
    }
    
    /**
     * Calcula un score de diversidad para la población
     */
    private double calculateDiversityScore(Population population) {
        if (population.size() < 2) return 0.0;
        
        double totalDistance = 0;
        int comparisons = 0;
        
        // Comparar los primeros 10 individuos entre sí para estimar diversidad
        int sampleSize = Math.min(10, population.size());
        
        for (int i = 0; i < sampleSize - 1; i++) {
            for (int j = i + 1; j < sampleSize; j++) {
                totalDistance += hammingDistance(population.getIndividual(i), 
                                               population.getIndividual(j));
                comparisons++;
            }
        }
        
        double avgDistance = totalDistance / comparisons;
        double maxPossibleDistance = population.getIndividual(0).size();
        
        return avgDistance / maxPossibleDistance;
    }
    
    /**
     * Calcula la distancia de Hamming entre dos individuos
     */
    private double hammingDistance(Individual indiv1, Individual indiv2) {
        int differences = 0;
        int size = Math.min(indiv1.size(), indiv2.size());
        
        for (int i = 0; i < size; i++) {
            if (indiv1.getGene(i) != indiv2.getGene(i)) {
                differences++;
            }
        }
        
        return differences;
    }
}

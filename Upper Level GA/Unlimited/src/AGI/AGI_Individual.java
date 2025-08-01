package AGI;

import java.io.IOException;
import java.util.Scanner;

import files.CustomReadFile;
import files.CustomWriteFile;

public class AGI_Individual {
	private double uniformRate = 0.5;
    static int defaultGeneLength = 25;
    private int[] genes = new int[defaultGeneLength];
    private double fitness = 0.0;
	private CustomReadFile readFile;
	private CustomWriteFile writeFile;
	Scanner sc;
    
    /**
     * Create random individual
     * @param index
     * @param numUsers 
     * @throws IOException 
     */
    public void generateIndividual(int index, int numUsers) throws IOException {
		String text = "";
		this.writeFile = new CustomWriteFile("IGA_indiv" + index + ".txt");
    	int gene;
        for (int i = 0; i < size(); i++) {
        	// The value for each gen is [0, 1]
            if (Math.random() <= uniformRate)
                gene = 0;
            else
                gene = 1;
			text += gene + " ";
        }
        text += -1000;
		this.writeFile.writeVector(this.writeFile, text);
		this.writeFile.closeWriteFile(this.writeFile);
	}
    
    /**
     * Read individual from file
     * @param index
     * @param numUsers
     * @return
     * @throws IOException
     */
	public int[] readIndividual(int index, int numUsers) throws IOException {
    	this.readFile = new CustomReadFile("IGA_indiv" + index + ".txt");
    	this.sc = new Scanner(this.readFile);
    	this.genes = this.readFile.readArray(sc, size());
    	return this.genes;
	}
	
    /**
     * Create individuals with different gene lengths
     * @param length
     */
    public void setDefaultGeneLength(int length) {
        AGI_Individual.defaultGeneLength = length;
    }
    
    /**
     * get gene
     * @param index
     * @return
     */
    public int getGene(int index) {
        return genes[index];
    }
    
    /**
     * get genes
     * @return
     */
    public int[] getGenes() {
    	return this.genes;
    }
    
    /**
     * set gene
     * @param index
     * @param value
     */
    public void setGene(int index, int value) {
        genes[index] = value;
        fitness = 0.0;
    }
    
    public void setGeneCopy(int index, int value) {
        genes[index] = value;
    }

    /**
     * Get genes length
     * @return
     */
    public int size() {
        return genes.length;
    }
    
    /**
     * Get Fitness value
     * @return
     */
    public double getFitness() {
        if (fitness == 0) {
            fitness = AGI_FitnessCalc.getFitness(this);
        }
        return fitness;
    }
    
    /**
     * Returns the fitness of individual
     * @return
     */
    public double getOnlyFitness() {
    	return this.fitness;
    }
    
    /**
     * Set Fitness value
     * @param utility
     */
    public void setFitness(double utility) {
    	this.fitness = utility;
    }

    @Override
    public String toString() {
        String geneString = "";
        for (int i = 0; i < size(); i++) {
            geneString += getGene(i);
        }
        return geneString;
    }
}
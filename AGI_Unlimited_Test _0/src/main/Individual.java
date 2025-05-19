package main;

import java.io.IOException;
import java.util.Scanner;

import data.CustomReadFile;
import data.CustomWriteFile;

public class Individual {
	
	private double uniformRate = 0.5;
    static int defaultGeneLength = 40;
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
    	this.sc = new Scanner((Readable) this.readFile);
    	this.genes = this.readFile.readArray(sc, size());
    	return this.genes;
	}
    
    /**
     * get Gene
     * @param index
     * @return
     */
    public int getGene(int index) {
        return genes[index];
    }
    
    /**
     * set Gene
     * @param index
     * @param value
     */
    public void setGene(int index, int value) {
        genes[index] = value;
        fitness = 0;
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
            fitness = FitnessCalc.getFitness(this);
        }
        return fitness;
    }
    
    /**
     * Get Only Fitness value
     * @return
     */
	public double getOnlyFitness() {
		return this.fitness;
	}
	
    /**
     * Set Fitness value
     * @param fitness
     */
    public void setFitness(double fitness) {
    	this.fitness = fitness;
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
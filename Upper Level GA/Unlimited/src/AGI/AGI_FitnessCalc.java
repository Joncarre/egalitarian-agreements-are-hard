package AGI;


import java.io.IOException;

import java.util.Scanner;
import java.util.Vector;

import files.CustomReadFile;
import files.CustomWriteFile;
public class AGI_FitnessCalc {
	private static Vector<Vector<Double>> M_preferences;
	private static Vector<Vector<Double>> O_preferences;
	private static Vector<Double> Temporal_preferences; 
	private static Vector<Double> changedResources;
	static int numUsers;
	private int maxValuePreference;
	private int minValuePreference;
	private Vector<AGI_Individual> indiv_Results;
	private Vector<AGI_Individual> M_population;
	private CustomReadFile readFile;
	private CustomWriteFile writeFile;
	Scanner sc;
	
	/**
	 * Constructor
	 * @param _numUser
	 * @param _numPacks
	 */
	public AGI_FitnessCalc(int _numUser) {
		this.numUsers = _numUser;
		this.maxValuePreference = 999;
		this.minValuePreference = -999;
		this.M_preferences = new Vector<Vector<Double>>(this.numUsers);
		this.O_preferences = new Vector<Vector<Double>>(this.numUsers);
		this.changedResources = new Vector<Double>(AGI_Individual.defaultGeneLength);
	}
	
	/**
	 * Used to read preferences from AGS
	 * @param O_preferences
	 */
	public void setO_Preferences(Vector<Vector<Double>> O_preferences) {
		this.O_preferences = O_preferences;
	}
	
	/**
	 * Used to read preferences from AGS
	 * @param preferences
	 */
    public void setM_Preferences(Vector<Vector<Double>> M_preferences) {
    	this.M_preferences = M_preferences;
    }
	
    /**
     * Write preference for each user
     * @throws IOException
     */
    public void writePreferences() throws IOException {
		for(int i = 0; i < numUsers; i++) {
			String text = "";
			this.writeFile = new CustomWriteFile("user" + i + ".txt");
			for(int j = 0; j < AGI_Individual.defaultGeneLength; j++)
				text += M_preferences.get(i).get(j) + " ";
			text += -1.0;
			this.writeFile.writeVector(this.writeFile, text);
			this.writeFile.closeWriteFile(this.writeFile);
		}
    }
    
    /**
     * Generate random preference for each user
     */
    public void randomPreferences() {
        for (int i = 0; i < numUsers; i++) {
        	Vector<Double> newPreference = new Vector<Double>(AGI_Individual.defaultGeneLength);
        	for(int j = 0; j < AGI_Individual.defaultGeneLength; j++)
        		newPreference.add(j, ((Math.random() * this.maxValuePreference) + 1));
        	M_preferences.add(i, newPreference);
        }
    }

    /**
     * Calculate individuals fitness
     * @param individual
     * @return
     */
    public static double getFitness(AGI_Individual individual) {
    	// The assignments for each resource is a vector of numUsers positions
    	double[] assignments = new double[numUsers];
        // Loop through our individuals genes
        for (int i = 0; i < AGI_Individual.defaultGeneLength; i++) {
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
    	this.indiv_Results = new Vector<AGI_Individual>(avgIterations);
    }
    
    public void saveIndividuals(AGI_Individual individual) {
    	this.indiv_Results.add(individual);
    }
    
    public AGI_Individual getBestIndividual() {
    	AGI_Individual best = new AGI_Individual();
    	best = this.indiv_Results.get(0);
    	for(int i = 0; i < this.indiv_Results.size(); i++) {
    		if(this.indiv_Results.get(i).getOnlyFitness() > best.getOnlyFitness()) {
    			best = this.indiv_Results.get(i);
    		}
    	}
    	return best;
    }
	
	/*----------------------------------------*/
	public static Vector<Double> printM_PreferencesAgent0() {
		return M_preferences.get(0);
	}
    
	public void printM_Preferences() {
		for(int i = 0; i < this.M_preferences.size(); i++) {
			System.out.println(this.M_preferences.get(i).toString());
		}
	}
	
	public void printO_Preferences() {
		for(int i = 0; i < this.O_preferences.size(); i++) {
			System.out.println(this.O_preferences.get(i).toString());
		}
	}
}

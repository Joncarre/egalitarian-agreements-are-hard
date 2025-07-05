package data;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class CustomReadFile extends FileReader implements ICustomReadFile {
	private double endLine;
	/**
	 * Constructora que recibe nombre de fichero
	 * @param name
	 * @throws IOException 
	 */
	public CustomReadFile(String name) throws IOException {
		super(new java.io.File( "." ).getCanonicalPath() + "/src/data/file/" + name);
		this.endLine = -1000;
	}
	
	@Override
	public Double readDouble(Scanner in) {
		in.useDelimiter(" ");
		String elemString = in.next();
		return Double.parseDouble(elemString);
	}
	
	@Override
	public int readInt(Scanner in) {
		in.useDelimiter("\n");
		String elemString = in.next();
		return Integer.parseInt(elemString);
	}
	
	@Override
	public Vector<Double> readVector(Scanner in) throws IOException {
		Vector<Double> newPreference = new Vector<Double>();
		double element = readDouble(in);
		while(element != endLine) {
			if (element != endLine)
				newPreference.add(element);
			element = readDouble(in);
		}
		return newPreference;
	}

	@Override
	public void closeReadFile(CustomReadFile file) {
		try {
			file.close();
		} catch (IOException e) {
			System.out.println("Error - closeFileRead: " + e.getMessage());
		}
	}

	@Override
	public int[] readArray(Scanner in, int sizeArray) {
		int[] individual = new int[sizeArray];
		int element = in.nextInt();
		int i = 0;
		while(element != endLine) {
			if (element != endLine)
				individual[i] = element;
			element = in.nextInt();
			i++;
		}
		return individual;
	}
}
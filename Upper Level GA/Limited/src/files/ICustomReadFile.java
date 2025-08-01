package files;

import java.io.IOException;

import java.util.Scanner;
import java.util.Vector;

public interface ICustomReadFile {
	/** Cierra el fichero de lectura */
	public abstract void closeReadFile(CustomReadFile file);
	/** Lee un vector del fichero de lectura 
	 * @throws IOException */
	public abstract Vector<Double> readVector(Scanner in) throws IOException;
	/** Lee un double de fichero de lectura */
	public abstract double readDouble(Scanner in);
	/** Lee un array del fichero de lectura */
	public abstract int[] readArray(Scanner in, int sizeArray);
	/** Lee un int de fichero de lectura */
	int readInt(Scanner in);
}
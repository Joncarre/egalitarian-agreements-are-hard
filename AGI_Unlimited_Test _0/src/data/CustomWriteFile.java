package data;
import java.io.FileWriter;
import java.io.IOException;


public class CustomWriteFile extends FileWriter implements ICustomWriteFile{
	/**
	 * Constructora que recibe nombre de fichero
	 * @param name
	 * @throws IOException 
	 */
	public CustomWriteFile(String name) throws IOException {
		super(new java.io.File( "." ).getCanonicalPath() + "/src/data/file/" + name);
	}
	
	@Override
	public void writeVector(CustomWriteFile file, String text) {
		try {
			file.write(text + " \n");
		} catch (IOException e) {
			System.out.println("Error - writeVector: " + e.getMessage());
		}
	}
	
	@Override
	public void closeWriteFile(CustomWriteFile file) {
		try {
			file.close();
		} catch (IOException e) {
			System.out.println("Error - closeFileWrite: " + e.getMessage());
		}
	}
}
package data;

public interface ICustomWriteFile {
	/** Cierra el fichero de escritura */
	public abstract void closeWriteFile(CustomWriteFile file);
	/** Escribe en el fichero de escritura */
	public abstract void writeVector(CustomWriteFile file, String text);
}

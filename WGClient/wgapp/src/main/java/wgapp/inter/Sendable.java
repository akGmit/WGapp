package wgapp.inter;
/**
 * Interface for all objects which can be sent to server.
 * 
 * @author Andrius Korsakas
 */
public interface Sendable {
	/**
	 * Method to return JSON representation of an object
	 * @return String containing JSON representation of an object.
	 */
	String toJSON();
}

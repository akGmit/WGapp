package wgapp.inter;
/**
 * Functional observer interface. Single update method.
 * 
 * @author ak
 *
 */
public interface Observer {
	/**
	 * Method representing some updated state of a observed subject
	 * @param obj Object representing update of a subject
	 */
	void update(Object obj);
}

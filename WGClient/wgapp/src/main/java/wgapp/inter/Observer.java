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
	 * @param event String representation of event, useful for Observer object to process update according to event type.
	 * @param obj Object representing update of a subject
	 */
	void update(String event, Object obj);
}

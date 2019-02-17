package wgapp.inter;
/**
 * Interface specifying observable subject methods.
 * 
 * @author ak
 *
 */
public interface Subject {
	/**
	 * Notify all observers.
	 * @param obj Object representing new state/update sent to observer.
	 */
	void notifyObserver(String event, Object obj);
	/**
	 * Add observer to observers list.
	 * @param observer Observer of a subject
	 */
	void addObserver(Observer observer);
	/**
	 * Remove observer from subjects observer list.
	 * @param observer Observer to remove.
	 */
	void removeObserver(Observer observer);
}

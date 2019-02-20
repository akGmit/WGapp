package wgapp.gui;
/**
 * Abstract class with a single intention of providing access to main JavaFX UI class.
 * 
 * @author ak
 *
 */
public abstract class AbstractController {
	protected UI mainUI;
	
	public void setMainApp(UI mainUI) {
		this.mainUI = mainUI;
	}
}

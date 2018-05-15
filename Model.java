import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private List<Observer> observers; // Observers of the Model
    private boolean isSelect; // Flag if in Select mode
    private boolean isMulti; // Flag if multiSelect enabled
    private int thickness; // Currently used thickness mode
    private Color fillColor; // Currently used fill colour
    private Color strokeColor; // Currently used stroke colour
    private char type; // Currently used drawing type
    private ArrayList<Drawable> drawables; // List of items that are currently drawn
    private Image lastDrawn; // The last image that was drawn of this model

    /**
     * Create a new model.
     */
    public Model() {
        observers = new ArrayList<Observer>();
        isSelect = false;
        isMulti = false;
        thickness = 1;
        fillColor = Color.WHITE;
        strokeColor = Color.BLACK;
        type = 'f';
        drawables = new ArrayList<Drawable>();
    }

    /**
     * Add an observer to be notified when this model changes.
     */
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    /**
     * Remove an observer from this model.
     */
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    /**
     * Notify all observers that the model has changed.
     */
    public void notifyObservers() {
        for (Observer observer: this.observers) {
            observer.update(this);
        }
    }
    
    // Set Select mode
    public void setSelect(boolean val) {
    	isSelect = val;
    	notifyObservers();
    }
    
    // Get Select mode
    public boolean getSelect() {
    	return isSelect;
    }
    
    // Set Thickness
    public void setThickness(int val) {
    	thickness = val;
    	notifyObservers();
    }
    
    // Get Thickness
    public int getThickness() {
    	return thickness;
    }
    
    // Set Fill Color
    public void setFillColor(Color c) {
    	fillColor = c;
    	notifyObservers();
    }
    
    // Get Fill Color
    public Color getFillColor() {
    	return fillColor;
    }
    
    // Set Stroke Color
    public void setStrokeColor(Color c) {
    	strokeColor = c;
    	notifyObservers();
    }

    // Get Stroke Color
    public Color getStrokeColor() {
    	return strokeColor;
    }
    
    // Get Drawable to array
    public ArrayList<Drawable> getDrawables() {
    	return drawables;
    }
    
    // Add Drawable to array
    public void addDrawable(Drawable d) {
    	drawables.add(d);
    	notifyObservers();
    }
    
    // Get type
    public char getType() {
    	return type;
    }
    
    // Get last added Drawable
    public Drawable getBack() {
    	return drawables.get(drawables.size() - 1);
    }
 	
 	// Set type
 	public void setType(char type) {
 		this.type = type;
 	}
 	
 	// Set drawables
 	public void setDrawables(ArrayList<Drawable> d) {
 		drawables = d;
 	}
 	
 	// Change isMulti flag
 	public void setMulti(boolean b) {
 		isMulti = b;
 	}
 	
 	// Get isMulti flag
 	public boolean getMulti() {
 		return isMulti;
 	}
 	
 	// Get last drawn image
 	public Image getLastDrawn() {
 		return lastDrawn;
 	}
 	
 	// Set last drawn image
 	public void setLastDrawn(Image i) {
 		lastDrawn = i;
 	}
}

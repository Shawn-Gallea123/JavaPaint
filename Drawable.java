import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Drawable {
	private Color stroke; // Currently used stroke colour
	private Color fill; // Currently used fill colour
	private int thickness; // Currently used stroke thickness
	private ArrayList<Integer> xPoints; // Array of X coordinates used by Drawable
	private ArrayList<Integer> yPoints; // Array of Y coordinates used by Drawable
	private boolean isSelected; // Flag if the object is selected
	private AffineTransform transform; // Current applied transformations
	
	// f = Freehand
	// s = straight line
	// r = rectangle
	// e = ellipse
	private char type;
	
	// Ctor
	public Drawable(char type, Color stroke, Color fill, int thickness) {
		this.type = type;
		this.stroke = stroke;
		this.fill = fill;
		this.thickness = thickness;
		xPoints = new ArrayList<Integer>();
		yPoints = new ArrayList<Integer>();
		isSelected = false;
		transform = new AffineTransform();
	}
	
	// Return type
	public char getType() {
		return type;
	}
	
	// Return stroke
	public Color getStroke() {
		return stroke;
	}
	
	// Return fill
	public Color getFill() {
		return fill;
	}
	
	// Set stroke
	public void setStroke(Color c) {
		stroke = c;
	}
	
	// Set fill
	public void setFill(Color c) {
		fill = c;
	}
	
	// Return thickness
	public int getThickness() {
		return thickness;
	}
	
	// Set thickness
	public void setThickness(int thick) {
		thickness = thick;
	}
	
	// Return X Points
	public int[] getXPoints() {
		return xPoints.stream().mapToInt(Integer::intValue).toArray();
	}
	
	// Return Y points
	public int[] getYPoints() {
		return yPoints.stream().mapToInt(Integer::intValue).toArray();
	}
	
	// Return X arraylist
	public ArrayList<Integer> getXArr() {
		return xPoints;
	}
	
	// Return Y arraylist
	public ArrayList<Integer> getYArr() {
		return yPoints;
	}
	
	// Return Points array sizes
	public int getSize() {
		return xPoints.size();
	}
	
	// Add X Point
	public void addX(int x) {
		xPoints.add(x);
	}
	
	// Add Y Point
	public void addY(int y) {
		yPoints.add(y);
	}
	
	// Set selected
	public void setSelected(boolean b) {
		isSelected = b;
	}
	
	// Get selected
	public boolean getSelected() {
		return isSelected;
	}
	
	// Set AffineTransform
	public void setAffineTransform(AffineTransform a) {
		transform = a;
	}
	
	// Get AffineTransfrom
	public AffineTransform getAffineTransform() {
		return transform;
	}
	
}

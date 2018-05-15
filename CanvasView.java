import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class CanvasView extends JPanel implements Observer {
	
	private Model model; // The model
	private Image buffer; // Image buffer used for double buffering
	private Graphics2D g2; // Graphics2D object for drawing
	
	// ctor
	public CanvasView(Model model) {
		this.model = model;
		model.addObserver(this);
		
		// Add object to drawables on mouse click
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!model.getSelect()) {
					Drawable d = new Drawable(model.getType(), model.getStrokeColor(), model.getFillColor(), model.getThickness());
					d.addX(e.getX());
					d.addY(e.getY());
					model.addDrawable(d);
				}  else {
					try {
						checkBounds(e);
					} catch (NoninvertibleTransformException e1) {
					}
					model.notifyObservers();
				}
			}
		});
		
		// Change/Add coordinates for needed behaviour
		this.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				if (!model.getSelect() && model.getBack().getType() == 'f') {
					addXY(e.getX(), e.getY());
				} else if (!model.getSelect() && model.getBack().getType() == 's') {
					changeEnd(e.getX(), e.getY());
				} else if (!model.getSelect() && model.getBack().getType() == 'r') {
					changeEnd(e.getX(), e.getY());
				} else if (!model.getSelect() && model.getBack().getType() == 'e') {
					changeEnd(e.getX(), e.getY());
				}
			}
		});
	
		
	}
	
	// Check if transformed mouse is inside, used for rectangle
	private boolean isInside(Point MT, int minX, int minY, int maxX, int maxY) {
		int mX = (int) MT.getX();
		int mY = (int) MT.getY();
		
		return (mX >= minX) && (mX <= maxX) && (mY >= minY) && (mY <= maxY);
	}
	
	// General bounds checking method
	private void checkBounds(MouseEvent e) throws NoninvertibleTransformException {
		
		// Get drawables from model
		ArrayList<Drawable> drawables = model.getDrawables();
		int tempThick = model.getThickness();
		
		boolean found = false;
		int len = drawables.size();
		
		// Iterate through all drawables backwards
		for (int z = len - 1; z >= 0; --z) {
			Drawable d = drawables.get(z);
			ArrayList<Integer> xArr = d.getXArr();
			ArrayList<Integer> yArr = d.getYArr();
			int size = xArr.size();
			int minX = 4000000;
			int minY = 4000000;
			int maxX = -1;
			int maxY = -1;
			
			for (int i = 0; i < size; ++i) {
				if (xArr.get(i) < minX) {
					minX = xArr.get(i);
				}
				if (yArr.get(i) < minY) {
					minY = yArr.get(i);
				}
				if (xArr.get(i) > maxX) {
					maxX = xArr.get(i);
				}
				if (yArr.get(i) > maxY) {
					maxY = yArr.get(i);
				}
			}
			
			// Store mousepoint, create new point to be used for
			// transformed mousepoint
			Point MT = new Point();
			Point M = new Point(e.getPoint());
			
			// Invert the objects AffineTransform matrix
			AffineTransform Inverted = d.getAffineTransform().createInverse();
			Inverted.transform(M, MT);
			
			// Check if inside before doing more specific bounds checking
			if (checkInsideFound(MT, minX, minY, maxX, maxY, found, d)) {
				// Only further hit-test if inside maximum bounding box
				if ((d.getType() == 'f' || d.getType() == 's') && isNear(d, MT)) {
					// Checking using point-to-line hit-testing for lines
					d.setSelected(true);
					found = true;
					tempThick = d.getThickness();
					model.setFillColor(d.getFill());
					model.setStrokeColor(d.getStroke());
				} else if (d.getType() == 'r' || d.getType() == 'e') {
					// Create temporary Rectangle2D object, use contains to check if mouse inside
					Point2D.Double first = new Point2D.Double(d.getXArr().get(0), d.getYArr().get(0));
					Point2D.Double second = new Point2D.Double(d.getXArr().get(1), d.getYArr().get(1));
					Rectangle2D temp = new Line2D.Double(first,second).getBounds2D();
					
					if (temp.contains((int) MT.getX(), (int) MT.getY())) {
						d.setSelected(true);
						found = true;
						tempThick = d.getThickness();
						model.setFillColor(d.getFill());
						model.setStrokeColor(d.getStroke());
					}
				}
			}
			
		}
		
		// Set model thickness based on selected item
		model.setThickness(tempThick);
		
	}
	
	private boolean checkInsideFound(Point MT, int minX, int minY, int maxX, int maxY, boolean found, Drawable d) {
		if (model.getMulti()) {
			return isInside(MT, minX, minY, maxX, maxY);
		} else {
			if (isInside(MT, minX, minY, maxX, maxY) && !found) {
				return true;
			} else {
				d.setSelected(false);
				return false;
			}
		}
	}
	
	// Check if transformed mouse point is near line
	private boolean isNear(Drawable d, Point MT) {
		ArrayList<Integer> Xs = d.getXArr();
		ArrayList<Integer> Ys = d.getYArr();
		int len = Xs.size();
		
		for (int i = 0; i < len - 1; ++i) {
			if (Line2D.ptSegDist(Xs.get(i), Ys.get(i), Xs.get(i+1), Ys.get(i+1), MT.getX(), MT.getY()) <= 5) {
				return true;
			}
		}
		
		return false;
		
	}
	
	// Get minimum from list
	private int getMin(ArrayList<Integer> list) {
		int min = 4000000;
		for (Integer i : list) {
			if (i < min) {
				min = i;
			}
		}
		return min;
	}
	
	// Get maximum from list
	private int getMax(ArrayList<Integer> list) {
		int max = -1;
		for (Integer i : list) {
			if (i > max) {
				max = i;
			}
		}
		return max;
	}
	
	// Paint component
	public void paintComponent(Graphics g) {
		
		// Get drawables objects from model
		ArrayList<Drawable> drawables = model.getDrawables();
		
		// Create blank buffer image
		buffer = createImage(1024, 768);
		
		// Set graphics object
		Graphics2D g2 = (Graphics2D) buffer.getGraphics();

		// Iterate through all drawables in model, rendering each to an image
		for (Drawable d : drawables) {
			// Save current transform info
			AffineTransform save = g2.getTransform();
			g2.setColor(d.getStroke());
			g2.setStroke(new BasicStroke(d.getThickness()));
			g2.setTransform(d.getAffineTransform());
			
			
			if (d.getType() == 'f') {
				g2.drawPolyline(d.getXPoints(), d.getYPoints(), d.getSize());
			} else if (d.getType() == 's') {
				if (d.getSize() > 1) {
					g2.drawLine(d.getXArr().get(0), d.getYArr().get(0), d.getXArr().get(1), d.getYArr().get(1));
				}
			} else {
				if (d.getSize() > 1) {
					
					int x1 = d.getXArr().get(0);
					int y1 = d.getYArr().get(0);
					int x2 = d.getXArr().get(1);
					int y2 = d.getYArr().get(1);
					
					int width = Math.abs(x1 - x2);
					int height = Math.abs(y1 - y2);
					int originX = Integer.min(x1, x2);
					int originY = Integer.min(y1,  y2);
					
					if (d.getType() == 'r') {
						g2.setColor(d.getFill());
						g2.fillRect(originX, originY, width, height);
						g2.setColor(d.getStroke());
						g2.drawRect(originX, originY, width, height);
					} else {
						g2.setColor(d.getFill());
						g2.fillOval(originX, originY, width, height);
						g2.setColor(d.getStroke());
						g2.drawOval(originX, originY, width, height);
					}
					
				}
			}
			
			// If object is selected, draw cyan box around it
			if (d.getSelected()) {
				int minX = getMin(d.getXArr());
				int maxX = getMax(d.getXArr());
				int minY = getMin(d.getYArr());
				int maxY = getMax(d.getYArr());
				g2.setColor(Color.CYAN);
				g2.setStroke(new BasicStroke(3));
				g2.drawRect(minX, minY, maxX - minX, maxY - minY);
			}
			
			// Reset transformations
			g2.setTransform(save);
			
		}
		
		// Draw image (buffer) to screen
		Graphics2D g3 = (Graphics2D) g;
		g3.drawImage(buffer, 0, 0, null);
		model.setLastDrawn(buffer);
		
	}
	
	// Update the canvas view
	public void update(Object observable) {
		paintComponent(getGraphics());
	}
	
	// Add X,Y coordinates
	private void addXY(int x, int y) {
		model.getBack().getXArr().add(x);	 
		model.getBack().getYArr().add(y);
	 	model.notifyObservers();
	}
	
	// Change end coordinate for a straight line object
	private void changeEnd(int x, int y) {
	 ArrayList<Integer> xArr = model.getBack().getXArr();
	 ArrayList<Integer> yArr = model.getBack().getYArr();
	 int size = model.getBack().getSize();
	 		
	 if (size == 2) {
	 	xArr.set(size - 1, x);
	 	yArr.set(size - 1, y);
	 } else {
	 	xArr.add(x);
	 	yArr.add(y);
	 }
	 
	 model.notifyObservers();
	 
	}
}

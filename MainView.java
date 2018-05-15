import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

public class MainView extends JFrame implements Observer {

    private Model model;
    private JMenuBar menuBar;
    private JMenu file;
    private JMenu edit;
    private JMenu format;
    private JToolBar toolbar;
    private JToggleButton select;
    private JToggleButton draw;
    private JToggleButton multi;
    private JComboBox<String> shape;
    private JComboBox<String> thickness;
    private JButton fill_color;
    private JButton stroke_color;
    private JMenuItem item_New;
    private JMenuItem item_Export;
    private JMenuItem item_Exit;
    private JCheckBoxMenuItem item_Selection_Mode;
    private JCheckBoxMenuItem item_Drawing_Mode;
    private JMenuItem item_Delete_Shape;
    private JMenuItem item_Transform_Shape;
    private JMenuItem item_Stroke_Width;
    private JMenuItem item_Fill_Color;
    private JMenuItem item_Stroke_Color;
    private CanvasView canvas;
    private Swatch fillSwatch;
    private Swatch strokeSwatch;
    private Swatch item_FillSwatch;
    private Swatch item_StrokeSwatch;
    private TransformDiag tDiag;
    
    /**
     * Create a new View.
     */
    public MainView(Model model) {
        // Set up the window.
        this.setTitle("CS 349 W18 A2");
        this.setMinimumSize(new Dimension(128, 128));
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.setLayout(new BorderLayout());

        // Hook up this observer so that it will be notified when the model
        // changes.
        this.model = model;
        model.addObserver(this);

        
        // Create menu bar
        menuBar = new JMenuBar();
        
        // Create File, Edit, and Format menus
        file = new JMenu("File");
        edit = new JMenu("Edit");
        format = new JMenu("Format");
        
        
        // Add New, Export and Exit menu items to the file menu
        item_New = new JMenuItem("New");
        item_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        
        item_Export = new JMenuItem("Export...");
        item_Export.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
        item_Export.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		deselectAll();
        		new exportDiag();
        	}
        });
        
        
        item_Exit = new JMenuItem("Exit");
        item_Exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
        
        item_Exit.addActionListener((e) -> System.exit(0));
        
        // Add items to File
        file.add(item_New);
        file.add(item_Export);
        file.add(item_Exit);
        
        // Add Selection Mode and Drawing Mode items to the Edit menu
        item_Selection_Mode = new JCheckBoxMenuItem("Selection Mode");
        item_Selection_Mode.setSelected(false);
        item_Selection_Mode.addActionListener((e) -> model.setSelect(true));
        item_Selection_Mode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        
        item_Drawing_Mode = new JCheckBoxMenuItem("Drawing Mode");
        item_Drawing_Mode.setSelected(true);
        item_Drawing_Mode.addActionListener(e -> model.setSelect(false));
        item_Drawing_Mode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_DOWN_MASK));
        
        // Add Delete Shape item and Transform Shape item to Edit menu
        item_Delete_Shape = new JMenuItem("Delete Shape");
        item_Delete_Shape.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        
        // Action listener to delete selected shape
        item_Delete_Shape.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		removeSelected();
        	}
        });
        
        item_Delete_Shape.setEnabled(false);
        item_Transform_Shape = new JMenuItem("Transform Shape");
        item_Transform_Shape.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK));
        item_Transform_Shape.setEnabled(false);
        item_Transform_Shape.addActionListener(e -> tDiag.setVisible(true));
        
        // Create new transform dialog
        tDiag = new TransformDiag();
        
        edit.add(item_Selection_Mode);
        edit.add(item_Drawing_Mode);
        edit.add(item_Delete_Shape);
        edit.add(item_Transform_Shape);
        
       
        item_Stroke_Width = new JMenuItem("Stroke Width");
        item_Stroke_Width.addActionListener(e -> new TDiag());
        
        item_Fill_Color = new JMenuItem("Fill Colour...");
        item_Stroke_Color = new JMenuItem("Stroke Colour...");
        
        item_Fill_Color.addActionListener(e -> alterColor('f'));
        item_FillSwatch = new Swatch('f');
        item_Fill_Color.setIcon(item_FillSwatch);
        
        item_Stroke_Color.addActionListener(e -> alterColor('s'));
        item_StrokeSwatch = new Swatch('s');
        item_Stroke_Color.setIcon(item_StrokeSwatch);
        
        format.add(item_Stroke_Width);
        format.add(item_Fill_Color);
        format.add(item_Stroke_Color);
        
        // Add menus to menubar
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(format);
        this.setJMenuBar(menuBar);
        
        // Create toolbar
        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        // Create toggle buttons and add them
        select = new JToggleButton("Select");
        
        select.addActionListener((e) -> model.setSelect(true));
       
        select.setSelected(false);
        
        draw = new JToggleButton("Draw");
        
        // Action listener to change to draw mode
        draw.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		model.setSelect(false);
        		model.setMulti(false);
        		deselectAll();
        		model.notifyObservers();
        	}
        });
        
        draw.setSelected(true);
        
        multi = new JToggleButton("Multi");
        multi.setSelected(false);
        
        multi.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (model.getMulti()) {
        			deselectAll();
        		}
        		if (model.getSelect()) {
        			model.setMulti(!model.getMulti());
        		}
        		model.notifyObservers();
        	}
        });
        
        // Create combobox
        String[] shapes = {"Freeform", "Straight line", "Rectangle", "Ellipse"};
        shape = new JComboBox<String>(shapes);
        
        // Action listener to change currently used drawing type
        shape.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		int selected = shape.getSelectedIndex();
        		if (selected == 0) {
        			model.setType('f');
        		} else if (selected == 1) {
        			model.setType('s');
        		} else if (selected == 2) {
        			model.setType('r');
        		} else {
        			model.setType('e');
        		}
        	}
        });
        
        // Create thickness combobox
        String[] thicknessvals = {"1px", "2px", "3px", "4px", "5px", "6px", "7px", "8px", "9px", "10px"};
        thickness = new JComboBox<String>(thicknessvals);
        
        thickness.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent e) {
        		model.setThickness(thickness.getSelectedIndex() + 1);
        		applyThickSelected(thickness.getSelectedIndex() + 1);
        	}
        });
        
        fillSwatch = new Swatch('f');
        strokeSwatch = new Swatch('s');
        
        // Create Fill Colour and Stroke Colour buttons
        fill_color = new JButton("Fill Colour", fillSwatch);
        stroke_color = new JButton("Stroke Colour", strokeSwatch);
        
        // Add ability to choose Stroke and Fill Colors from toolbar
        fill_color.addActionListener(e -> alterColor('f'));
        stroke_color.addActionListener(e -> alterColor('s'));
        
        // Add items to toolbar
        toolbar.add(select);
        toolbar.add(draw);
        toolbar.add(multi);
        toolbar.add(shape);
        toolbar.add(thickness);
        toolbar.add(fill_color);
        toolbar.add(stroke_color);
        
        // Add toolbar to top
        this.add(toolbar, BorderLayout.PAGE_START);
        
        // Create and add new canvas for the rest of the window
        canvas = new CanvasView(model);
        this.add(canvas, BorderLayout.CENTER);
         
        // Create new canvas
        item_New.addActionListener(new AbstractAction() {
        	 
        	public void actionPerformed(ActionEvent e) {
        		 model.setDrawables(new ArrayList<Drawable>());
        		 model.notifyObservers();
            }
        	 
        });
        setVisible(true);
    }
    
    // Helper to alter colour given a type
    private void alterColor(char type) {
    	if (type == 'f') {
    		Color chosenColor = JColorChooser.showDialog(null, "Choose a Fill Colour", model.getFillColor());
    		model.setFillColor(chosenColor);
			applyToSelected(chosenColor, 'f');
    	} else {
    		Color chosenColor = JColorChooser.showDialog(null, "Choose a Stroke Colour", model.getStrokeColor());
    		model.setStrokeColor(chosenColor);
    		applyToSelected(chosenColor, 's');
    	}
    }
    
    // Remove selected item
    private void removeSelected() {
    	ArrayList<Drawable> drawables = model.getDrawables();
    	int len = drawables.size();
    	
    	for (int i = 0; i < len; ++i) {
    		if (drawables.get(i).getSelected()) {
    			drawables.remove(i);
    			model.notifyObservers();
    			--i;
    			--len;
    		}
    	}
    	
    }
    
    // Apply new color to selected object
    public void applyToSelected(Color c, char type) {
    	ArrayList<Drawable> drawables = model.getDrawables();
    	
    	for (Drawable d : drawables) {
    		if (d.getSelected()) {
    			if (type == 'f') {
    				d.setFill(c);
    			} else {
    				d.setStroke(c);
    			}
    		}
    	}
    	
    }
    
    // Apply new thickness to selected object
    public void applyThickSelected(int thick) {
    	ArrayList<Drawable> drawables = model.getDrawables();
    	
    	for (Drawable d : drawables) {
    		if (d.getSelected()) {
    			d.setThickness(thick);
    		}
    	}
    }

    // Check if any items in the model are currently selected
    public boolean anySelected() {
    	ArrayList<Drawable> drawables = model.getDrawables();
    	
    	boolean temp = false;
    	for (Drawable d : drawables) {
    		if (d.getSelected()) {
    			temp = true;
    		}
    	}
    	return temp;
    }
    
    /**
     * Update with data from the model.
     */
    public void update(Object observable) {

        if (model.getSelect()) {
        	draw.setSelected(false);
        	select.setSelected(true);
        	item_Selection_Mode.setSelected(true);
        	item_Drawing_Mode.setSelected(false);
        } else {
        	select.setSelected(false);
        	multi.setSelected(false);
        	draw.setSelected(true);
        	item_Selection_Mode.setSelected(false);
        	item_Drawing_Mode.setSelected(true);
        }
        
        if (model.getMulti()) {
        	multi.setSelected(true);
        } else {
        	multi.setSelected(false);
        }
        
        boolean changeF = false;
        boolean changeS = false;
        
        if (fillSwatch.getColor() == model.getFillColor()) {
        	changeF = false;
        } else {
        	changeF = true;
        }
        
        if (strokeSwatch.getColor() == model.getStrokeColor()) {
        	changeS = false;
        } else {
        	changeS = true;
        }
        
        fillSwatch.setColor(model.getFillColor());
        strokeSwatch.setColor(model.getStrokeColor());
        if (model.getMulti()) {
        	if (changeF) {
        		applyToSelected(model.getFillColor(), 'f');
        	}
        	if (changeS) {
        	applyToSelected(model.getStrokeColor(), 's');
        	}
        }
        
        if (thickness.getSelectedIndex() != (model.getThickness() - 1)) {
        	thickness.setSelectedIndex(model.getThickness() - 1);
        }
        
        if (anySelected()) {
        	item_Delete_Shape.setEnabled(true);
        	item_Transform_Shape.setEnabled(true);
        } else {
        	item_Delete_Shape.setEnabled(false);
        	item_Transform_Shape.setEnabled(false);
        }
        
        repaint();
        
    }
    
    // Deselect all objects
    private void deselectAll() {
    	ArrayList<Drawable> drawables = model.getDrawables();
    	for (Drawable d : drawables) {
    		d.setSelected(false);
    	}
    	model.notifyObservers();
    }
    
    // Thickness dialogue class
    class TDiag extends JDialog {
    	// ctor
    	public TDiag() {
    		this.setTitle("Choose Thickness");
    		 // Create thickness combobox
            String[] thicknessvals = {"1px", "2px", "3px", "4px", "5px", "6px", "7px", "8px", "9px", "10px"};
            JComboBox<String> thick = new JComboBox<String>(thicknessvals);
            thick.setSelectedIndex(model.getThickness() - 1);
            thick.addActionListener(e -> model.setThickness(thick.getSelectedIndex() + 1));
    		this.setSize(400, 70);
    		this.setModal(true);
    		this.add(thick);
    		this.setVisible(true);
    	}
    }
    
    // Inner class for displaying color in button
    private class Swatch implements Icon {
    	private char type;
    	private Color sColor;
    	
    	// ctr
    	Swatch(char t) {
    		type = t;
    	}
    	
    	// Return icon width
    	public int getIconWidth() {
    		return 20;
    	}
    	
    	// Return icon height
    	public int getIconHeight() {
    		return 20;
    	}
    	
    	// Set swatch color
    	public void setColor(Color c) {
    		sColor = c;
    	}
    	
    	// Get swatch color
    	public Color getColor() {
    		return sColor;
    	}
    	
    	// Paint swatch
    	public void paintIcon(Component c, Graphics g, int x, int y) {
    		
    		if (type == 'f') {
    			sColor = model.getFillColor();
    			g.setColor(sColor);
    		} else {
    			sColor = model.getStrokeColor();
    			g.setColor(sColor);
    		}
    		g.fillRect(x,  y, 20, 20);
    	}
    }
    
    // Inner class for displaying transformation dialog
    private class TransformDiag extends JDialog {
    	JPanel firstRow;
    	JPanel secondRow;
    	JPanel thirdRow;
    	JPanel fourthRow;
    	JLabel translateText;
    	JLabel rotateText;
    	JLabel scaleText;
    	JSpinner xTransSpin;
    	JSpinner yTransSpin;
    	JSpinner rotateSpin;
    	JSpinner xScaleSpin;
    	JSpinner yScaleSpin;
    	JButton OK;
    	JButton Cancel;
    	
    	// Reset all spinner values
    	private void resetSpinners() {
    		xTransSpin.setValue(0);
			yTransSpin.setValue(0);
			rotateSpin.setValue(0);
			xScaleSpin.setValue(1.0);
			yScaleSpin.setValue(1.0);
    	}
    	
    	// ctor
    	public TransformDiag() {
    		this.setSize(400, 200);
    		this.setModal(true);
    		this.setTitle("Transform shape");
    		
    		firstRow = new JPanel();
    		firstRow.setLayout(new FlowLayout());
    		
    		secondRow = new JPanel();
    		secondRow.setLayout(new FlowLayout());
    		
    		thirdRow = new JPanel();
    		thirdRow.setLayout(new FlowLayout());
    		
    		fourthRow = new JPanel();
    		fourthRow.setLayout(new FlowLayout());
    		
    		translateText = new JLabel("Translate (px): ");
    		
    		xTransSpin = new JSpinner(new SpinnerNumberModel(0, -400000, 400000, 1));
    		yTransSpin = new JSpinner(new SpinnerNumberModel(0, -400000, 400000, 1));
    		rotateSpin = new JSpinner(new SpinnerNumberModel(0, -400000, 400000, 1));
    		xScaleSpin = new JSpinner(new SpinnerNumberModel(1.0, -400000.0, 400000.0, 1.0));
    		yScaleSpin = new JSpinner(new SpinnerNumberModel(1.0, -400000.0, 400000.0, 1.0));
    		
    		rotateText = new JLabel("Rotate (degrees): ");
    		scaleText = new JLabel("Scale (times):");
    		
    		OK = new JButton("Ok");
    		
    		Cancel = new JButton("Cancel");
    		
    		// Action listener to reset spinners
    		Cancel.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				resetSpinners();
    			}
    		});
    		
    		// Action listener to close window
    		Cancel.addActionListener(e -> this.setVisible(false));
    		
    		// Window listenr used when user clicks exit button
    		this.addWindowListener(new WindowAdapter() {
    			public void windowClosing(WindowEvent e) {
    				resetSpinners();
    			}
    		});
    		
    		// Action listener for OK button to apply transformations to selected shape
    		OK.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				
    				ArrayList<Drawable> drawables = model.getDrawables();
    				
    				for (Drawable d : drawables) {
    					if (d.getSelected()) {
    						AffineTransform newtrans = new AffineTransform();
    						newtrans.translate((int) xTransSpin.getValue(), (int) yTransSpin.getValue());
    						double midX = (getMax(d.getXArr()) + getMin(d.getXArr())) / 2;
    						double midY = (getMax(d.getYArr()) + getMin(d.getYArr())) / 2;
    						
    						
    	    				newtrans.rotate(Math.toRadians((int)rotateSpin.getValue()), midX, midY);
    	    				
    	    				newtrans.translate(midX, midY);
    	    				newtrans.scale((Double)xScaleSpin.getValue(), (Double)yScaleSpin.getValue());
    	    				newtrans.translate(-midX, -midY);
    	    				
    	    				d.setAffineTransform(newtrans);
    	    				
    	    				
    					}
    				}
    				model.notifyObservers();
    				Cancel.doClick();
    			}
    		});
    		
    		// Use grid layout with nested JPanels that use flow layouts
    		this.setLayout(new GridLayout(4, 1));
    		
    		// Add items to rows
    		firstRow.add(translateText);
    		firstRow.add(new JLabel("x:"));
    		firstRow.add(xTransSpin);
    		firstRow.add(new JLabel("y:"));
    		firstRow.add(yTransSpin);
    		secondRow.add(rotateText);
    		secondRow.add(rotateSpin);
    		thirdRow.add(scaleText);
    		thirdRow.add(new JLabel("x:"));
    		thirdRow.add(xScaleSpin);
    		thirdRow.add(new JLabel("y:"));
    		thirdRow.add(yScaleSpin);
    		fourthRow.add(OK);
    		fourthRow.add(Cancel);
    		
    		// Add rows to this
    		this.add(firstRow);
    		this.add(secondRow);
    		this.add(thirdRow);
    		this.add(fourthRow);
    	}
    }
    
    // Private class for export dialog
    private class exportDiag extends JDialog {
    	private JTextField nameEntry; // Name entry field
    	private JPanel panel; // Panel
    	private JPanel inner; // Inner panel 1
    	private JPanel inner2; // Inner panel 2
    	private JButton OK; // OK button
    	private JButton Cancel; // Cancel button
    	
    	// ctor
    	public exportDiag() {
    		this.setSize(400, 200);
    		this.setModal(true);
    		this.setTitle("Export as PNG File");
    		
    		nameEntry = new JTextField();
    		nameEntry.setPreferredSize(new Dimension(100, 20));
    		panel = new JPanel();
    		inner = new JPanel();
    		inner2 = new JPanel();
    		OK = new JButton("OK");
    		Cancel = new JButton("Cancel");
    		
    		//inner.setLayout(new GridLayout(1, 2));
    		inner.add(new JLabel("File Name: "));
    		inner.add(nameEntry);
    		
    		//inner2.setLayout(new FlowLayout());
    		inner2.add(OK);
    		inner2.add(Cancel);
    		
    		panel.setLayout(new GridLayout(2, 1));
    		panel.add(inner);
    		panel.add(inner2);
    		
    		// Save image to .png file
    		OK.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				BufferedImage img = (BufferedImage) model.getLastDrawn();
    				try {
    					if (!nameEntry.getText().equals("")) {
    						ImageIO.write(img, "png", new File(nameEntry.getText() + ".png"));
    					} else {
    						ImageIO.write(img, "png", new File("default.png"));
    					}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
    				
    				Cancel.doClick();
    			}
    		});
    		
    		// Cancel the dialog
    		Cancel.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent e) {
    				nameEntry.setText("");
    			}
    		});
    		
    		Cancel.addActionListener(e -> this.setVisible(false));
    		
    		this.add(panel);
    		this.setVisible(true);
    	}
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
	
    // Get max from list
	private int getMax(ArrayList<Integer> list) {
		int max = -1;
		for (Integer i : list) {
			if (i > max) {
				max = i;
			}
		}
		return max;
	}
    
}

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Client-server graphical editor
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; loosely based on CS 5 code by Tom Cormen
 * @author CBK, winter 2014, overall structure substantially revised
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author CBK, spring 2016 and Fall 2016, restructured Shape and some of the GUI
 *
 * @author Sajjad
 * @author Josue
 */

public class Editor extends JFrame {
	private static String serverIP = "localhost";			// IP address of sketch server
	// "localhost" for your own machine;
	// or ask a friend for their IP address

	private static final int width = 800, height = 800;		// canvas size

	// Current settings on GUI
	public enum Mode {
		DRAW, MOVE, RECOLOR, DELETE
	}
	private Mode mode = Mode.DRAW;				// drawing/moving/recoloring/deleting objects
	private String shapeType = "ellipse";		// type of object to add
	private Color color = Color.black;			// current drawing color

	// Drawing state
	// these are remnants of my implementation; take them as possible suggestions or ignore them
	private Shape curr = null;					// current shape (if any) being drawn
	private ArrayList<Segment> polyline = null; //list of segments to draw freehand
	private Sketch sketch;						// holds and handles all the completed objects
	private int movingID = -1;					// current shape id (if any; else -1) being moved
	private Point drawFrom = null;				// where the drawing started
	private Point moveFrom = null;				// where object is as it's being dragged


	// Communication
	private EditorCommunicator comm;			// communication with the sketch server

	public Editor() {
		super("Graphical Editor");

		sketch = new Sketch();

//		 Connect to server
		comm = new EditorCommunicator(serverIP, this);
		comm.start();

		// Helpers to create the canvas and GUI (buttons, etc.)
		JComponent canvas = setupCanvas();
		JComponent gui = setupGUI();

		// Put the buttons and canvas together into the window
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(canvas, BorderLayout.CENTER);
		cp.add(gui, BorderLayout.NORTH);

		// Usual initialization
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	/**
	 * Creates a component to draw into
	 */
	private JComponent setupCanvas() {
		JComponent canvas = new JComponent() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawSketch(g);
			}
		};

		canvas.setPreferredSize(new Dimension(width, height));

		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				handlePress(event.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				handleRelease();
			}
		});

		canvas.addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent event) {
				handleDrag(event.getPoint());
			}
		});

		return canvas;
	}

	/**
	 * Creates a panel with all the buttons
	 */
	private JComponent setupGUI() {
		// Select type of shape
		String[] shapes = {"ellipse", "freehand", "rectangle", "segment"};
		JComboBox<String> shapeB = new JComboBox<String>(shapes);
		shapeB.addActionListener(e -> shapeType = (String)((JComboBox<String>)e.getSource()).getSelectedItem());

		// Select drawing/recoloring color
		// Following Oracle example
		JButton chooseColorB = new JButton("choose color");
		JColorChooser colorChooser = new JColorChooser();
		JLabel colorL = new JLabel();
		colorL.setBackground(Color.black);
		colorL.setOpaque(true);
		colorL.setBorder(BorderFactory.createLineBorder(Color.black));
		colorL.setPreferredSize(new Dimension(25, 25));
		JDialog colorDialog = JColorChooser.createDialog(chooseColorB,
				"Pick a Color",
				true,  //modal
				colorChooser,
				e -> { color = colorChooser.getColor(); colorL.setBackground(color); },  // OK button
				null); // no CANCEL button handler
		chooseColorB.addActionListener(e -> colorDialog.setVisible(true));

		// Mode: draw, move, recolor, or delete
		JRadioButton drawB = new JRadioButton("draw");
		drawB.addActionListener(e -> mode = Mode.DRAW);
		drawB.setSelected(true);
		JRadioButton moveB = new JRadioButton("move");
		moveB.addActionListener(e -> mode = Mode.MOVE);
		JRadioButton recolorB = new JRadioButton("recolor");
		recolorB.addActionListener(e -> mode = Mode.RECOLOR);
		JRadioButton deleteB = new JRadioButton("delete");
		deleteB.addActionListener(e -> mode = Mode.DELETE);
		ButtonGroup modes = new ButtonGroup(); // make them act as radios -- only one selected
		modes.add(drawB);
		modes.add(moveB);
		modes.add(recolorB);
		modes.add(deleteB);
		JPanel modesP = new JPanel(new GridLayout(1, 0)); // group them on the GUI
		modesP.add(drawB);
		modesP.add(moveB);
		modesP.add(recolorB);
		modesP.add(deleteB);

		// Put all the stuff into a panel
		JComponent gui = new JPanel();
		gui.setLayout(new FlowLayout());
		gui.add(shapeB);
		gui.add(chooseColorB);
		gui.add(colorL);
		gui.add(modesP);
		return gui;
	}

	/**
	 * Getter for the sketch instance variable
	 */
	public Sketch getSketch() {
		return sketch;
	}


	/**
	 * Draws all the shapes in the sketch,
	 * along with the object currently being drawn in this editor (not yet part of the sketch)
	 */
	public void drawSketch(Graphics g) {
		// TODO: YOUR CODE HERE
		sketch.draw(g); //draw the sketch
		if (curr != null) curr.draw(g); //if curr is not null, then also draw curr shape
		repaint(); //repaint
	}


	/**
	 * Helper method for press at point
	 * In drawing mode, start a new object;
	 * in moving mode, (request to) start dragging if clicked in a shape;
	 * in recoloring mode, (request to) change clicked shape's color
	 * in deleting mode, (request to) delete clicked shape
	 */
	private void handlePress(Point p) {

		if (mode == Mode.DRAW) //if draw mode
		{
			drawFrom = p; //set the drawing point
			repaint();
		}
		else if (mode == Mode.MOVE && sketch.contains(p) != null) //if move mode and a shape is clicked on
		{
			movingID = sketch.getContainingShape(p); //set movingID to the shape
			curr = sketch.getShapes(movingID); //sur curr to shape
			moveFrom = p; //set moveFrom point to p
		}
		else if (mode == Mode.RECOLOR && sketch.contains(p) != null) //if recolor mode and shape is clicked
		{
			movingID = sketch.getContainingShape(p); //set moving ID to that shape
			comm.requestColor(movingID, color); //request color
		}
		else if (mode == Mode.DELETE && sketch.contains(p) != null) //if delete mode and contains shape
		{
			movingID = sketch.getContainingShape(p); //set movingID to that shape
			curr = sketch.getShapes(movingID); //set curr to that shape
			comm.requestDelete(sketch.getContainingShape(p)); //request to delete the shape
		}
		repaint(); //repaint
	}



	/**
	 * Helper method for drag to new point
	 * In drawing mode, update the other corner of the object;
	 * in moving mode, (request to) drag the object
	 */
	private void handleDrag(Point p) {
		if (mode == Mode.DRAW) //if draw mode
		{
			drawDrag(p); //call helper method

		}
		else if (mode == Mode.MOVE && sketch.contains(p) != null) //if move mode and shape not null
		{
			if (curr != null) //if curr is not null
			{
				comm.requestMove(movingID,  p.x - moveFrom.x, p.y - moveFrom.y); //request to move
				moveFrom = p; //set moveFrom point to p
			}
			repaint(); //repaint
		}
	}

	/**
	 * Handle drag helper method to draw the shape while dragging
	 * @param p
	 */
	private void drawDrag(Point p)
	{
		if (drawFrom != null) //if drawFrom point is notnull
		{
			if (shapeType.equals("ellipse")) //if shape is ellipse
			{
				curr = new Ellipse(drawFrom.x, drawFrom.y, p.x, p.y, color); //draw ellipse
			}
			else if (shapeType.equals("rectangle")) //if shape is rectangle
			{
				curr = new Rectangle(drawFrom.x, drawFrom.y, p.x, p.y, color); //draw rectangle
			}
			else if (shapeType.equals("freehand")) //if shape is freehand
			{
				if (polyline == null) //if polyline ArrayList is null
				{
					polyline = new ArrayList<>(); //initialize it
					curr = new Polyline(polyline, color); //set curr to a new Polyline, passing the list of segments
				}
				polyline.add(new Segment(drawFrom.x, drawFrom.y, p.x, p.y, color)); //add to the list a new Segment
				drawFrom = p; //set drawFrom point to p

			}
			else if (shapeType.equals("segment")) //if shape is segment
			{
				curr = new Segment(drawFrom.x, drawFrom.y, p.x, p.y, color); //draw new segment
			}
		}
		repaint(); //repaint
	}


	/**
	 * Helper method for release
	 * In drawing mode, pass the add new object request on to the server;
	 * in moving mode, release it
	 */
	private void handleRelease() {

		if (mode == Mode.DRAW) //if draw mode
		{
			comm.requestDraw(curr); //request to draw
		}
		//set everything to null to release it
		polyline = null;
		curr = null;
		moveFrom = null;
		drawFrom = null;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Editor();
			}
		});
	}
}

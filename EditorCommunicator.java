import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Handles communication to/from the server for the editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 *
 * @author Sajjad
 * @author Josue
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected Editor editor;		// handling communication for


	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
	try
		{
			// Handle messages
			// TODO: YOUR CODE HERE
			String line; // The message from the client

			while((line = in.readLine())!= null) //while there is a line to read
			{
				Message message = new Message(line, editor.getSketch()); //instantiate message object
				message.handleMessage(line, editor.getSketch()); //call handleMessage method
				editor.repaint(); //repaint the editor
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("server hung up");
		}
	}

	/**
	 * Method to request color - takes shape ID and color and sends message request to server
	 * @param id
	 * @param color
	 */
	public synchronized void requestColor(Integer id, Color color)
	{
		send("recolor " + id + " " + color.getRGB());
	}

	/**
	 * Method to request delete - takes shape ID and sends message request to server to delete that ID
	 * @param id
	 */
	public synchronized void requestDelete(Integer id) { send("delete " + id); }

	/**
	 * Method to request move - takes shape ID, dx, dy, and sends message request to server
	 * @param id
	 * @param dx
	 * @param dy
	 */
	public synchronized void requestMove(Integer id, Integer dx, Integer dy) { send("move " + id + " " + dx + " " + dy); }

	/**
	 * Method to requestDraw - takes shape, and sends the shapes toString as request to server
	 * @param shape
	 */
	public synchronized void requestDraw(Shape shape)
	{
		if (shape != null) send("draw " + shape.toString());
	}


	
}

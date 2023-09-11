import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 *
 * @author Sajjad
 * @author Josue
 */
public class Polyline implements Shape {
	private ArrayList<Segment> segments; //list of segments
	private Color color;

	public Polyline(ArrayList<Segment> segments, Color color) {
		this.segments = segments;
		this.color = color;
	}


	@Override
	public void moveBy(int dx, int dy)
	{
		segments.forEach((s) -> s.moveBy(dx, dy)); //for each segment, move it by dx, dy
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color)
	{
		segments.forEach((s) -> s.setColor(color)); //for each segment, set its color
	}

	@Override
	public boolean contains(int x, int y)
	{
		return segments.stream().anyMatch((s) -> s.contains(x, y));// return true if any segment contains the point
	}

	@Override
	public void draw(Graphics g)
	{
		segments.forEach((s) -> s.draw(g)); //for each segment, draw the segment
	}

	@Override
	public String toString() {
		String res = "freehand";
		for (Segment segment : segments)
		{
			res += " " + segment.getPoints(); //add every segment points to result
		}
		res += " " + color.getRGB(); //add color to the very end
		return res;
	}
}

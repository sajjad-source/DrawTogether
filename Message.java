import java.awt.*;
import java.util.ArrayList;

/**
 * Message handler class that takes in a message request and parses it
 * @author Sajjad
 * @author Josue
 */
public class Message
{
    String message; //message being received
    Sketch sketch; //sketch of the server


    public Message(String message, Sketch sketch)
    {
        this.message = message;
        this.sketch = sketch;
    }

    /**
     * Method that handles messages by parsing and reading which request was made
     * @param message
     * @param sketch
     */
    public void handleMessage(String message, Sketch sketch)
    {
        String[] messageArray = message.split(" "); //message array after parsing it
        String mode = messageArray[0]; //mode will be the first item in the array
        Shape curr = null; //set curr shape to null

         if (mode.equals("recolor")) //if recolor mode
        {
            Integer id = Integer.parseInt(messageArray[1]); //get the id
            Integer color = Integer.parseInt(messageArray[2]); //get the recolor
            sketch.setColor(id, new Color(color)); //recolor the shape in sketch
        } 
        else if (mode.equals("delete")) //if delete mode
        {
            Integer id = Integer.parseInt(messageArray[1]); //get the id
            sketch.removeShape(id); //delete the shape
        }
        else if (mode.equals("move")) //if move mode
        {
            Integer id = Integer.parseInt(messageArray[1]); //get the id
            Integer dx = Integer.parseInt(messageArray[2]); //get dx
            Integer dy = Integer.parseInt(messageArray[3]); //get dy
            Point point = new Point(dx, dy); //create a point with dx dy
            sketch.moveShape(id, point); //move the shape by that point
        }
        else if (mode.equals("update")) //if update mode (new client joined)
        {
            int id = Integer.parseInt(messageArray[1]); //get the id
            String shapeType = messageArray[2]; //get the shape type
            sketch.setID(Integer.parseInt(messageArray[messageArray.length - 1])); //set the sketch id to the sketch server ID to update it

            if (shapeType.equals("ellipse")) //if shape is ellipse
            {
                int x1 = Integer.parseInt(messageArray[3]);
                int y1 = Integer.parseInt(messageArray[4]);
                int x2 = Integer.parseInt(messageArray[5]);
                int y2 = Integer.parseInt(messageArray[6]);
                Color color = new Color(Integer.parseInt(messageArray[7]));

                curr = new Ellipse(x1, y1 ,x2, y2, color); //set curr to a new ellipse
            }
            else if (shapeType.equals("rectangle")) //if shape is rectangle
            {
                int x1 = Integer.parseInt(messageArray[3]);
                int y1 = Integer.parseInt(messageArray[4]);
                int x2 = Integer.parseInt(messageArray[5]);
                int y2 = Integer.parseInt(messageArray[6]);
                Color color = new Color(Integer.parseInt(messageArray[7]));
                curr = new Rectangle(x1, y1, x2, y2, color); //set curr to a new rectangle
            }
            else if (shapeType.equals("freehand")) //if shape is freehand
            {
                ArrayList<Segment> polyline = new ArrayList<>(); //create a new list of segments
                Color color = new Color(Integer.parseInt(messageArray[messageArray.length - 2])); //get the color
                for (int i = 3; i < messageArray.length-2; i+=4) //for every point incrementing by 4 (x1, y1, x2, y2)
                {
                    //create point 1 with first 2 coordinates
                    Point point1 = new Point(Integer.parseInt(messageArray[i]), Integer.parseInt(messageArray[i+1]));
                    //create point 2 with next 2 coordinates
                    Point point2 = new Point(Integer.parseInt(messageArray[i+2]), Integer.parseInt(messageArray[i+3]));
                    //add to list of segments a new segment created with those new points
                    polyline.add(new Segment(point1.x, point1.y, point2.x, point2.y, color));
                }
                curr = new Polyline(polyline, color); //set curr to the new polyline made
            }
            else if (shapeType.equals("segment")) //if shape is segment
            {
                Integer x1 = Integer.parseInt(messageArray[3]);
                Integer y1 = Integer.parseInt(messageArray[4]);
                Integer x2 = Integer.parseInt(messageArray[5]);
                Integer y2 = Integer.parseInt(messageArray[6]);
                Color color = new Color(Integer.parseInt(messageArray[7]));
                curr = new Segment(x1, y1, x2, y2, color); //set curr to a new segment
            }
            if (curr != null) //if curr has been assigned
            {
                sketch.addShape(id, curr); //add curr to shape alongside it's id
            }

         }
        else if (mode.equals("draw")) //if draw mode
        {
            String shapeType = messageArray[1]; //shape type
            if (shapeType.equals("ellipse")) //if shape is ellipse
            {
                int x1 = Integer.parseInt(messageArray[2]);
                int y1 = Integer.parseInt(messageArray[3]);
                int x2 = Integer.parseInt(messageArray[4]);
                int y2 = Integer.parseInt(messageArray[5]);
                Color color = new Color(Integer.parseInt(messageArray[6]));

                curr = new Ellipse(x1, y1 ,x2, y2, color); //set curr to new ellipse
            }
            else if (shapeType.equals("rectangle")) //if rectangle
            {
                int x1 = Integer.parseInt(messageArray[2]);
                int y1 = Integer.parseInt(messageArray[3]);
                int x2 = Integer.parseInt(messageArray[4]);
                int y2 = Integer.parseInt(messageArray[5]);
                Color color = new Color(Integer.parseInt(messageArray[6]));
                curr = new Rectangle(x1, y1, x2, y2, color); //set curr to a new rectangle
            }
            else if (shapeType.equals("freehand")) //if freehand
            {
                ArrayList<Segment> polyline = new ArrayList<>(); //initialize new list of segments
                Color color = new Color(Integer.parseInt(messageArray[messageArray.length - 1]));
                for (int i = 2; i < messageArray.length-1; i+=4) //for every point incrementing by 4 (x1, y1, x2, y2)
                {
                    //create point 1 (x1,y1)
                    Point point1 = new Point(Integer.parseInt(messageArray[i]), Integer.parseInt(messageArray[i+1]));
                    //create point 2 (x2,y2)
                    Point point2 = new Point(Integer.parseInt(messageArray[i+2]), Integer.parseInt(messageArray[i+3]));
                    //create segment and add it to polyline list
                    polyline.add(new Segment(point1.x, point1.y, point2.x, point2.y, color));
                }
                curr = new Polyline(polyline, color); //set curr to a new polyline
            }
            else if (shapeType.equals("segment")) //if shape is segment
            {
                Integer x1 = Integer.parseInt(messageArray[2]);
                Integer y1 = Integer.parseInt(messageArray[3]);
                Integer x2 = Integer.parseInt(messageArray[4]);
                Integer y2 = Integer.parseInt(messageArray[5]);

                Color color = new Color(Integer.parseInt(messageArray[6]));

                curr = new Segment(x1, y1, x2, y2, color); //set curr to a new segment
            }
            if (curr != null) //if curr is not null
            {
                sketch.addShape(curr); //add curr to sketch
            }
        }
    }

}

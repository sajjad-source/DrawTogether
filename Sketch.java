import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

/**
 * @author Sajjad
 * @author Josue
 */
public class Sketch
{
    public int id; //id of shape
    private TreeMap <Integer, Shape> shapes = new TreeMap<>(); //Map K -> ID V -> Shape

    /**
     * Returns shape given an ID
     * @param id
     * @return Shape
     */
    public Shape getShapes(int id)
    {
        return shapes.get(id);
    }

    /**
     * Return shapes map
     * @return TreeMap of shapes
     */
    public TreeMap <Integer, Shape> getMap()
    {
        return shapes;
    }

    /**
     * method to getID
     * @return id
     */
    public int getID()
    {
        return id;
    }

    /**
     * method to setID
     * @param id
     */
    public void setID(int id)
    {
        this.id = id;
    }

    /**
     * Method that adds a shape to shapes map
     * @param shape
     * @return - id of shape added
     */
    public synchronized int addShape(Shape shape)
    {
        if (shapes.isEmpty()) //if map is empty
        {
            id = 1; //first id will be 1
        } else { //else if not empty
            id = Collections.max(shapes.keySet()) + 1; //the id will be the current highest id + 1
        }
        shapes.put(id, shape); //put the id and shape in map
        return id; //return id
    }

    /**
     * Method to add shape to map given a shape and ID
     * @param id
     * @param shape
     */
    public synchronized void addShape(int id, Shape shape)
    {
        shapes.put(id, shape); //put the ID -> shape in map
    }

    /**
     * Method to remove shape given ID
     * @param id
     */
    public synchronized void removeShape(int id)
    {
        shapes.remove(id); //remove ID from map
    }

    /**
     * Method to draw shapes in sketch
     * @param g
     */
    public synchronized void draw(Graphics g)
    {
        for(Map.Entry<Integer, Shape> entry : shapes.entrySet()) //loop through map
        {
            Shape shape = entry.getValue(); //get the shape
            shape.draw(g); //draw the shape
        }
    }

    /**
     * Method to move shape
     * @param id
     * @param delta
     */
    public synchronized void moveShape(int id, Point delta)
    {
        Shape shape = shapes.get(id); //get the shape from given ID
        if (shape != null)  //if shape is not null
        {
            shape.moveBy(delta.x, delta.y); //move the shape by dx and dy
        }
    }

    /**
     * Method to set a shape to a color given ID
     * @param id
     * @param color
     */
    public synchronized void setColor(int id, Color color)
    {
        Shape shape = shapes.get(id); //get the shape from given ID
        if (shape != null) //if shape not null
        {
            shape.setColor(color); //set the color of the shape
        }
    }

    /**
     * Method to return ID of a shape containing the point clicked
     * @param p
     * @return - id of shape clicked
     */
    public synchronized int getContainingShape(Point p)
    {

        for (Integer id : shapes.navigableKeySet()) //loop through every ID in TreeMap
        {
            Shape shape = shapes.get(id); //get the shape
            if (shape.contains(p.x, p.y)) //if shape contains the point clicked
            {
                return id; //return id
            }
        }
        return - 1; //else return -1
    }

    /**
     * Method that returns shape of point clicked
     * @param p
     * @return - shape
     */
    public synchronized Shape contains(Point p)
    {

        for(Integer id : shapes.navigableKeySet()) //loop through TreeMap
        {
            if(shapes.get(id).contains(p.x, p.y)) //if shape contains the point
            {
                return shapes.get(id); //return the shape
            }
        }
        return null; //else return null
    }

    /**
     * toString method
     * @return - returns a string of sketch class
     */
    public String toString()
    {
       String res = "sketch ";
       for (Shape shape : shapes.values())
       {
           String[] shapeString = new String[]{shape.toString()};
           res += shapeString + ",";
       }
       return res;
    }
}

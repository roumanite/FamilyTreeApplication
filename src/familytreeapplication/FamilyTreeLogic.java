package familytreeapplication;

import person_data.Person;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The logic of turning a root Person into a file and loading that file
 * 
 * Title    : ICT373 Assignment 2 - Family Tree Application
 * Author   : Madyarini Grace Ariel
 * Date     : 3/7/2019
 * Filename : FamilyTreeLogic.java
 * Purpose  : Handles the logic to load and save serializable objects into binary file
 * 
 * @author madya
 */
public class FamilyTreeLogic implements Serializable
{
    private Person root = null;
    private static final long serialVersionUID = -3399989652016259821L;
    
    /**
     * Loading file that contains serialized object (binary file)
     * @param file
     * @return 0 - success, 1 - fail
     */
    public int loadFile(File file)
    {
        try 
        {
            ObjectInputStream src = new ObjectInputStream(new FileInputStream(file));
            root = (Person)src.readObject();
            return 0;
        } 
        catch (FileNotFoundException ex) 
        {
            System.out.println("A file related error has occurred while loading file: " + ex);
            return 1;
        } 
        catch (IOException | ClassNotFoundException ex) 
        {
            System.out.println("A file related error has occurred while loading file: " + ex);
            return 1;
        }
    }
    
    /**
     * Saving serialized object into binary file
     * @param file
     * @return 0 - success, 1 - fail
     */
    public int saveFile(File file)
    {
        try 
        {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(root);
            return 0;
        } 
        catch (FileNotFoundException ex) 
        {
            System.out.println("A file related error has occurred while saving file: " + ex);
            return 1;
        } 
        catch (IOException ex) 
        {
            System.out.println("A file related error has occurred while saving file: " + ex);
            return 1;
        }
    }
    
    /**
     * Set root person
     * @param root 
     */
    public void setRoot(Person root)
    {
        this.root = root;
    }
    
    /**
     * Get root person
     * @return root
     */
    public Person getRoot()
    {
        return root;
    }
}

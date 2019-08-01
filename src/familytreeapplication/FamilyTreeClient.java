package familytreeapplication;

import static javafx.application.Application.launch;

/**
 * The client of the GUI application
 * 
 * Title    : ICT373 Assignment 2 - Family Tree Application
 * Author   : Madyarini Grace Ariel
 * Date     : 27/07/2019
 * Filename : FamilyTreeClient.java
 * Purpose  : Serves as client and has the main function to be executed. 
 * 
 * @author madya
 */
public class FamilyTreeClient 
{
    /**
     * Main function to be executed
     * 
     * @param args 
     */
    public static void main(String[] args)
    {
        FamilyTreeGUI ft_app = new FamilyTreeGUI();
        ft_app.run();
    }
    
}

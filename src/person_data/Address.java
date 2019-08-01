
package person_data;
import java.io.Serializable;

/**
 * The class that represents Address.
 * 
 * Title    : ICT373 Assignment 2 - Family Tree Application
 * Author   : Madyarini Grace Ariel
 * Date     : 27/07/2019
 * Filename : Address.java
 * Purpose  : Represents Address
 * 
 * @author madya
 */
public class Address implements Serializable
{
    private int stNum;
    private String stName;
    private String suburb;
    private String postcode;
    private static final long serialVersionUID = 4192153270127803323L;
    
    /**
     * The only constructor of this class
     * @param stNum - Street number
     * @param stName - Street name
     * @param suburb - Suburb name
     * @param postcode - Postal code
     */
    public Address(int stNum, String stName, String suburb, String postcode)
    {
        setStNum(stNum);
        setStName(stName);
        setSuburb(suburb);
        setPostcode(postcode);
    }
    
    /**
     * Set street number
     * @param stNum 
     */
    public void setStNum(int stNum)
    {
        this.stNum = stNum;
    }
    
    /**
     * Set street name
     * @param stName 
     */
    public void setStName(String stName)
    {
        if(stName != null)
            stName = stName.trim();
        this.stName = stName;
    }
    
    /**
     * Set suburb name
     * @param suburb 
     */
    public void setSuburb(String suburb)
    {
        if(suburb != null)
            suburb = suburb.trim();
        this.suburb = suburb;
    }
    
    /**
     * Set postcode
     * @param postcode 
     */
    public void setPostcode(String postcode)
    {
        if(postcode != null)
            postcode = postcode.trim();
        this.postcode = postcode;
    } 
    
    /**
     * Get street number
     * @return street number
     */
    public int getStNum()
    {
        return stNum;
    }
    
    /**
     * Get street name
     * @return street name
     */
    public String getStName()
    {
        return stName;
    }
    
    /**
     * Get suburb name
     * @return suburb name
     */
    public String getSuburb()
    {
        return suburb;
    }
    
    /**
     * Get postal code
     * @return postal code
     */
    public String getPostcode()
    {
        return postcode;
    }
}

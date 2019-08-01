package person_data;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class represents Person that could be part of a family tree
 * 
 * <p>
 * Title    : ICT373 Assignment 2 - Family Tree Application
 * Author   : Madyarini Grace Ariel
 * Date     : 3/7/2019
 * Filename : Person.java
 * Purpose  : Represents Person
 * 
 * @author madya
 */
public class Person implements Serializable
{
    private String id;
    private String fName;
    private String surname;
    private String maidenName;
    private char gender;
    private Address addr;
    private String desc;
    private Person father = null;
    private Person mother = null;
    private Person spouse = null;
    private ArrayList<Person> children = new ArrayList<>();
    private static final long serialVersionUID = -8532752351526717608L; 
    // if Serial Version UID keeps changing, loading old files might fail
    
    /**
     * The only constructor for this class
     * @param id - unique id
     * @param fName - first name
     * @param surname - surname/last name
     * @param maidenName - maiden name
     * @param gender - gender (M/F)
     * @param addr - Address object
     * @param desc - life description
     */
    public Person(
        String id,
        String fName, 
        String surname, 
        String maidenName, 
        char gender, 
        Address addr,
        String desc
    )
    {
        setID(id);
        setFName(fName);
        setSurname(surname);
        setMaidenName(maidenName);
        setGender(gender);
        setAddr(addr);
        setDesc(desc);
    }
    
    /**
     * Set unique ID
     * @param id 
     */
    public void setID(String id)
    {
        this.id = id;
    }
    
    /**
     * Set first name
     * @param fName 
     */
    public void setFName(String fName)
    {
        if(fName != null)
            fName = fName.trim();
        this.fName = fName;
    }
    
    /**
     * Set address object
     * @param addr 
     */
    public void setAddr(Address addr)
    {
        this.addr = addr;
    }
    
    /**
     * Set surname
     * @param surname 
     */
    public void setSurname(String surname)
    {
        if(surname != null)
            surname = surname.trim();
        this.surname = surname;
    }
    
    /**
     * Set maiden name
     * @param maidenName 
     */
    public void setMaidenName(String maidenName)
    {
        if(maidenName != null)
            maidenName = maidenName.trim();
        this.maidenName = maidenName;
    }
    
    /**
     * Set gender
     * @param gender 
     */
    public void setGender(char gender)
    {
        this.gender = Character.toUpperCase(gender); // M or F
    }
    
    /**
     * Set life description
     * @param desc 
     */
    public void setDesc(String desc)
    {
        if(desc != null)
            desc = desc.trim();
        this.desc = desc;
    }
    
    /**
     * Set father, if null means father is not specified. 
     * Add this object to the father's list of children
     * 
     * @param father 
     */
    public void setFather(Person father)
    {
        if(this.father != null) // If earlier, a father was specified
        {
            this.father.removeChild(this);
            this.father = null;
        }
        if(father != null) // If the user is not removing an existing father but setting/replacing
        {
            father.addChild(this);
            this.father = father;
            if(father.spouse == null)
                father.setSpouse(mother);
            else
            {
                mother = father.spouse;
                mother.addChild(this);
            }
                
        }
    }
    
    /**
     * Set mother, if null means mother is not specified. 
     * Add this object to the mother's list of children
     * 
     * @param mother 
     */
    public void setMother(Person mother)
    {
        if(this.mother != null) // If earlier, a mother was specified
        {
            this.mother.removeChild(this); 
            this.mother = null;
        }
        if(mother != null) // If the user is not removing an existing mother but setting/replacing
        {
            mother.addChild(this);
            this.mother = mother;
            if(mother.spouse == null)
                mother.setSpouse(father);
            else
            {
                father = mother.spouse;
                father.addChild(this);
            }
        }
    }
    
    /**
     * Set spouse, copy each other's children if not yet exist
     * @param other 
     */
    public void setSpouse(Person other)
    {
        if(this.spouse != null) // If a spouse was specified earlier
        {
            this.spouse.spouse = null; // remove this person from the spouse's spouse
            this.spouse = null; // remove the spouse from this person's spouse
        }
        if(other != null)
        {
            for(Person child : children) // loop through this person's children
            {
                if(this.gender == 'M')
                    child.mother = other; // if this person is male, set the other person as the child's mother
                else
                    child.father = other;
                other.addChild(child); // add this child to the other's children
            }
            for(Person child2 : other.children) // loop through the other's children
            {
                if(this.gender == 'M')
                    child2.father = this; // if this person is male, set this person as the father of the new child
                else
                    child2.mother = this; // if this person is female, set this person as the mother of the new child
                this.addChild(child2);
            }
            this.spouse = other;
            other.spouse = this;
        }
    }
    
    /**
     * Add child if not null and not inside the list yet
     * @param child 
     */
    private void addChild(Person child)
    {
        if(child != null && !children.contains(child))
            children.add(child);
    }
    
    /**
     * Remove child
     * @param child 
     */
    private void removeChild(Person child)
    {
        if(child != null)
        {
            children.remove(child);
            if(spouse != null)
                spouse.children.remove(child);
        }
    }
    
    /**
     * Get id
     * @return id 
     */
    public String getID()
    {
        return id;
    }
    
    /**
     * Get first name
     * @return first name
     */
    public String getFName()
    {
        return fName;
    }
    
    /**
     * Get surname
     * @return surname
     */
    public String getSurname()
    {
        return surname;
    }
    
    /**
     * Get maiden name
     * @return maiden name
     */
    public String getMaidenName()
    {
        return maidenName;
    }
    
    /**
     * Get gender
     * @return gender
     */
    public char getGender()
    {
        return gender;
    }
    
    /**
     * Get address object
     * @return addr
     */
    public Address getAddr()
    {
        return addr;
    }
    
    /**
     * Get life description
     * @return desc
     */
    public String getDesc()
    {
        return desc;
    }
    
    /**
     * Get father 
     * @return father
     */
    public Person getFather()
    {
        return father;
    }
    
    /**
     * Get mother
     * @return mother
     */
    public Person getMother()
    {
        return mother;
    }
    
    /**
     * Get spouse
     * @return spouse
     */
    public Person getSpouse()
    {
        return spouse;
    }
    
    /**
     * Get children list
     * @return children list
     */
    public ArrayList<Person> getChildren()
    {
        return new ArrayList<>(children);
    }
    
    /**
     * Override toString() method
     * @return A concatenated string containing the first name and last name of this person
     */
    @Override
    public String toString()
    {
        return fName + " " + surname;
    }
    
    /**
     * Compares ID
     * @param other
     * @return true if equal, false if not equal
     */
    public boolean equals(Person other)
    {
        if(this != null && other != null)
        {
            if(this.id.equals(other.id))
            {
                return true;
            }
        }
        return false;
    }
}

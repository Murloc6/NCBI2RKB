/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package NCBIElements;

/**
 *
 * @author murloc
 */
public class Division 
{
    private String name;
    private int id;
    
    public Division(int id, String name)
    {
        this.name = name;
        this.id = id;
    }
    
    public String toString()
    {
        return this.id+" --> "+this.name;
    }
    
    public int getId()
    {
        return this.id;
    }
    
    public String getName()
    {
        return this.name;
    }
    
}

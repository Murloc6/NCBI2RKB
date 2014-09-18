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
public class Node 
{
    private int id;
    private Names n;
    //private Division d;
    private String rank;
    private int parentId;
    
    public Node(int id, Names n, String rank, int parentId)
    {
        this.id = id;
        this.n = n;
        //this.d = d;
        this.parentId = parentId;
        this.rank = rank.replaceAll(" ", "_");
    }
    
    public String getRank()
    {
        return this.rank;
    }
    
    public int getParentId()
    {
        return this.parentId;
    }
    
    /*public int getDivisionId()
    {
        return this.d.getId();
    }*/
    
    public String getLabelsTtl()
    {
        return this.n.getTtlConcat();
    }
    
}

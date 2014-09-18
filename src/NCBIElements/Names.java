/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package NCBIElements;

import java.util.ArrayList;

/**
 *
 * @author murloc
 */
public class Names 
{
    private ArrayList<String> prefLabels;
    private ArrayList<String> altLabels;
    private int id;
    
    
    public Names(int id)
    {
        this.id = id;
        this.prefLabels = new ArrayList<>();
        this.altLabels = new ArrayList<>();
    }
    
    
    private String cleanLabel(String s)
    {
        String ret = s.replaceAll("\"", "");
        ret = ret.replaceAll("\n", "");
        ret = ret.replaceAll("\t", "");
        ret = ret.replaceAll("\r", "");
        ret = ret.replaceAll("<", "");
        ret = ret.replaceAll(">", "");
        
        return ret;
    }
    
    public void addPrefLabels(String s)
    {
        String s1 = this.cleanLabel(s);
        if(!this.prefLabels.contains(s1))
        {
            this.prefLabels.add(s1);
        }
    }
    
    public void addAltLabels(String s)
    {
        String s1 = this.cleanLabel(s);
        if(!this.altLabels.contains(s1))
        {
            this.altLabels.add(s1);
        }
    }
    
    public String getTtlConcat()
    {
        //TODO seperate prefLabel and altLabel
        String ret ="";
        for(String s : this.prefLabels)
        {
            ret += "<http://ontology.irstea.fr/AgronomicTaxon#hasScientificName> \""+s+"\";";
        }
        for(String s : this.altLabels)
        {
            ret += "<http://ontology.irstea.fr/AgronomicTaxon#hasVernacularName> \""+s+"\";";
        }
        
        return ret.substring(0, ret.lastIndexOf(";"));
    }
    
}

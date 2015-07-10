/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ncbi2rkb;

import NCBIElements.Division;
import NCBIElements.Names;
import NCBIElements.Node;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author murloc
 */
public class NCBIExtractor 
{
    HashMap<Integer, Names> nodesNames;
    HashMap<Integer, Division>divisions;
    HashMap<Integer, Node> nodes;
    HashMap<String, String> uriRank;
    
    
    SparqlProxy spOut;
    
    //
    public NCBIExtractor(SparqlProxy spOut)
    {
            this.spOut = spOut;
            this.nodesNames = new HashMap<>();
            this.divisions = new HashMap<>();
            this.nodes = new HashMap<>();
            uriRank = new HashMap<>();
            uriRank.put("varietas", "http://ontology.irstea.fr/AgronomicTaxon#VarietyRank");
            uriRank.put("species", "http://ontology.irstea.fr/AgronomicTaxon#SpecyRank");
            uriRank.put("phylum", "http://ontology.irstea.fr/AgronomicTaxon#PhylumRank");
            uriRank.put("order", "http://ontology.irstea.fr/AgronomicTaxon#OrderRank");
            uriRank.put("kingdom", "http://ontology.irstea.fr/AgronomicTaxon#KingdomRank");
            uriRank.put("genus", "http://ontology.irstea.fr/AgronomicTaxon#GenusRank");
            uriRank.put("family", "http://ontology.irstea.fr/AgronomicTaxon#FamilyRank");
            uriRank.put("class", "http://ontology.irstea.fr/AgronomicTaxon#ClassRank");
    }
    
    
    public int loadDivision(String fileName)
    {
        int nbDivision = 0;
        
        Path path = Paths.get(fileName);
        try (Scanner scanner =  new Scanner(path))
        {
          while (scanner.hasNextLine())
          {
              String line = scanner.nextLine();
              String[] params = line.split("[|]");
              
              nbDivision ++;
              
              int id = Integer.parseInt(params[0].trim());
              Division d = new Division (id, params[2].trim());
              this.divisions.put(id, d);
          }      
        } 
        catch (IOException ex) 
        {
            System.out.println("Error during reading division file!");
            System.exit(0);
        }
        return nbDivision;
    }
    
    public int loadNames(String fileName)
    {
        int nbNames = 0;
        
         Path path = Paths.get(fileName);
        try (Scanner scanner =  new Scanner(path))
        {
          while (scanner.hasNextLine())
          {
              String line = scanner.nextLine();
              String[] params = line.split("[|]");
              
              
              int id = Integer.parseInt(params[0].trim());
              String label = params[1].trim();
              String type = params[3].trim();
              Names n = this.nodesNames.get(id);
              if(n == null)
              {
                  n = new Names(id);
                  this.nodesNames.put(id, n);
              }
              
              nbNames ++;
              
              if(type.equalsIgnoreCase("synonym") || type.equalsIgnoreCase("equivalent name"))
              {
                  n.addAltLabels(label);
              }
              else if(type.equalsIgnoreCase("scientific name"))
              {
                  n.addPrefLabels(label);
              }
          }      
        } 
        catch (IOException ex) 
        {
            System.out.println("Error during reading names file!");
            System.exit(0);
        }
        return nbNames;
    }
    
    public int loadNodes(String fileName)
    {
        int nbNodes = 0;
        
         Path path = Paths.get(fileName);
        try (Scanner scanner =  new Scanner(path))
        {
          while (scanner.hasNextLine())
          {
              String line = scanner.nextLine();
              String[] params = line.split("[|]");
              
              int id = Integer.parseInt(params[0].trim());
              int parentId = Integer.parseInt(params[1].trim());
              String rank = params[2].trim();
              int divisionId = Integer.parseInt(params[4].trim());
              if(divisionId == 4) // get only plants
              {
                //Division d = this.divisions.get(divisionId);
                Names n = this.nodesNames.get(id);
                Node node = new Node(id, n, rank, parentId);
                this.nodes.put(id, node);
                nbNodes++;
              }
          }      
        } 
        catch (IOException ex) 
        {
            System.out.println("Error during reading nodes file!");
            System.exit(0);
        }
        
        //System.out.println("Nb nodes loaded : "+nbNodes);
        return nbNodes;
    }
    
    
      public String getADOMTtl(String adomFileName)
    {
        
        String ret = "prefix : <http://ontology.irstea.fr/AgronomicTaxon#> \n" +
"prefix owl: <http://www.w3.org/2002/07/owl#> \n" +
"prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
"prefix xml: <http://www.w3.org/XML/1998/namespace> \n" +
"prefix xsd: <http://www.w3.org/2001/XMLSchema#> \n" +
"prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ";
        
        ret += "INSERT DATA {";
        
        try 
        {
            ret +=  IOUtils.toString( new FileInputStream(new File(adomFileName)));
            //ret = ret.replaceAll("^@.+\\.$", "");   // remove Prefix (wrong syntax for SPARQL insert query)
        } 
        catch (IOException ex) 
        {
            System.err.println("Can't read adom file!");
            System.exit(0);
        }
        
        
        ret += "}";
        return ret;
    }
    
    public void exportNodesToSpOut(String adomFileName)
    {
        System.out.println("Delete previous KB on endpoint ...");
        this.spOut.storeData(new StringBuilder("DELETE WHERE { ?a ?b ?c.}"));
        System.out.println("Endpoint empty.");
        
        
        System.out.println("Exporting ADOM ...");
        this.spOut.storeData(new StringBuilder(this.getADOMTtl(adomFileName)));
        System.out.println("ADOM exported!");
        
        //Generate the ontology before added some instances
        String uriBase = "http://ncbi.rkb.fr#";
        String ncbiUriBase = "http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?id=";  
        
        
        /*System.out.println("Start export the ontology base");
         StringBuilder updateQuery = new StringBuilder("INSERT DATA { <"+uriBase+"Taxon> rdf:type owl:Class. "+
                                                                                                                                       "<"+uriBase+"Division> rdf:type owl:Class. "+
                                                                                                                                        "<"+uriBase+"hasDivision> rdf:type owl:ObjectProperty.}");
         this.spOut.storeData(updateQuery);
         System.out.println("Ontology base exported.");*/
         
         /*System.out.println("Start export divisions");
         updateQuery = new StringBuilder("INSERT DATA { ");
         for(Division d : this.divisions.values())
         {
             updateQuery.append("<"+uriBase+"Division_"+d.getId()+"> rdf:type <"+uriBase+"Division>; rdfs:label \""+d.getName()+"\".");
         }
         updateQuery.append("}");
         if(!this.spOut.storeData(updateQuery))
         {
             System.exit(0);
         }
         System.out.println("Divisions exported");*/
         
         
         StringBuilder updateQuery = new StringBuilder("INSERT DATA {");
         int nbQuery = 0;
         int i  = 0;

         System.out.println("Start exporting "+this.nodes.size()+" nodes ...");
         for(Entry<Integer, Node> e : this.nodes.entrySet())
         {
             String currentQueryPart = "";
             
             int id = e.getKey();
             Node n = e.getValue();
             
             String uriR = uriRank.get(n.getRank());
             if(uriR == null)
             {
                 if(n.getRank().compareTo("no rank") != 0)
                 {
                    uriR = "http://www.ncbi.nlm.nih.gov/Taxonomy/"+n.getRank();
                    this.uriRank.put(n.getRank(), uriR);
                    currentQueryPart += "<"+uriR+"> rdf:type owl:Class; rdfs:subClassOf  <http://ontology.irstea.fr/AgronomicTaxon#Taxon>; rdfs:label \""+n.getRank()+"\".  ";
                 }
                 else
                 {
                     uriR = "http://ontology.irstea.fr/AgronomicTaxon#Taxon";
                 }
                 
             }
             currentQueryPart += "<"+ncbiUriBase+id+"> rdf:type <"+uriR+">;";
             
             currentQueryPart += "<http://ontology.irstea.fr/AgronomicTaxon#hasHigherRank> <"+ncbiUriBase+n.getParentId()+">; "+
                                                       //"<"+uriBase+"hasDivision> <"+uriBase+"Division_"+n.getDivisionId()+">;"+
                                                        n.getLabelsTtl()+".";
            int fullLength = updateQuery.length()+currentQueryPart.length();
            if(fullLength > 4000000) // limit for the fuseki update query length
            {
                nbQuery ++;
                updateQuery.append("}");
                boolean ret = this.spOut.storeData(updateQuery);
                System.out.println(i+" nodes treated (query n° "+nbQuery+")...");
                if(!ret) //if store query bugged
                {
                    System.exit(0);
                }
                updateQuery = new StringBuilder("INSERT DATA {"+currentQueryPart);
            }
            else
            {
                updateQuery.append(currentQueryPart);
            }
             i++;
         }
         nbQuery ++;
        updateQuery.append("}");
        boolean ret = this.spOut.storeData(updateQuery);
        System.out.println(i+" nodes treated (query n° "+nbQuery+")...");
        if(!ret) //if store query bugged
            System.exit(0);
        System.out.println("All nodes are exported!");
    }
    
    public void saveExportToFile(String fileName)
    {
        this.spOut.writeKBFile(fileName);
    }
    
}

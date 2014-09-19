/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ncbi2rkb;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author murloc
 */
public class Ncbi2RKB 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        SparqlProxy spOut = SparqlProxy.getSparqlProxy("http://amarger.murloc.fr:8080/Ncbi2RKB_out/");
        
        NCBIExtractor ne = new NCBIExtractor(spOut);
        
        /*System.out.println("Loading divisions ...");
       int nbDiv = ne.loadDivision("in/division.dmp");
        System.out.println("Divisions loaded");*/
        
        System.out.println("Loading names ...");
        int nbNames = ne.loadNames("in/names.dmp");
        System.out.println("Names loaded");
        
        System.out.println("Loading nodes...");
        int nbNodes = ne.loadNodes("in/nodes.dmp");
        System.out.println("Nodes loaded");
        
        System.out.println("Exporting to sparql output");
        ne.exportNodesToSpOut("in/agronomicTaxon.owl");
        System.out.println("Exported");
        
        
        String dateFileName = new SimpleDateFormat("dd-MM_HH-mm_").format(new Date());
        System.out.println("Exporting RKB to file : out/"+dateFileName+"_NCBI_OWL.owl");
        ne.saveExportToFile(dateFileName+"_NCBI_OWL"); 
        System.out.println("File generated");
        
        System.out.println("NCBI processed!");
        /*System.out.println("--------------------------------");
        
        System.out.println("Nb nodes loaded : "+nbNodes);
        System.out.println("Nb labesl : "+nbNames);*/
        //System.out.println("Nb Division : "+nbDiv);
    }
    
}

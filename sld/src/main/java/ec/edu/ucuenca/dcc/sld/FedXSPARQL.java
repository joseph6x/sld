/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import com.fluidops.fedx.Config;
import com.fluidops.fedx.FedXFactory;
import com.fluidops.fedx.FederationManager;
import com.fluidops.fedx.QueryManager;
import com.fluidops.fedx.exception.FedXException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.output.WriterOutputStream;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.sparqljson.SPARQLResultsJSONWriter;

/**
 *
 * @author cedia
 */
public class FedXSPARQL {

    private FedXSPARQL() {

        try {
            Config.initialize();
            FedXFactory.initializeSparqlFederation(Arrays.asList(
                    "http://data.utpl.edu.ec/serendipity/oar/sparql",
                    "http://dbpedia.org/sparql"));

        } catch (Exception ex) {
            Logger.getLogger(FedXSPARQL.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String Query(String sparql, PrintWriter out) throws Exception {
        
        ByteArrayOutputStream stb= new ByteArrayOutputStream ();
        String txtres=null;
        try {
            TupleQuery query = QueryManager.prepareTupleQuery(sparql);
            TupleQueryResult res = query.evaluate();

            OutputStream os = new WriterOutputStream(out);
            
            SPARQLResultsJSONWriter w = new SPARQLResultsJSONWriter(os);
            SPARQLResultsJSONWriter w2 = new SPARQLResultsJSONWriter(stb);
            w.startQueryResult(res.getBindingNames());
            w2.startQueryResult(res.getBindingNames());

            while (res.hasNext()) {
                BindingSet next = res.next();
                w.handleSolution(next);
                w2.handleSolution(next);
            }

            w.endQueryResult();
            w2.endQueryResult();
            
            txtres=stb.toString("UTF-8");
        } catch (Exception ex) {
            Logger.getLogger(FedXSPARQL.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

        return txtres;
    }

    public static FedXSPARQL getInstance() {
        return FedXSPARQLHolder.INSTANCE;
    }

    private static class FedXSPARQLHolder {

        private static final FedXSPARQL INSTANCE = new FedXSPARQL();
    }

    public void Kill() throws FedXException {
        FederationManager.getInstance().shutDown();
    }
}

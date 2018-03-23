/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cedia
 */
public class SPARQL {

    public List<String> SimpleQueryString(String qry, String end, String var) {
        List<String> lista = new ArrayList();
        List<RDFNode> SimpleQuery = SimpleQuery(qry, end, var);
        for (RDFNode nd : SimpleQuery) {
            String string = "";
            if (nd.isLiteral()) {
                string=nd.asLiteral().getString();
            }else{
                string=nd.asResource().getURI();
            }
            lista.add(string);
        }

        return lista;
    }

    public List<String> SimpleQueryStringLang(String qry, String end, String var) {
        List<String> lista = new ArrayList();
        List<RDFNode> SimpleQuery = SimpleQuery(qry, end, var);
        for (RDFNode nd : SimpleQuery) {
            String string = nd.asLiteral().getLanguage();
            lista.add(string);
        }

        return lista;
    }

    
    
    public boolean SimpleQueryAsk(String qry, String end) {
        String endpoint = end;
        String consulta = qry;
        Query query = QueryFactory.create(consulta);
        boolean ask=false;
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        try {
            ask = qexec.execAsk();
        } catch (Exception e) {
            ask=true;
            e.printStackTrace();
            System.out.println(qry);
            System.out.println("Verificar consulta, no existen datos para mostrar" + e);
        } finally {
            qexec.close();

        }
        return ask;
    }
    
    
    public List<RDFNode> SimpleQuery(String qry, String end, String var) {
        List<RDFNode> lista = new ArrayList();
        String endpoint = end;
        String consulta = qry;
        Query query = QueryFactory.create(consulta);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        try {
            ResultSet rs = qexec.execSelect();
            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                lista.add(soln.get(var));
            }
            return lista;
        } catch (Exception e) {
            System.out.println(qry);
            e.printStackTrace();
            System.out.println("Verificar consulta, no existen datos para mostrar" + e);
        } finally {
            qexec.close();

        }
        return lista;
    }

}

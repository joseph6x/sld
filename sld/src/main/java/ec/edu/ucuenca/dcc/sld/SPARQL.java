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

    public static synchronized List<RDFNode> SimpleQuery(String qry, String end, String var) {
        List<RDFNode> lista = null;
        String endpoint = end;
        String consulta = qry;
        Query query = QueryFactory.create(consulta);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        try {
            ResultSet rs = qexec.execSelect();
            lista = new ArrayList();
            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                lista.add(soln.get(var));
            }
        } catch (Exception e) {
            //System.out.println("Verificar consulta, no existen datos para mostrar" + e);
        } finally {
            qexec.close();

        }
        return lista;
    }
    public static synchronized List<List<RDFNode>> SimpleDoubleQuery(String qry, String end, String var1, String var2) {
        List<RDFNode> lista1 = null;
        List<RDFNode> lista2 = null;
        String endpoint = end;
        String consulta = qry;
        Query query = QueryFactory.create(consulta);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
        try {
            ResultSet rs = qexec.execSelect();
            lista1 = new ArrayList();
            lista2 = new ArrayList();
            while (rs.hasNext()) {
                QuerySolution soln = rs.nextSolution();
                lista1.add(soln.get(var1));
                lista2.add(soln.get(var2));
            }
        } catch (Exception e) {
            //System.out.println("Verificar consulta, no existen datos para mostrar" + e);
        } finally {
            qexec.close();

        }
        List<List<RDFNode>> lista = new ArrayList();
        lista.add(lista1);
        lista.add(lista2);
        
        
        return lista;
    }

}
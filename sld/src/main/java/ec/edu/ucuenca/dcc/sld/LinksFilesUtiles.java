/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;

/**
 *
 * @author cedia
 */
public class LinksFilesUtiles {

    private static String[] cmdls = new String[]{"cat * | grep '^<???>' | cut -d' ' -f3 | cut -d'<' -f2 | cut -d'>' -f1 | uniq",
        "cat * | grep '^<[^ ]*[_/#]???> ' | cut -d' ' -f3 | cut -d'<' -f2 | cut -d'>' -f1 | uniq"};

    public static List<String> getLinks(int type, String s) {
        String value = ConfigInfo.getInstance().getConfig().get("Output").getAsString().value();
        String cmd2 = cmdls[type].replaceAll("\\?\\?\\?", s);

        String[] cmd = {
            "/bin/sh",
            "-c",
            cmd2
        };
        String executeCommand = executeCommand(cmd, value);
        String[] array = executeCommand.split("\n");
        String[] trimmedArray = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            trimmedArray[i] = array[i].trim();
        }
        List<String> list = new ArrayList<String>(Arrays.asList(trimmedArray));
        list.removeAll(Arrays.asList("", null));
        return list;
    }

    private static String executeCommand(String[] command, String path) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            File f = new File(path);
            p = Runtime.getRuntime().exec(command, null, f);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output.toString();
    }

    public static String getTitle(String uri) {
        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();
        for (int i = 0; i < get.size(); i++) {
            try {
                JsonObject get1 = get.get(i).getAsObject();
                String value = get1.get("Endpoint").getAsString().value();

                SPARQL s = new SPARQL();
                List<RDFNode> SimpleQuery = s.SimpleQuery("select ?h { {<" + uri + "> <http://purl.org/dc/terms/title> ?h .} union { <" + uri + "> <http://www.w3.org/2000/01/rdf-schema#label> ?h . } } limit 1", value, "h");
                RDFNode get2 = SimpleQuery.get(0);
                Literal asLiteral = get2.asLiteral();
                String title= asLiteral.getString();

                
                if (SimpleQuery != null && !SimpleQuery.isEmpty()) {
                    //return SimpleQuery.get(0).toString();
                    return title;
                    
                }
            } catch (Exception ex) {
                ex.printStackTrace(new PrintStream(System.out));
            }
        }

        
        String r=uri;
        
        return r;
    }
    
    public static String getLang(String uri) {
        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();
        for (int i = 0; i < get.size(); i++) {
            try {
                JsonObject get1 = get.get(i).getAsObject();
                String value = get1.get("Endpoint").getAsString().value();

                SPARQL s = new SPARQL();
                List<RDFNode> SimpleQuery = s.SimpleQuery("select ?h { {<" + uri + "> <http://purl.org/dc/terms/title> ?h .} union { <" + uri + "> <http://www.w3.org/2000/01/rdf-schema#label> ?h . } } limit 1", value, "h");
                RDFNode get2 = SimpleQuery.get(0);
                Literal asLiteral = get2.asLiteral();
                String titleLan= asLiteral.getLanguage();

                
                if (SimpleQuery != null && !SimpleQuery.isEmpty()) {
                    //return SimpleQuery.get(0).toString();
                    return titleLan;
                    
                }
            } catch (Exception ex) {
                ex.printStackTrace(new PrintStream(System.out));
            }
        }

        
        String r=uri;
        
        return r;
    }
    
    
    
    
    public static String getHandle(String uri) {
        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();
        for (int i = 0; i < get.size(); i++) {
            try {
                JsonObject get1 = get.get(i).getAsObject();
                String value = get1.get("Endpoint").getAsString().value();

                SPARQL s = new SPARQL();
                List<RDFNode> SimpleQuery = s.SimpleQuery("select ?h {<" + uri + "> <http://purl.org/ontology/bibo/handle> ?h . } limit 1", value, "h");
                if (SimpleQuery != null && !SimpleQuery.isEmpty()) {
                    return SimpleQuery.get(0).toString();
                }
            } catch (Exception ex) {
                ex.printStackTrace(new PrintStream(System.out));
            }
        }

        String[] dt = uri.split("_|/|#|=");
        String ID = dt[dt.length - 1];

        String r="http://interwp.cepal.org/sisgen/ConsultaIntegrada.asp?idIndicador="+ID;
        
        return r;
    }

    public static String getIcon(String uri) {
        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();
        for (int i = 0; i < get.size(); i++) {
            try {
                JsonObject get1 = get.get(i).getAsObject();
                String value = get1.get("Endpoint").getAsString().value();

                SPARQL s = new SPARQL();
                List<RDFNode> SimpleQuery = s.SimpleQuery("select ?h {<" + uri + "> <http://www.w3.org/1999/xhtml/vocab#icon> ?h . } limit 1", value, "h");
                if (SimpleQuery != null && !SimpleQuery.isEmpty()) {
                    return SimpleQuery.get(0).toString();
                }
            } catch (Exception ex) {
                ex.printStackTrace(new PrintStream(System.out));
            }
        }

        
        String r=uri;
        
        return r;
    }
    
    
}

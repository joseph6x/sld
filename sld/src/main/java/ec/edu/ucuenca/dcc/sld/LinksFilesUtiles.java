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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    public static void addProperty(JSONObject obj, String Name, String uri, String Def, boolean appendId) {
        String def = Def;
        if (appendId) {
            String[] dt = uri.split("_|/|#|=");
            String ID = dt[dt.length - 1];
            def += ID;
        }

        obj.put(Name, def);
    }

    public static Date tryParse(String dateString) {
        List<String> formatStrings = Arrays.asList("M/d/y", "y-M-d", "y-M", "y", "[y]");
        for (String formatString : formatStrings) {
            try {
                Date r = new SimpleDateFormat(formatString).parse(dateString);

                if (r.after(new Date(0, 0, 0))) {

                    return r;
                }

            } catch (ParseException e) {
            }
        }

        return new Date(0, 0, 0);
    }

    public static void sortDate(JSONArray arry) {

        arry.sort(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {

                JSONObject obj = (JSONObject) o1;
                String dat = (String) obj.get("Date");
                Date tryParse1 = tryParse(dat);
                obj = (JSONObject) o2;
                dat = (String) obj.get("Date");
                Date tryParse2 = tryParse(dat);

                return tryParse2.compareTo(tryParse1);
            }
        });

    }

    public static void addProperty(JSONObject obj, String Name, String repository, String uri, boolean one, boolean appendId, String defaulT, boolean lang, String... property) throws Exception {

        List<String> property1 = getProperty(uri, repository, lang, one ? 1 : 5, property);

        if (one) {
            String dat = "";
            if (property1.isEmpty()) {
                String def = defaulT;
                if (appendId) {
                    String[] dt = uri.split("_|/|#|=");
                    String ID = dt[dt.length - 1];
                    def += ID;
                }
                dat = def;
            } else {
                dat = property1.get(0);
            }
            obj.put(Name, dat);
        } else {

            if (property1.isEmpty()) {
                //ignore
            } else {

                JSONArray OneArray = new JSONArray();
                OneArray.addAll(property1);
                obj.put(Name, OneArray);
            }
        }

    }

    public static List<String> getProperty(String uri, String repository, boolean lang, int limit, String... property) throws Exception {

        List<String> lsResults = new ArrayList<>();

        String properties = "";
        for (String prs : property) {
            properties += "+" + prs;
        }

        String key = uri + "+" + repository + "+" + properties + "+" + lang + "+" + limit;

        String getCache = Cache.getInstance().get("PropertiesCache=" + key);
        if (getCache != null) {
            JsonObject parse = JSON.parse(getCache);
            JsonArray asArray = parse.get("data").getAsArray();
            for (JsonValue a : asArray) {
                lsResults.add(a.getAsString().value());
            }
        } else {
            JsonArray get = ConfigInfo.getInstance().getConfig().get("Repositories").getAsArray();
            JsonValue endpoint = null;
            for (JsonValue a : get) {
                if (a.getAsObject().get("Name").getAsString().value().compareTo(repository) == 0) {
                    endpoint = a;
                    break;
                }
            }
            if (endpoint == null) {
                throw new Exception("Repository " + repository + " not found");
            } else {
                try {
                    JsonObject get1 = endpoint.getAsObject();
                    String value = get1.get("Endpoint").getAsString().value();
                    SPARQL s = new SPARQL();

                    String sparql = "select distinct ?h { ";

                    for (int ii = 0; ii < property.length; ii++) {

                        sparql += " { <" + uri + "> <" + property[ii] + "> ?h . } ";
                        if (ii != property.length - 1) {
                            sparql += " union ";
                        }
                    }

                    sparql += " } limit " + limit;

                    List<String> SimpleQuery = null;
                    if (!lang) {
                        SimpleQuery = s.SimpleQueryString(sparql, value, "h");
                    } else {
                        SimpleQuery = s.SimpleQueryStringLang(sparql, value, "h");
                    }
                    if (SimpleQuery != null && !SimpleQuery.isEmpty()) {
                        for (String oneNode : SimpleQuery) {
                            lsResults.add(oneNode);
                        }
                    }

                    JSONObject obj = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.addAll(lsResults);
                    obj.put("data", jsonArray);
                    Cache.getInstance().put("PropertiesCache=" +key, obj.toJSONString());

                } catch (Exception ex) {
                    ex.printStackTrace(new PrintStream(System.out));
                }

            }

        }

        return lsResults;
    }

    @Deprecated
    public static String getTitle(String uri) {

        String getCache = Cache.getInstance().get("Title=" + uri);
        if (getCache != null) {
            return getCache;
        }

        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();
        for (int i = 0; i < get.size(); i++) {
            try {
                JsonObject get1 = get.get(i).getAsObject();
                String value = get1.get("Endpoint").getAsString().value();

                SPARQL s = new SPARQL();
                List<RDFNode> SimpleQuery = s.SimpleQuery("select ?h { {<" + uri + "> <http://purl.org/dc/terms/title> ?h .} union { <" + uri + "> <http://www.w3.org/2000/01/rdf-schema#label> ?h . } } limit 1", value, "h");

                if (SimpleQuery != null && !SimpleQuery.isEmpty()) {
                    RDFNode get2 = SimpleQuery.get(0);
                    Literal asLiteral = get2.asLiteral();
                    String title = asLiteral.getString();
                    //return SimpleQuery.get(0).toString();
                    Cache.getInstance().put("Title=" + uri, title);
                    return title;

                }
            } catch (Exception ex) {
                ex.printStackTrace(new PrintStream(System.out));
            }
        }

        String r = uri;
        Cache.getInstance().put("Title=" + uri, r);
        return r;
    }

    @Deprecated
    public static List<String> getBibLevel(String uri) {

        List<String> results = new ArrayList<>();
        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();
        for (int i = 0; i < get.size(); i++) {
            try {
                JsonObject get1 = get.get(i).getAsObject();
                String value = get1.get("Endpoint").getAsString().value();

                SPARQL s = new SPARQL();
                List<RDFNode> SimpleQuery = s.SimpleQuery("select ?h { <" + uri + "> <http://myontology.org/bibLevel> ?h . } ", value, "h");

                if (SimpleQuery != null && !SimpleQuery.isEmpty()) {
                    for (RDFNode oneNode : SimpleQuery) {
                        String string = oneNode.asLiteral().getString();
                        results.add(string);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(new PrintStream(System.out));
            }
        }

        return results;
    }

    @Deprecated
    public static List<String> getCallNumber(String uri) {
        List<String> results = new ArrayList<>();
        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();
        for (int i = 0; i < get.size(); i++) {
            try {
                JsonObject get1 = get.get(i).getAsObject();
                String value = get1.get("Endpoint").getAsString().value();

                SPARQL s = new SPARQL();
                List<RDFNode> SimpleQuery = s.SimpleQuery("select ?h { <" + uri + "> <http://myontology.org/callNumber> ?h . } ", value, "h");

                if (SimpleQuery != null && !SimpleQuery.isEmpty()) {
                    for (RDFNode oneNode : SimpleQuery) {
                        String string = oneNode.asLiteral().getString();
                        results.add(string);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace(new PrintStream(System.out));
            }
        }

        return results;
    }

    @Deprecated
    public static String getLang(String uri) {
        String getCache = Cache.getInstance().get("Lang=" + uri);
        if (getCache != null) {
            return getCache;
        }
        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();
        for (int i = 0; i < get.size(); i++) {
            try {
                JsonObject get1 = get.get(i).getAsObject();
                String value = get1.get("Endpoint").getAsString().value();

                SPARQL s = new SPARQL();
                List<RDFNode> SimpleQuery = s.SimpleQuery("select ?h { {<" + uri + "> <http://purl.org/dc/terms/title> ?h .} union { <" + uri + "> <http://www.w3.org/2000/01/rdf-schema#label> ?h . } } limit 1", value, "h");

                if (SimpleQuery != null && !SimpleQuery.isEmpty()) {
                    RDFNode get2 = SimpleQuery.get(0);
                    Literal asLiteral = get2.asLiteral();
                    String titleLan = asLiteral.getLanguage();
                    //return SimpleQuery.get(0).toString();
                    Cache.getInstance().put("Lang=" + uri, titleLan);
                    return titleLan;

                }
            } catch (Exception ex) {
                ex.printStackTrace(new PrintStream(System.out));
            }
        }

        String r = uri;
        Cache.getInstance().put("Lang=" + uri, r);
        return r;
    }

    @Deprecated
    public static String getHandle(String uri) {
        String getCache = Cache.getInstance().get("Handle=" + uri);
        if (getCache != null) {
            return getCache;
        }
        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();
        for (int i = 0; i < get.size(); i++) {
            try {
                JsonObject get1 = get.get(i).getAsObject();
                String value = get1.get("Endpoint").getAsString().value();

                SPARQL s = new SPARQL();
                List<RDFNode> SimpleQuery = s.SimpleQuery("select ?h {<" + uri + "> <http://purl.org/ontology/bibo/handle> ?h . } limit 1", value, "h");
                if (SimpleQuery != null && !SimpleQuery.isEmpty()) {

                    String Handle = SimpleQuery.get(0).toString();
                    Cache.getInstance().put("Handle=" + uri, Handle);
                    return Handle;
                }
            } catch (Exception ex) {
                ex.printStackTrace(new PrintStream(System.out));
            }
        }

        String[] dt = uri.split("_|/|#|=");
        String ID = dt[dt.length - 1];

        String r = "http://interwp.cepal.org/sisgen/ConsultaIntegrada.asp?idIndicador=" + ID;
        Cache.getInstance().put("Handle=" + uri, r);
        return r;
    }

    @Deprecated
    public static String getIcon(String uri) {
        String getCache = Cache.getInstance().get("Icon=" + uri);
        if (getCache != null) {
            return getCache;
        }
        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray get = instance.getConfig().get("Repositories").getAsArray();
        for (int i = 0; i < get.size(); i++) {
            try {
                JsonObject get1 = get.get(i).getAsObject();
                String value = get1.get("Endpoint").getAsString().value();

                SPARQL s = new SPARQL();
                List<RDFNode> SimpleQuery = s.SimpleQuery("select ?h {<" + uri + "> <http://www.w3.org/1999/xhtml/vocab#icon> ?h . } limit 1", value, "h");
                if (SimpleQuery != null && !SimpleQuery.isEmpty()) {
                    String Icon = SimpleQuery.get(0).toString();
                    Cache.getInstance().put("Icon=" + uri, Icon);
                    return Icon;
                }
            } catch (Exception ex) {
                ex.printStackTrace(new PrintStream(System.out));
            }
        }

        String r = uri;

        Cache.getInstance().put("Icon=" + uri, r);
        return r;
    }

}

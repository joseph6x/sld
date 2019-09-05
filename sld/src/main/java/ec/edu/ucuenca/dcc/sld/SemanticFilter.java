/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

/**
 *
 * @author joe
 */
import com.google.common.collect.Lists;
import static ec.edu.ucuenca.dcc.sld.HttpUtils.Escape2;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SemanticFilter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here

        //String k = "la economía política de las drogas en la década de los noventa: una nota de síntesis , aspectos economicos , consumo , drogas de uso indebido , produccion , trafico de drogas , consumption , drug traffic , drugs of abuse , economic aspects , production , america latina y el caribe , latin america and the caribbean";
        //String k = "minería en cuba , desarrollo de los recursos minerales , estructura de organizacion , exportaciones , historia , mano de obra , mineria , exports , history , manpower , mineral resources development , mining , organizational structure , cuba , cuba";
//        String k = "las pandillas en el salvador: propuestas y desafíos para la inclusión social juvenil en contextos de violencia urbana,juventud,pandillas,zonas urbanas,violencia,aislamiento social,politica social,youth,gangs,urban areas,violence,social isolation,social policy,el salvador,el salvador,cohesión social,educación,equidad e igualdad,juventud,políticas y programas sociales,violencia social,education,equity and equality,social cohesion,social policies and programmes,social violence,youth";
//        String k1 = removeStopWords(HttpUtils.traductorYandex(k));
//        System.out.println(k1);
//        String corticalk = HttpUtils.sendPost2("text/keywords", k1);
//        System.out.println(corticalk);
//        List<String> completew = extractCompleteWords(corticalk, k1, 4);
//        System.out.println(completew.toString());
        System.out.println(filter2(URLDecoder.decode(
                "(science,+technology+and+innovation+in+the+digital+economy:+the+state+of+the+art+in+latin+america+and+the+caribbean)+++(internet)+++(sociedad+de+la+informacion)+++(desarrollo+economico)+++(ciencia+y+tecnologia)+++(innovaciones)+++(economia+basada+en+el+conocimiento)+++(recursos+humanos)+++(propiedad+intelectual)+++(medio+ambiente)+++(agricultura)+++(indicadores+de+ciencia+y+tecnologia)+++(internet)+++(information+society)+++(economic+development)+++(science+and+technology)+++(innovations)+++(knowledge-based+economy)+++(human+resources)+++(intellectual+property)+++(environment)+++(agriculture)+++(science+and+technology+indicators)+++(america+latina+y+el+caribe)+++(latin+america+and+the+caribbean)+++(agenda+2030+para+el+desarrollo+sostenible)+++(equidad+e+igualdad)+++(innovación,+ciencia+y+tecnología)+++(tecnologías+de+la+información+y+las+comunicaciones+(tic))+++(2030+agenda+for+sustainable+development)+++(equity+and+equality)+++(information+and+communications+technologies+(icts))+++(innovation,+science+and+technology)")));

    }

    public static String compare(String k) throws Exception {
        String[] split = k.split("\\|\\|\\|\\|");
        JSONArray a = new JSONArray();
        String tit = split[0].trim();

        Set<String> h = new HashSet<>();
        for (int i = 1; i < split.length; i++) {
            h.add(split[i].trim());
        }
        List<String> ls = new ArrayList<>();
        ls.addAll(h);

        Map<String, Integer> auxmap =new HashMap ();
        
        for (String ll : ls) {
            JSONArray a_ = new JSONArray();
            JSONObject o1 = new JSONObject();
            o1.put("text", tit);
            JSONObject o2 = new JSONObject();
            o2.put("text", ll);
            a_.add(o1);
            a_.add(o2);
            a.add(a_);
            auxmap.put(ll,1);
        }
        
        String resp = HttpUtils.sendPost2("compare/bulk", a.toJSONString());
        Map<String, Integer> mp = new HashMap();
        if (resp != null ) {
        String sendPost2 = "{\"data\":" + resp + "}";
        JsonObject parse = JSON.parse(sendPost2);
       

        for (int ke = 0; ke < parse.get("data").getAsArray().size(); ke++) {
            JsonValue qa = parse.get("data").getAsArray().get(ke);
            String get = ls.get(ke);
            int intValue = qa.getAsObject().get("overlappingAll").getAsNumber().value().intValue();
            mp.put(get, intValue);
        }
        mp = sortByValue(mp);
        }else {
        mp = auxmap ;
        }
        
        String query = query(mp);
        return query;
   
    }

    public static String count(String e) {

        String[] split = e.split("\\|\\|\\|\\||,|\\s");
        List<String> asList = Lists.newArrayList(split);

        asList.removeAll(Arrays.asList("", null));

        Map<String, Integer> occurrences = new HashMap<String, Integer>();

        for (String word : asList) {
            Integer oldCount = occurrences.get(word);
            if (oldCount == null) {
                oldCount = 0;
            }
            occurrences.put(word, oldCount + 1);
        }
        Map<String, Integer> sortByValue = sortByValue(occurrences);

        System.out.println(sortByValue);

        return null;
    }

    public static String query(Map<String, Integer> mp) {
        ArrayList<String> keySet = new ArrayList<>(mp.keySet());

        String qq = "(";
        int m = keySet.size() > 4 ? 4 : keySet.size();
        for (int k = 0; k < m; k++) {
            String query = keySet.get(k);
            String[] split = query.split("\\s+");
            List<String> asList = Lists.newArrayList(split);
            asList.removeAll(Arrays.asList("", null));
            qq += "(";
            for (int i = 0; i < asList.size(); i++) {
                qq += " finalText:" + asList.get(i).toLowerCase().trim() + (i == asList.size() - 1 ? " " : " AND");
            }
            qq += ") " + (k == m - 1 ? " " : " OR ");
        }
        qq += ")";
        return qq;
    }

    public static String filter2(String k) throws Exception {

        String klean = klean(k);
        String corticalk = null;
        String traductorYandex = HttpUtils.traductorYandex(klean).replaceAll("\\|\\s+\\|", "||");
        //String corticalk = HttpUtils.sendPost2("text/keywords", traductorYandex);
        /*corticalk = HttpUtils.sendPost2("text/keywords", traductorYandex);*/
        System.out.print (corticalk);
        String sendPost2;
        if (corticalk == null){
        String basic = "[\""+klean.replace(" |||| ", "\",\"")+"\"]";  
        sendPost2 = "{\"data\":" + basic + "}";
        } else {
        sendPost2 = "{\"data\":" + corticalk + "}";
        }
       /* System.out.print (sendPost2);*/
       /* String corticall = HttpUtils.sendPost2("text/keywords", traductorYandex);
        System.out.print (corticall);*/
        
        JsonObject parse = JSON.parse(sendPost2);
        JsonArray asArray = parse.get("data").getAsArray();
        List<String> da = new ArrayList<>();
        int c = 0;
        int mx = 4;
        for (JsonValue a : asArray) {
            if (c < mx && !isStopword(a.getAsString().value())) {
                da.add(a.getAsString().value());
                c++;
            }
        }
        
        String qq = "(";
        for (int e=0; e<da.size(); e++){
            qq+=" ( finalText:"+da.get(e).toLowerCase().trim() +(e==da.size()-1?")^=1.0 ":")^=1.0 OR ");
        }
        qq += ")";
        

        String q2 = compare(traductorYandex);

        
        return q2 +(da.isEmpty()?"":" OR "+qq);
        
        

//        String corticalk = HttpUtils.sendPost2("text/keywords", traductorYandex);
//        List<String> extractCompleteWords = extractCompleteWords(corticalk, traductorYandex, 4);
//
//        String[] stockArr = new String[extractCompleteWords.size()];
//        stockArr = extractCompleteWords.toArray(stockArr);
//
//        String[] split = String.join(" ", stockArr).replaceAll("\\s+", " ").trim().split(" ");
//        Set <String> l = new HashSet<String>(Arrays.asList(split));
//        l.remove("and");
//        l.remove("or");
//        l.remove("of");
//        l.remove("at");
//        l.remove("in");
//        l.remove("on");
//        l.remove("a");
//        l.remove("eclac");
//        stockArr = new String[l.size()];
//        stockArr = l.toArray(stockArr);
//        
//        return String.join(" ", stockArr);
    }

    public static String filter(String k) throws Exception {

        String klean = klean(k);
        String traductorYandex = HttpUtils.traductorYandex(klean).replaceAll("\\|\\s+\\|", "||");

        String corticalk = HttpUtils.sendPost2("text/keywords", traductorYandex);
        List<String> extractCompleteWords = extractCompleteWords(corticalk, traductorYandex, 4);

        String[] stockArr = new String[extractCompleteWords.size()];
        stockArr = extractCompleteWords.toArray(stockArr);

        String[] split = String.join(" ", stockArr).replaceAll("\\s+", " ").trim().split(" ");
        Set<String> l = new HashSet<String>(Arrays.asList(split));
        l.remove("and");
        l.remove("or");
        l.remove("of");
        l.remove("at");
        l.remove("in");
        l.remove("on");
        l.remove("a");
        l.remove("eclac");
        stockArr = new String[l.size()];
        stockArr = l.toArray(stockArr);

        return String.join(" ", stockArr);
    }

    public static String klean(String k) throws Exception {
        String[] split = k.split(" \\+ |   ");
        List<String> ls = new ArrayList<>();

        for (int i = 0; i < split.length; i++) {
            split[i] = Escape2(split[i].substring(1, split[i].length() - 1)).replaceAll("\\s+", " ").trim();
            //if (!isStopword(split[i])) {
            ls.add(split[i]);
            //}

        }
        split = new String[ls.size()];
        split = ls.toArray(split);
        String join = String.join(" |||| ", split);
        return join;
    }

    public static boolean isStopword(String d) {
        String nd = d.toLowerCase();
        ConfigInfo instance = ConfigInfo.getInstance();
        JsonArray asArray = instance.getConfig().get("Stopwords").getAsArray();
        for (JsonValue w : asArray) {
            String toLowerCase = w.getAsString().value().toLowerCase();
            boolean e = toLowerCase.contains(nd) || nd.contains(toLowerCase);
            if (e) {
                return e;
            }
        }
        return false;
    }

    public static String removeStopWords(String k) {
        List<String> sw = new ArrayList();
        sw.add("latin");
        sw.add("america");
        sw.add("caribbean");
        sw.add("cepal");
        sw.add("eclac");
        sw.add(" the ");

        List<String> sepw = new ArrayList();
        sepw.add("and");
        sepw.add("or");
        sepw.add(":");
        //   sepw.add("-");

        String pk = k.toLowerCase();
        for (String rp : sepw) {
            pk = pk.replaceAll("\\s+" + sepw + "\\s+", ",");

        }

        for (String w : sw) {
            pk = pk.replace(w, " ");

        }
        pk = pk.replaceAll(",\\s*,", "");

        return pk;
    }

    private static List<String> extractCompleteWords(String ck, String origin, int size) {
        int sizecompare = size;
        HashMap<String, String> hwords = new HashMap();
        String[] korigin = origin.split("\\|\\|");
        String[] kcortical = ck.replaceAll("[\\[\\]\"]", "").split(",");

        for (String ko : korigin) {
            for (int i = 0; i < kcortical.length; i++) {

                //System.out.println("Compara:" + ko + "-" + kcortical[i]);
                if (ko.contains(kcortical[i])) {

                    if (!(hwords.containsKey(kcortical[i]) && (hwords.get(kcortical[i]).length() < ko.length() || hwords.containsValue(ko)))) {
                        hwords.put(kcortical[i], ko);
                        //System.out.println("Contiene:" + ko + "-" + kcortical[i]);
                    }

                }

            }
        }
        List<String> l = new ArrayList();

        for (String kc : kcortical) {
            if (!l.contains(hwords.get(kc))) {
                l.add(hwords.get(kc));
            }
        }

        if (l.size() < sizecompare) {
            sizecompare = l.size();
        }

        return l.subList(0, sizecompare);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V>
            sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}

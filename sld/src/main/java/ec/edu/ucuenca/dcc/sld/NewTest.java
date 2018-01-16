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
import ec.edu.ucuenca.dcc.sld.HttpUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here

        //String k = "la economía política de las drogas en la década de los noventa: una nota de síntesis , aspectos economicos , consumo , drogas de uso indebido , produccion , trafico de drogas , consumption , drug traffic , drugs of abuse , economic aspects , production , america latina y el caribe , latin america and the caribbean";
        //String k = "minería en cuba , desarrollo de los recursos minerales , estructura de organizacion , exportaciones , historia , mano de obra , mineria , exports , history , manpower , mineral resources development , mining , organizational structure , cuba , cuba";
        String k = "las pandillas en el salvador: propuestas y desafíos para la inclusión social juvenil en contextos de violencia urbana,juventud,pandillas,zonas urbanas,violencia,aislamiento social,politica social,youth,gangs,urban areas,violence,social isolation,social policy,el salvador,el salvador,cohesión social,educación,equidad e igualdad,juventud,políticas y programas sociales,violencia social,education,equity and equality,social cohesion,social policies and programmes,social violence,youth";
        String k1 = removeStopWords(HttpUtils.traductorYandex(k));
        System.out.println(k1);
        String corticalk = HttpUtils.sendPost2("text/keywords", k1);
        System.out.println(corticalk);
        List<String> completew = extractCompleteWords(corticalk, k1, 4);
        System.out.println(completew.toString());
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
            pk = pk.replaceAll("\\s+"+sepw+"\\s+", ",");

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
        String[] korigin = origin.split(",");
        String[] kcortical = ck.replaceAll("[\\[\\]\"]", "").split(",");
       
        for (String ko : korigin) {
            for (int i = 0; i < kcortical.length - 1; i++) {

                System.out.println("Compara:" + ko + "-" + kcortical[i]);
                if (ko.contains(kcortical[i])) {

                    if (!(hwords.containsKey(kcortical[i]) && (hwords.get(kcortical[i]).length() < ko.length() || hwords.containsValue(ko)))) {
                        hwords.put(kcortical[i], ko);
                        System.out.println("Contiene:" + ko + "-" + kcortical[i]);
                    }

                }

            }
        }
         List <String> l = new ArrayList();
         
         
        for (String kc : kcortical){
             if (!l.contains(hwords.get(kc))){
                l.add(hwords.get(kc));
             }
        }
        
         if (l.size() < sizecompare) {
            sizecompare = l.size();
           }
         
        return  l.subList(0, sizecompare);
    }

}

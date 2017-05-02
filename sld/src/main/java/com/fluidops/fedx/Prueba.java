/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluidops.fedx;

import com.fluidops.fedx.exception.FedXException;
import java.util.Arrays;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;

/**
 *
 * @author cedia
 */
public class Prueba {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here

        Config.initialize();
        FedXFactory.initializeSparqlFederation(Arrays.asList(
                "http://data.utpl.edu.ec/serendipity/oar/sparql",
                "http://dbpedia.org/sparql"));

        String q = "prefix text:<http://jena.apache.org/text#>\n" +
"select *  {\n" +
"{\n" +
"service <http://201.159.222.25:8180/UG/sparql> {\n" +
"select   ?Score1 ?Endpoint ?EntityURI ?EntityClass ?EntityLabel ?Property ?PropertyLabel ?PropertyValue  (max(?Year1)as ?Year) (max(?Lang1) as ?Lang) (max(?Type1) as ?Type)  ((?Score1*if(count(?Score2)>0,2,1)*if(count(?Score3)>0,2,1)*if(count(?Score4)>0,1,1)) as ?Score ) \n" +
"{\n" +
"\n" +
"bind('UG' AS ?Endpoint) .\n" +
"bind(IRI(<http://purl.org/ontology/bibo/Document>) as ?EntityClass) .\n" +
"bind(IRI(<http://purl.org/ontology/bibo/abstract>) AS ?Property) .\n" +
"bind('Abstract' AS ?PropertyLabel) .\n" +
"\n" +
"\n" +
"(?EntityURI ?Score1 ?PropertyValue) text:query (<http://purl.org/ontology/bibo/abstract> '(cuenca)' 1000) .\n" +
"?EntityURI <http://purl.org/dc/terms/title> ?EntityLabel .\n" +
"filter(str(?PropertyValue)!='') .\n" +
"optional { (?EntityURI ?Score2 ?PropertyValue2) text:query (<http://purl.org/dc/terms/subject> '(_)' ) .  filter(str(?EntityURI)!='') .} \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/language> ?Lang1 .   } \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/language> ?Lang2 .  filter(str(?Lang2) = 'none'). bind( 1 as ?Score3  ).  } \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/issued> ?y2. bind( strbefore( ?y2, '-' ) as ?y3 ).  bind( strafter( ?y2, ' ' ) as ?y4 ). bind( if (str(?y3)='' && str(?y4)='',?y2,if(str(?y3)='',?y4,?y3)) as ?Year1 ).  }\n" +
"optional { ?EntityURI a ?Type1 . filter (str(?Type1) != 'http://xmlns.com/foaf/0.1/Agent' &&  str(?Type1) != 'http://purl.org/ontology/bibo/Document') .   } \n" +
"optional { {?EntityURI a <http://purl.org/ontology/bibo/Article> .  bind(1 as ?Score4  ). } union { ?EntityURI a <http://purl.org/net/nknouf/ns/bibtex#Mastersthesis> .  bind(1 as ?Score4  ). }  } \n" +
"} group by ?Endpoint ?EntityURI ?EntityClass ?EntityLabel ?Property ?PropertyLabel ?PropertyValue ?Score1  \n" +
"}\n" +
"}\n" +
"union {\n" +
"service <http://201.159.222.25:8180/UG/sparql> {\n" +
"select   ?Score1 ?Endpoint ?EntityURI ?EntityClass ?EntityLabel ?Property ?PropertyLabel ?PropertyValue  (max(?Year1)as ?Year) (max(?Lang1) as ?Lang) (max(?Type1) as ?Type)  ((?Score1*if(count(?Score2)>0,2,1)*if(count(?Score3)>0,2,1)*if(count(?Score4)>0,1,1)) as ?Score ) \n" +
"{\n" +
"\n" +
"bind('UG' AS ?Endpoint) .\n" +
"bind(IRI(<http://purl.org/ontology/bibo/Document>) as ?EntityClass) .\n" +
"bind(IRI(<http://purl.org/ontology/bibo/abstract>) AS ?Property) .\n" +
"bind('Abstract' AS ?PropertyLabel) .\n" +
"\n" +
"\n" +
"(?EntityURI ?Score1 ?PropertyValue) text:query (<http://purl.org/dc/terms/title> '(cuenca)' 1000) .\n" +
"?EntityURI <http://purl.org/dc/terms/title> ?EntityLabel .\n" +
"filter(str(?PropertyValue)!='') .\n" +
"optional { (?EntityURI ?Score2 ?PropertyValue2) text:query (<http://purl.org/dc/terms/subject> '(_)' ) .  filter(str(?EntityURI)!='') .} \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/language> ?Lang1 .   } \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/language> ?Lang2 .  filter(str(?Lang2) = 'none'). bind( 1 as ?Score3  ).  } \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/issued> ?y2. bind( strbefore( ?y2, '-' ) as ?y3 ).  bind( strafter( ?y2, ' ' ) as ?y4 ). bind( if (str(?y3)='' && str(?y4)='',?y2,if(str(?y3)='',?y4,?y3)) as ?Year1 ).  }\n" +
"optional { ?EntityURI a ?Type1 . filter (str(?Type1) != 'http://xmlns.com/foaf/0.1/Agent' &&  str(?Type1) != 'http://purl.org/ontology/bibo/Document') .   } \n" +
"optional { {?EntityURI a <http://purl.org/ontology/bibo/Article> .  bind(1 as ?Score4  ). } union { ?EntityURI a <http://purl.org/net/nknouf/ns/bibtex#Mastersthesis> .  bind(1 as ?Score4  ). }  } \n" +
"} group by ?Endpoint ?EntityURI ?EntityClass ?EntityLabel ?Property ?PropertyLabel ?PropertyValue ?Score1  \n" +
"}\n" +
"}\n" +
"union {\n" +
"service <http://201.159.222.25:8180/UG/sparql> {\n" +
"select   ?Score1 ?Endpoint ?EntityURI ?EntityClass ?EntityLabel ?Property ?PropertyLabel ?PropertyValue  (max(?Year1)as ?Year) (max(?Lang1) as ?Lang) (max(?Type1) as ?Type)  ((?Score1*if(count(?Score2)>0,2,1)*if(count(?Score3)>0,2,1)*if(count(?Score4)>0,1,1)) as ?Score ) \n" +
"{\n" +
"\n" +
"bind('UG' AS ?Endpoint) .\n" +
"bind(IRI(<http://purl.org/ontology/bibo/Document>) as ?EntityClass) .\n" +
"bind(IRI(<http://purl.org/ontology/bibo/abstract>) AS ?Property) .\n" +
"bind('Abstract' AS ?PropertyLabel) .\n" +
"\n" +
"(?EntityURI ?Score1 ?PropertyValue) text:query (<http://purl.org/dc/terms/subject> '(cuenca)' 1000) .\n" +
"?EntityURI <http://purl.org/dc/terms/title> ?EntityLabel .\n" +
"filter(str(?PropertyValue)!='') .\n" +
"optional { (?EntityURI ?Score2 ?PropertyValue2) text:query (<http://purl.org/dc/terms/subject> '(_)' ) .  filter(str(?EntityURI)!='') .} \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/language> ?Lang1 .   } \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/language> ?Lang2 .  filter(str(?Lang2) = 'none'). bind( 1 as ?Score3  ).  } \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/issued> ?y2. bind( strbefore( ?y2, '-' ) as ?y3 ).  bind( strafter( ?y2, ' ' ) as ?y4 ). bind( if (str(?y3)='' && str(?y4)='',?y2,if(str(?y3)='',?y4,?y3)) as ?Year1 ).  }\n" +
"optional { ?EntityURI a ?Type1 . filter (str(?Type1) != 'http://xmlns.com/foaf/0.1/Agent' &&  str(?Type1) != 'http://purl.org/ontology/bibo/Document') .   } \n" +
"optional { {?EntityURI a <http://purl.org/ontology/bibo/Article> .  bind(1 as ?Score4  ). } union { ?EntityURI a <http://purl.org/net/nknouf/ns/bibtex#Mastersthesis> .  bind(1 as ?Score4  ). }  } \n" +
"} group by ?Endpoint ?EntityURI ?EntityClass ?EntityLabel ?Property ?PropertyLabel ?PropertyValue ?Score1  \n" +
"}\n" +
"}\n" +
"union {\n" +
"service <http://201.159.222.25:8180/UG/sparql> {\n" +
"select   ?Score1 ?Endpoint ?EntityURI ?EntityClass ?EntityLabel ?Property ?PropertyLabel ?PropertyValue  (max(?Year1)as ?Year) (max(?Lang1) as ?Lang) (max(?Type1) as ?Type)  ((?Score1*if(count(?Score2)>0,2,1)*if(count(?Score3)>0,2,1)*if(count(?Score4)>0,1,1)) as ?Score ) \n" +
"{\n" +
"\n" +
"bind('UG' AS ?Endpoint) .\n" +
"bind(IRI(<http://purl.org/ontology/bibo/Document>) as ?EntityClass) .\n" +
"bind(IRI(<http://purl.org/ontology/bibo/abstract>) AS ?Property) .\n" +
"bind('Abstract' AS ?PropertyLabel) .\n" +
"\n" +
"(?EntityURI ?Score1 ?PropertyValue) text:query (<http://xmlns.com/foaf/0.1/name> '(cuenca)' 1000) .\n" +
"?EntityURI <http://xmlns.com/foaf/0.1/name> ?EntityLabel .\n" +
"filter(str(?PropertyValue)!='') .\n" +
"optional { (?EntityURI ?Score2 ?PropertyValue2) text:query (<http://purl.org/dc/terms/subject> '(_)' ) .  filter(str(?EntityURI)!='') .} \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/language> ?Lang1 .   } \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/language> ?Lang2 .  filter(str(?Lang2) = 'none'). bind( 1 as ?Score3  ).  } \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/issued> ?y2. bind( strbefore( ?y2, '-' ) as ?y3 ).  bind( strafter( ?y2, ' ' ) as ?y4 ). bind( if (str(?y3)='' && str(?y4)='',?y2,if(str(?y3)='',?y4,?y3)) as ?Year1 ).  }\n" +
"optional { ?EntityURI a ?Type1 . filter (str(?Type1) != 'http://xmlns.com/foaf/0.1/Agent' &&  str(?Type1) != 'http://purl.org/ontology/bibo/Document') .   } \n" +
"optional { {?EntityURI a <http://purl.org/ontology/bibo/Article> .  bind(1 as ?Score4  ). } union { ?EntityURI a <http://purl.org/net/nknouf/ns/bibtex#Mastersthesis> .  bind(1 as ?Score4  ). }  } \n" +
"} group by ?Endpoint ?EntityURI ?EntityClass ?EntityLabel ?Property ?PropertyLabel ?PropertyValue ?Score1  \n" +
"}\n" +
"}\n" +
"union {\n" +
"service <http://201.159.222.25:8180/UG/sparql> {\n" +
"select   ?Score1 ?Endpoint ?EntityURI ?EntityClass ?EntityLabel ?Property ?PropertyLabel ?PropertyValue  (max(?Year1)as ?Year) (max(?Lang1) as ?Lang) (max(?Type1) as ?Type)  ((?Score1*if(count(?Score2)>0,2,1)*if(count(?Score3)>0,2,1)*if(count(?Score4)>0,1,1)) as ?Score ) \n" +
"{\n" +
"\n" +
"bind('UG' AS ?Endpoint) .\n" +
"bind(IRI(<http://purl.org/ontology/bibo/Document>) as ?EntityClass) .\n" +
"bind(IRI(<http://purl.org/ontology/bibo/abstract>) AS ?Property) .\n" +
"bind('Abstract' AS ?PropertyLabel) .\n" +
"\n" +
"(?EntityURI ?Score1 ?PropertyValue) text:query (<http://purl.org/dc/terms/description> '(cuenca)' 1000) .\n" +
"?EntityURI <http://purl.org/dc/terms/description> ?EntityLabel .\n" +
"filter(str(?PropertyValue)!='') .\n" +
"optional { (?EntityURI ?Score2 ?PropertyValue2) text:query (<http://purl.org/dc/terms/subject> '(_)' ) .  filter(str(?EntityURI)!='') .} \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/language> ?Lang1 .   } \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/language> ?Lang2 .  filter(str(?Lang2) = 'none'). bind( 1 as ?Score3  ).  } \n" +
"optional { ?EntityURI <http://purl.org/dc/terms/issued> ?y2. bind( strbefore( ?y2, '-' ) as ?y3 ).  bind( strafter( ?y2, ' ' ) as ?y4 ). bind( if (str(?y3)='' && str(?y4)='',?y2,if(str(?y3)='',?y4,?y3)) as ?Year1 ).  }\n" +
"optional { ?EntityURI a ?Type1 . filter (str(?Type1) != 'http://xmlns.com/foaf/0.1/Agent' &&  str(?Type1) != 'http://purl.org/ontology/bibo/Document') .   } \n" +
"optional { {?EntityURI a <http://purl.org/ontology/bibo/Article> .  bind(1 as ?Score4  ). } union { ?EntityURI a <http://purl.org/net/nknouf/ns/bibtex#Mastersthesis> .  bind(1 as ?Score4  ). }  } \n" +
"} group by ?Endpoint ?EntityURI ?EntityClass ?EntityLabel ?Property ?PropertyLabel ?PropertyValue ?Score1  \n" +
"}\n" +
"}\n" +
" . filter(str(?EntityURI)!='') . }  order by DESC(?Score)  \n" +
"  ";

        
       // String queryPlan = QueryManager.getQueryPlan(q);
        
       // System.out.println(queryPlan);
        
        TupleQuery query = QueryManager.prepareTupleQuery(q);
        
        System.out.println(query.toString());
        
        TupleQueryResult res = query.evaluate();

        while (res.hasNext()) {
            System.out.println(res.next());
        }

        FederationManager.getInstance().shutDown();
        System.out.println("Done.");
        System.exit(0);

    }

}

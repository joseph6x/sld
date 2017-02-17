/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ucuenca.dcc.sld;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cedia
 */
public class SNER {
    
    private String[] SNER_ClassifiersList = null;
    
    private List<AbstractSequenceClassifier<CoreLabel>> InstanciedClassifiers;
    
    private SNER() {
        try {
            Init();
        } catch (Exception ex) {
            Logger.getLogger(SNER.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static SNER getInstance() {
        return SNERHolder.INSTANCE;
    }
    
    private static class SNERHolder {
        
        private static final SNER INSTANCE = new SNER();
    }
    
    private void Init() throws IOException, ClassCastException, ClassNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        String ClassifierFile1 = classLoader.getResource("classifiers/english.all.3class.distsim.crf.ser.gz").getFile();
        String ClassifierFile2 = classLoader.getResource("classifiers/spanish.ancora.distsim.s512.crf.ser.gz").getFile();
        SNER_ClassifiersList = new String[]{ClassifierFile1,ClassifierFile2};
        InstanciedClassifiers = new ArrayList();
        for (String ClassifierPath : SNER_ClassifiersList) {
            CRFClassifier<CoreLabel> classifierInstance = CRFClassifier.getClassifier(ClassifierPath);
            InstanciedClassifiers.add(classifierInstance);
        }
    }
    
    public List<String> GetNamedEntities(String InputText) {
        List<Triple<String, Integer, Integer>> Results = new ArrayList();
        //NER analyzing
        for (AbstractSequenceClassifier<CoreLabel> aClassifierInstance : InstanciedClassifiers) {
            List<Triple<String, Integer, Integer>> partialResults = aClassifierInstance.classifyToCharacterOffsets(InputText);
            Results.addAll(partialResults);
        }
        
        List<String> DetectedEntities = new ArrayList<>();
        //Filtering locations
        for (Triple<String, Integer, Integer> aPartialResult : Results) {
            
            String OriginalText = InputText.substring(aPartialResult.second(), aPartialResult.third());
            String EntityType = aPartialResult.first();
            
            //Only locations
            if (EntityType.compareTo("LOCATION") == 0) {
                DetectedEntities.add(OriginalText);
            }
            
        }
        //Remove duplicates
        Set<String> UniqueEntities = new HashSet(DetectedEntities);
        DetectedEntities = new ArrayList<>(UniqueEntities);
        
        return DetectedEntities;
    }
    
}

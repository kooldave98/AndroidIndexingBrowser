package com.indexingbrowser.core;

// A class to represent a web index of contents or keywords
// It is formed by merging contents or keywords extracted from individual web documents
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class WebIndex implements Serializable {

    private boolean keywordsIndex;
    // type of index, either "contents" or "keywords"
    private String indexType;
    private Set<WebDoc> webDocs;
    //private Set<WebDoc> matchedDocs;
    // a map with contents/keywords as key and set of WebDoc as value
    private Map<String, Set<WebDoc>> index;

    public WebIndex(boolean useKeywords) {

        keywordsIndex = useKeywords;
        if (useKeywords) {
            indexType = "keywords";
        } else {
            indexType = "contents";
        }

        webDocs = new TreeSet<WebDoc>();
        //matchedDocs = new HashSet<WebDoc>();
        index = new TreeMap<String, Set<WebDoc>>();
    }

    public void add(WebDoc doc) {

        // this can be either keywords or contents
        Set<String> words = null;

        if (keywordsIndex) {
            words = doc.getKeywords();
        } else {
            words = doc.getContents();
        }

        // add to the list of documents in the index if the contents/keywords exist in the document
        if (words.size() > 0) {
            webDocs.add(doc);
        }

        for (Iterator<String> iter = words.iterator(); iter.hasNext();) {

            String key = iter.next();

            // initialize a new set to be used below
            Set<WebDoc> keySet = new TreeSet<WebDoc>();

            // if the word is already in the index map, then get the set of docs associated with it
            if (index.containsKey(key)) {
                keySet = index.get(key);
            }

            // add this document to an existing set associated with an existing key, or
            // add this document to a new set
            keySet.add(doc);

            // put the new set as a value for this key
            // for an existing key, this will replace the original set with this modified set
            index.put(key, keySet);
        }
    }

    public Set<WebDoc> getDocuments() {

        return webDocs;
    }

    public Set<WebDoc> getMatches(String wd) {

        // returns the set associated with the key supplied as the string argument

        Set<WebDoc> get = (TreeSet<WebDoc>) index.get(wd);
        Set<WebDoc> matches = new TreeSet<WebDoc>();
        if (get != null) {
            for (WebDoc wDoc : get) {
                matches.add(wDoc);
            }
            return matches;
        } else {
            //System
            return new TreeSet<WebDoc>();
        }
    }

    @Override
    public String toString() {

        return "WebIndex " + indexType + " contains " + index.size() + " words from " + webDocs.size() + " documents";

    }
}







//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.Set;
//import java.util.TreeSet;
//
//public class WebIndex implements Serializable {
//
//    private boolean _useKeywords;
//    private Set<String> _contentsOrKeywordsIndex;
//    private ArrayList<WebDoc> _webDocuments;
//
//    //This method is the constructor that initializes the attributes of the object
//    //It is supplied a boolean which tells what is to be indexed as a parameter.
//    public WebIndex(boolean useKeywords) {
//
//        _useKeywords = useKeywords;
//        _webDocuments = new ArrayList<WebDoc>();
//        _contentsOrKeywordsIndex = new TreeSet<String>();
//    }
//
//    //This method is called to add a WebDocument into the index.
//    //It is supplied a WebDoc as a parameter.
//    public void add(WebDoc doc) {
//        _webDocuments.add(doc);
//
//        if (_useKeywords == false) {
//            _contentsOrKeywordsIndex.addAll(doc.getContents());
//        } else {
//            _contentsOrKeywordsIndex.addAll(doc.getKeywords());
//        }
//    }
//
//    //This method is called to retrieve all the Documents that have been indexed.
//    //It returns a Set of WebDocs
//    public Set<WebDoc> getDocuments() {
//
//        Set<WebDoc> webDocuments = new TreeSet<WebDoc>();
//        webDocuments.addAll(_webDocuments);
//
//        return webDocuments;
//    }
//
//    //This method is called to search through the WebIndex for a String
//    //It returns a LinkedHashSet of WebDocs that contain one or more Strings that match.
//    //It is supplied a word as a String.
//    public Set<WebDoc> getMatches(String wd) {
//
//        Set<WebDoc> matches = new TreeSet<WebDoc>();
//
//        for (int i = 0; i < _webDocuments.size(); i++) {
//
//            if (_useKeywords == false) {
//                for (String str : _webDocuments.get(i).getContents()) {
//                    if (str.contains(wd)) {
//                        matches.add(_webDocuments.get(i));
//                    }
//                }
////                Iterator itr = _webDocuments.get(i).getContents().iterator();
////                while (itr.hasNext()) {
////                    if (itr.next().toString().contains(wd)) {
////                        //signal found and document associated webdoc
////                        matches.add(_webDocuments.get(i));
////                    }
////                }
//            } else {
//                for (String str : _webDocuments.get(i).getKeywords()) {
//                    if (str.contains(wd)) {
//                        matches.add(_webDocuments.get(i));
//                    }
//                }
//            }
//        }
//
//        return matches;
//    }
//
//    //This method is called to return a detailed String-Like representation of the the WebIndex object.
//    //It is not supplied any parameters. It overrides the toString method of its Object SuperClass.
//    @Override
//    public String toString() {
//
//        int indexedWordsCount = _contentsOrKeywordsIndex.size();
//        int indexedDocsCount = _webDocuments.size();
//
//        String response;
//
//        if (_useKeywords == false) {
//            response = "WebIndex CONTENTS contains " + indexedWordsCount + " words from " + indexedDocsCount + " documents";
//        } else {
//            response = "WebIndex KEYWORDS contains " + indexedWordsCount + " words from " + indexedDocsCount + " documents";
//        }
//
//        return response;
//    }
//}

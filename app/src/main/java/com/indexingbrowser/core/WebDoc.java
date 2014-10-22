package com.indexingbrowser.core;
// A class to represent the content words and keywords of a single HTML document
import java.io.*;
import java.net.*;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebDoc implements Serializable, Comparable<WebDoc> {

    // Instance variables
    private URL docURL;
    private Set<String> contents;
    private Set<String> keywords;
    // sizes of the contents and keywords sets
    int numContents;
    int numKeywords;
    // first and last elements of the contents set
    String firstContentWord;
    String lastContentWord;
    // defines whether the document is well-formed, partly well-formed or ill-formed
    String formString;

    // Constructor
    public WebDoc(URL url) {

        docURL = url;
        contents = new TreeSet<String>();
        keywords = new TreeSet<String>();
        numContents = 0;
        numKeywords = 0;
        firstContentWord = null;
        lastContentWord = null;

        // call to a method which constructs the contents and keywords sets
        processURL();

        // call to a method to determine if the document is well formed or not
        checkForm();
    }

    // returns the set of contents
    public Set<String> getContents() {

        return contents;
    }

    // returns the set of keywords
    public Set<String> getKeywords() {

        return keywords;
    }

    // returns the document URL
    public URL getURL() throws Exception {

        return docURL;
    }

    // returns the summary line in the required format
    @Override
    public String toString() {

        return docURL + " " + numContents + " (" + firstContentWord + " - " + lastContentWord + ") " + numKeywords + " " + formString;
    }

    // method to construct the contents and keywords sets
    private void processURL() {

        try {
            // open the document for reading
            BufferedReader doc = new BufferedReader(new InputStreamReader(docURL.openStream()));

            // read each line of the document
            String docLine;
            while ((docLine = doc.readLine()) != null) {
                // build the set of keywords
                buildKeywords(docLine);

                // build the set of contents
                buildContents(docLine);
            }

            // close the document to free up the resource
            doc.close();

        } catch (IOException io) {
            System.out.println("URL " + docURL + " cannot be opened for reading " + io);
        }

    }

    // match all the content words in the document and build up the set of contents
    private void buildContents(String inputLine) {

        // create a pattern to match all the html tags in the document line
        Pattern contentPattern = Pattern.compile("\\<.*?>");

        // match the above pattern in the line
        Matcher matcher = contentPattern.matcher(inputLine);

        // filter out the html tags
        String output = matcher.replaceAll("");

        // remove the special characters and numbers from the filtered string
        output = output.replaceAll("[^A-Za-z\\s]", "");

        // Construct a string tokenizer for the filtered string, using the default delimiter set
        StringTokenizer tok = new StringTokenizer(output);

        // convert each token to lower case and add it to the set of contents
        while (tok.hasMoreTokens()) {
            contents.add((tok.nextToken()).toLowerCase());
        }

        // store the size of the contents set
        numContents = contents.size();

        // get the first and last elements of the contents set
        if (numContents > 0) {
            firstContentWord = (((TreeSet) contents).first()).toString();
            lastContentWord = (((TreeSet) contents).last()).toString();
        }
    }

    private void buildKeywords(String inputLine) {

        // create a pattern to match all the keywords metatags in the document line
        // match is case-insensitive
        Pattern metaPattern = Pattern.compile("(?i)<meta.*name=\"keywords\".*");

        Matcher metaMatch = metaPattern.matcher(inputLine);

        // for each match, create another pattern to match the content of the keywords
        // match is case-insensitive
        while (metaMatch.find()) {
            Pattern keywordPattern = Pattern.compile("(?i)content=\"(.*?)\"");

            Matcher keywordsContent = keywordPattern.matcher(metaMatch.group());

            // for each match of the set of contents, extract each keyword, separated by a comma and optional space, and store it in an array
            while (keywordsContent.find()) {
                String keywordStr = keywordsContent.group(1);
                String[] keywordsArray = keywordStr.split(",\\s*");

                // add all the keywords in the keywords set
                for (int i = 0; i < keywordsArray.length; i++) {
                    keywords.add(keywordsArray[i]);
                }
            }
        }

        // store the size of the keywords set
        numKeywords = keywords.size();

    }

    // hashCode method for this object which calculates the value based on the URL string
    @Override
    public int hashCode() {

        return docURL.hashCode();
    }

    // method which determines if the document is well-formed or not
    private void checkForm() {

        // create a set of tags which do not need a closing tag
        Set<String> exemptedTags = new HashSet<String>();
        exemptedTags.add("meta");
        exemptedTags.add("p");
        exemptedTags.add("hr");
        exemptedTags.add("br");
        exemptedTags.add("link");

        // a stack to push all the opening tags to
        Stack<String> openingTagStack = new Stack<String>();

        // pattern to match all the markups
        Pattern tagPattern = Pattern.compile("<([/!]?([a-zA-Z]+).*?/?)>");

        // form value of the document which is converted into a corresponding string later
        int formValue = 2;

        try {
            BufferedReader doc = new BufferedReader(new InputStreamReader(docURL.openStream()));

            String docLine;
            while ((docLine = doc.readLine()) != null && formValue != 0) {
                Matcher matcher = tagPattern.matcher(docLine);
                while (matcher.find()) {

                    // match the entire markup
                    String markUp = matcher.group(1);

                    // match the tag part of the markup
                    String markUpTag = matcher.group(2);

                    // first and last characters of the entire markup
                    char startChar = markUp.charAt(0);
                    char endChar = markUp.charAt(markUp.length() - 1);

                    // for certain cases, a closing tag is not mandatory, hence continue
                    if (startChar == '!' || endChar == '/') {
                        continue;
                    } // if this is an opening tag then push it on to the stack
                    else if (startChar != '/') {
                        openingTagStack.push(markUpTag.toLowerCase());
                    } // if this a closing tag then look for a matching opening tag in the stack
                    else {
                        // if opening tag stack is empty then this is clearly an ill-formed document
                        // we do not need to proceed
                        if (openingTagStack.isEmpty()) {
                            formValue = 0;
                        } // repeat until we find an opening tag match for this closing tag
                        else {
                            boolean repeat = true;
                            while (repeat) {
                                // pop a string from the stack
                                String openTagStr = (String) openingTagStack.pop();
                                String closeTagStr = markUpTag.toLowerCase();

                                // if the closing tag does not match the opening tag,
                                // then if the opening tag does not require a closing tag, the document is partly well-formed
                                // else the document is ill-formed
                                if (!openTagStr.equals(closeTagStr)) {
                                    if (exemptedTags.contains(openTagStr)) {
                                        formValue = 1;
                                    } else {
                                        formValue = 0;
                                    }
                                } else {
                                    repeat = false;
                                }
                            }
                        }
                    }
                }
            }

            doc.close();

        } catch (IOException io) {
            System.out.println("file cannot be opened " + io);
        } catch (EmptyStackException ese) {
            formValue = 0;
        }

        // translate form values into strings defining the form of the document
        switch (formValue) {
            case 0:
                formString = "ill-formed";
                break;
            case 1:
                formString = "partly well-formed";
                break;
            case 2:
                formString = "well-formed";
                break;
        }

    }

    public int compareTo(WebDoc o) {

        int i = 0;
        try {
            int j = this.getURL().toString().compareToIgnoreCase(o.getURL().toString());
            i = j;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return i;

    }
}

package com.indexingbrowser.core;

/**
 *
 * @author davidolubajo
 */
import java.util.ArrayList;

public class SuperQuery {
    String[] rootElements;

    public SuperQuery(String queryExpression){
        rootElements = getRootElements(queryExpression);
    }

    static String[] getRootElements(String queryExpression) {

        ArrayList<Character> expressionCharacters = new ArrayList();
        char[] array = queryExpression.toCharArray();
        for (int i = 0; i < array.length; i++) {
            expressionCharacters.add(array[i]);
        }

        boolean rootBracketExists = false;
        int nonRootBracketCount = 0;
        String str = "";
        for (int i = 0; i < array.length; i++) {
            if (array[i] == '(' && !rootBracketExists) {
                rootBracketExists = true;
            } else if (array[i] == '(' && rootBracketExists) {
                nonRootBracketCount++;
            }
            if (array[i] == ',' && !rootBracketExists) {
                expressionCharacters.set(i, ':');
            }
            if (array[i] == ')' && nonRootBracketCount == 0) {
                rootBracketExists = false;
            } else if (array[i] == ')' && nonRootBracketCount > 0) {
                nonRootBracketCount--;
            }
            str += expressionCharacters.get(i);
        }
        return str.split(":");
    }
    
   
    @Override
    public String toString() {
        String str = "super:>>";
        for (String getStr : rootElements) {
            str += getStr + ":>>";
        }
        System.out.println(str + ":>>");
        return str + ")";
    }

}


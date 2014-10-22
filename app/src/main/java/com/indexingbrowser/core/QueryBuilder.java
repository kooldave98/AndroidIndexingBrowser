package com.indexingbrowser.core;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {

    public QueryBuilder() {
    }

    public static Query parse(String queryExpression) throws InvalidQueryException {
        String modifiedQueryExpression = queryExpression.replaceAll(" ", "");

        if (modifiedQueryExpression.startsWith("and(") && modifiedQueryExpression.endsWith(")")) {
            modifiedQueryExpression = modifiedQueryExpression.substring(modifiedQueryExpression.indexOf("and(") + 4, modifiedQueryExpression.lastIndexOf(")"));
            return new AndQuery(modifiedQueryExpression);
        } else if (modifiedQueryExpression.startsWith("or(") && modifiedQueryExpression.endsWith(")")) {
            modifiedQueryExpression = modifiedQueryExpression.substring(modifiedQueryExpression.indexOf("or(") + 3, modifiedQueryExpression.lastIndexOf(")"));
            return new OrQuery(modifiedQueryExpression);
        } else if (modifiedQueryExpression.startsWith("not(") && modifiedQueryExpression.endsWith(")")) {
            modifiedQueryExpression = modifiedQueryExpression.substring(modifiedQueryExpression.indexOf("not(") + 4, modifiedQueryExpression.lastIndexOf(")"));
            return new NotQuery(modifiedQueryExpression);
        } else if (queryExpression.matches("[a-zA-Z]+")) {
            return new AtomicQuery(modifiedQueryExpression);
        } else {
            throw new InvalidQueryException("#2: Invalid prefix Query");
        }

    }

    //Incomplete method for now it returns the String input parameter without any modifications.
    private static String addBrackets(String x) {
        String result = "";
        String[] words = x.split(" ");
        ArrayList<String> compounds = new ArrayList<String>();
        for (String str : words) {
            compounds.add(str);
        }

        if (compounds.size() > 3) {
            for (int i = 0; i < words.length; i++) {
                for (int j = 0; i < 3; i++) {
                    if (words[i].equals("not")) {
                        result += words[i] + " " + words[i + 1];
                        break;
                    } else {
                        result += words[i] + " ";
                    }
                    compounds.add("(" + result + ")");
                }
            }

        } else {
            return x;
        }
        return x;
    }

    public static String convertToInfix(String x) {
        //optional Pre-formatting
        x = x.toLowerCase();
        do {
            x.replaceAll("  ", " ");
        } while (x.contains("  "));

        //optional pre-bracketing
        if (!(x.contains("(") || x.contains(")"))) {
            x = addBrackets(x);
        }

        if (!x.contains(" ")) {
            return x; //LEVEL 1: if paramter is a single operand e.g "boy"
        }
        String[] words = x.split(" ");
        if (words.length == 2) {
            return "not(" + words[1] + ")"; //if parameter is a 'not' function e.g "not boy"
        } else if (words.length == 3) //LEVEL 2: if parameter is an 'and' or 'or' operation involving only 2 operands e.g "boy or girl"
        {
            String op = words[1];
            if (op.equals("and")) {
                return "and(" + words[0] + "," + words[2] + ")";
            } else if (op.equals("or")) {
                return "or(" + words[0] + "," + words[2] + ")";
            }
        } else if (words.length > 3) //LEVEL 3: if parameter is complex, involving varying combinations e.g "gun and (drugs and not rock) and roll"
        {
            List<String> compounds = new ArrayList<String>();
            char[] chars = x.toCharArray();
            String mainOp = "", compound = "";

            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == ')') {
                    break;
                } else if (chars[i] == 'n' && chars[i + 1] == 'o' && chars[i + 2] == 't') {
                    compound = "not ";
                    i += 4;
                    while (i < chars.length && chars[i] != ' ') {
                        compound += chars[i];
                        i++;
                    }
                    compounds.add(compound);
                    continue;
                } else if (chars[i] == '(') {
                    compound = "";
                    int count = 1;
                    i++;

                    for (; i < chars.length; i++) {
                        if (chars[i] == '(') {
                            count++;
                        } else if (chars[i] == ')') {
                            count--;
                        }
                        if (count <= 0) {
                            break;
                        }
                        compound += chars[i];
                    }
                    compounds.add(compound);
                    continue;
                } else if (chars[i] == ' ') {
                    continue;
                } else if (chars[i] == 'a' && chars[i + 1] == 'n' && chars[i + 2] == 'd') {
                    mainOp = "and(";
                    i += 3;
                    continue;
                } else if (chars[i] == 'o' && chars[i + 1] == 'r') {
                    mainOp = "or(";
                    i += 2;
                    continue;
                } else {
                    compound = "";
                    while (i < chars.length && chars[i] != ' ') {
                        compound += chars[i];
                        i++;
                    }
                    compounds.add(compound);
                    continue;
                }
            }

            for (String s : compounds) {
                //this is what breaks down the complex expression into simpler forms that would eventually be hanled by level 1 or level 2
                mainOp += convertToInfix(s);
                mainOp += ",";
            }
            mainOp = mainOp.substring(0, mainOp.length() - 1);
            mainOp += ")";
            return mainOp;
        }
        return "";
    }

    public static Query parseInfix(String x) throws InvalidQueryException {

        String stf = convertToInfix(x);
        //System.out.println(stf);
        return parse(stf);
    }
}

package com.indexingbrowser.core;

import java.util.Set;
import java.util.TreeSet;

public class NotQuery extends SuperQuery implements Query {

    public NotQuery(String queryExpression) {
        super(queryExpression);
    }

    public Set<WebDoc> matches(WebIndex wind) throws InvalidQueryException {
        if (rootElements.length != 1) {
            throw new InvalidQueryException("#1: Expression contains a NOT QUERY with more than one argument");
        }

        Set<WebDoc> notDocs = new TreeSet();
        notDocs.addAll(wind.getDocuments());
        for (String str : rootElements) {
            notDocs.removeAll(QueryBuilder.parse(str).matches(wind));
        }
        return notDocs;
    }

     @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("NOT ( ");
        boolean first = true;
        for (String getStr : rootElements) {
            try {
                if (!first) {
                    s.append(" , ");
                }
                s.append(QueryBuilder.parse(getStr));
                first = false;
            } catch (InvalidQueryException ex) {
                System.out.println(ex.getMessage() + "Invalid Query");
            }
        }
        s.append(" )");
        return s.toString();
    }
}

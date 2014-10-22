package com.indexingbrowser.core;

import java.util.Set;
import java.util.TreeSet;

public class OrQuery extends SuperQuery implements Query {

    public OrQuery(String queryExpression) {
        super(queryExpression);
    }

    public Set<WebDoc> matches(WebIndex wind) throws InvalidQueryException {
        if (rootElements.length < 2) {
            throw new InvalidQueryException("#1: Expression contains an OR QUERY with less than 2 arguments");
        }
        Set<WebDoc> orDocs = new TreeSet<WebDoc>();        
        for (String str : rootElements) {
            orDocs.addAll(QueryBuilder.parse(str).matches(wind));
        }
        return orDocs;
    }

     @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("OR ( ");
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

package com.indexingbrowser.core;

import java.util.Set;
import java.util.TreeSet;

public class AndQuery extends SuperQuery implements Query {
    //QueryBuilder querB;

    public AndQuery(String queryExpression) {
        super(queryExpression);
        //querB = new QueryBuilder();
    }

    public Set<WebDoc> matches(WebIndex wind) throws InvalidQueryException {
        if (rootElements.length < 2) {
            throw new InvalidQueryException("#1: Expression contains an AND QUERY with less than 2 arguments");
        }

        boolean first = true;
        Set<WebDoc> andDocs = new TreeSet();
        for (String str : rootElements) {
            if (first) {
                andDocs.addAll(QueryBuilder.parse(str).matches(wind));
            } else {
                andDocs.retainAll(QueryBuilder.parse(str).matches(wind));
                first = false;
            }
        }
        return andDocs;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("AND ( ");
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

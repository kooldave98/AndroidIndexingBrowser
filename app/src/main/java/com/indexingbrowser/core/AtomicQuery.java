package com.indexingbrowser.core;

import java.util.Set;

public class AtomicQuery extends SuperQuery implements Query {

    public AtomicQuery(String queryExpression) {
        super(queryExpression);
    }

    public Set<WebDoc> matches(WebIndex wind) throws InvalidQueryException {
        if (rootElements.length != 1) {
            throw new InvalidQueryException("#1: Expression contains an invalid ATOMIC QUERY");
        }
        return wind.getMatches(rootElements[0]);
    }

     @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("ATOMIC ( ");
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

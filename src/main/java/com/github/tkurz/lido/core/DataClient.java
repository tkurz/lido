package com.github.tkurz.lido.core;

import com.github.tkurz.lido.exception.EvaluationException;
import org.apache.marmotta.client.model.rdf.RDFNode;
import org.apache.marmotta.client.model.rdf.URI;
import org.apache.marmotta.client.model.sparql.SPARQLResult;

import java.util.List;
import java.util.Map;

/**
 * ...
 * <p/>
 * Author: Thomas Kurz (tkurz@apache.org)
 */
public interface DataClient {

    public Map<String, List<RDFNode>> evaluateLDPath(URI uri, String query) throws EvaluationException;
    public SPARQLResult evaluateSPARQL(String query) throws EvaluationException;

}

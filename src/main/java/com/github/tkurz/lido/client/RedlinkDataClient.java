package com.github.tkurz.lido.client;

import com.github.tkurz.lido.exception.EvaluationException;
import com.github.tkurz.lido.core.DataClient;
import io.redlink.sdk.RedLink;
import io.redlink.sdk.impl.data.model.LDPathResult;
import org.apache.marmotta.client.model.rdf.RDFNode;
import org.apache.marmotta.client.model.rdf.URI;
import org.apache.marmotta.client.model.sparql.SPARQLResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ...
 * <p/>
 * Author: Thomas Kurz (tkurz@apache.org)
 */
public class RedlinkDataClient implements DataClient {

    private RedLink.Data dataClient;

    public RedlinkDataClient(RedLink.Data dataClient) {
        this.dataClient = dataClient;
    }

    @Override
    public Map<String, List<RDFNode>> evaluateLDPath(URI uri, String query) throws EvaluationException {
        try {
            Map<String, List<RDFNode>> result = new HashMap<>();
            LDPathResult r = dataClient.ldpath(uri.getUri(), query);
            for (String field : r.getFields()) {
                result.put(field, r.getResults(field));
            }
            return result;
        } catch (Exception e) {
            throw new EvaluationException(e.getMessage(), e);
        }
    }

    @Override
    public SPARQLResult evaluateSPARQL(String query) throws EvaluationException {
        try {
            return dataClient.sparqlTupleQuery(query);
        } catch (Exception e) {
            throw new EvaluationException(e.getMessage(), e);
        }
    }
}

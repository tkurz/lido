package com.github.tkurz.lido.core;

import com.github.tkurz.lido.EvaluationException;
import com.github.tkurz.lido.extension.LangString;
import org.apache.marmotta.client.model.rdf.Literal;
import org.apache.marmotta.client.model.rdf.RDFNode;
import org.apache.marmotta.client.model.rdf.URI;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * ...
 * <p/>
 * Author: Thomas Kurz (tkurz@apache.org)
 */
public class LDPathMapper<T> {

    private static String SPARQL_QUERY = "SELECT ?uri WHERE {?uri a <%s>}";
    private static String SPARQL_QUERY_COUNT = "SELECT (count(?uri) AS ?count) WHERE {?uri a <%s>}";
    private static String SPARQL_QUERY_PAGE = "SELECT ?uri WHERE {?uri a <%s>} ORDER BY ?uri LIMIT $LIMIT OFFSET $OFFSET";

    private DataClient dataClient;
    private Class<T> clazz;

    private String sparqlQuery;
    private String sparqlQueryPage;
    private String sparqlQueryCount;
    private String ldpathQuery = "";

    private List<Mapping> mappings = new ArrayList<>();

    public LDPathMapper(DataClient dataClient, Class<T> clazz) {
        this.dataClient = dataClient;
        this.clazz = clazz;

        if(clazz.isAnnotationPresent(Type.class)) {
            this.sparqlQuery = String.format(SPARQL_QUERY, clazz.getAnnotation(Type.class).value());
            this.sparqlQueryPage = String.format(SPARQL_QUERY_PAGE, clazz.getAnnotation(Type.class).value());
            this.sparqlQueryCount = String.format(SPARQL_QUERY_COUNT, clazz.getAnnotation(Type.class).value());
        }

        //get path annotations
        for(Field field : clazz.getFields()) {
            if(field.isAnnotationPresent(Path.class)) {
                //add to program
                ldpathQuery += field.getName() + "=" + field.getAnnotation(Path.class).value() + ";\n";

                //add evaluator stuff
                if(field.getType().isPrimitive() || field.getType().equals(String.class)) {
                    mappings.add(new Mapping(field.getName(),field.getType(),field.getAnnotation(Path.class).value(),null,false));
                } else if(implementsInterface(field.getType(), Set.class)) {
                    java.lang.reflect.Type[] types = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
                    if(types.length == 1) {
                        Class tclass = (Class) types[0];
                        if(tclass.isPrimitive() || tclass.equals(String.class)) {
                            mappings.add(new Mapping(field.getName(), tclass, field.getAnnotation(Path.class).value(), null, true));
                        } else {
                            LDPathMapper mapper = new LDPathMapper(dataClient, tclass);
                            mappings.add(new Mapping(field.getName(), tclass, field.getAnnotation(Path.class).value(), mapper, true));
                        }
                    } else throw new RuntimeException("Set must have a generic type set");
                } else {
                    LDPathMapper mapper = new LDPathMapper(dataClient, field.getType());
                    mappings.add(new Mapping(field.getName(), field.getType(), field.getAnnotation(Path.class).value(), mapper, false));
                }
            }
        }
    }

    class Mapping {
        String name;
        Class clazz;
        String path;
        LDPathMapper mapper;
        boolean isSet;

        public Mapping(String name,Class clazz, String path, LDPathMapper mapper, boolean isSet) {
            this.name = name;
            this.clazz = clazz;
            this.path = path;
            this.mapper = mapper;
            this.isSet = isSet;
        }
    }

    private boolean implementsInterface(Class object, Class iface) {
        for(Class i : object.getInterfaces()) {
            if(i.equals(iface)) return true;
        }
        return false;
    }

    public T findOne(URI uri) throws EvaluationException {

        Map<String,List<RDFNode>> obj = dataClient.evaluateLDPath(uri, ldpathQuery);

        if(obj == null) return null;

        try {
            T result = clazz.newInstance();

            for(Mapping mapping: mappings) {
                List<RDFNode> nodes = obj.get(mapping.name);
                Field field = result.getClass().getField(mapping.name);

                if(nodes.isEmpty()) continue;

                if(mapping.isSet) {

                    Set set = new HashSet();

                    for(RDFNode node : nodes) {
                        if(mapping.clazz.equals(String.class)) {  //TODO add all primitives
                            set.add(node.toString());
                        } else if(mapping.clazz.equals(LangString.class)) {
                            //TODO handle this properly
                        } else if(mapping.clazz.equals(int.class)) {
                            set.add(Integer.parseInt(node.toString()));
                        } else if(mapping.clazz.equals(double.class)) {
                            set.add(Double.parseDouble(node.toString()));
                        } else if(mapping.clazz.equals(long.class)) {
                            set.add(Long.parseLong(node.toString()));
                        } else if(mapping.clazz.equals(float.class)) {
                            set.add(Float.parseFloat(node.toString()));
                        } else if(mapping.clazz.equals(boolean.class)) {
                            set.add(Boolean.parseBoolean(node.toString()));
                        } else if(mapping.mapper != null) {
                            if(node instanceof URI) {
                                set.add(mapping.mapper.findOne((URI)node));
                            }
                        } else {
                            throw new RuntimeException("Mapping for class not yet implemented: " + mapping.clazz);
                        }
                    }

                    field.set(result,set);

                } else {
                    if(mapping.clazz.equals(String.class)) {  //TODO add all primitives
                        RDFNode v = nodes.iterator().next();
                        if(!nodes.isEmpty()) field.set(result, v.toString());
                    } else if(mapping.clazz.equals(LangString.class)) {
                        LangString ls = new LangString();
                        for(RDFNode node : nodes) {
                            if(node instanceof Literal) {
                                Literal l = (Literal) node;
                                ls.addString(l.getLanguage(),l.toString());
                            }
                        }
                        field.set(result,ls);
                    } else if(mapping.clazz.equals(int.class)) {
                        RDFNode v = nodes.iterator().next();
                        if(v != null) field.setInt(result, Integer.parseInt(v.toString()));
                    } else if(mapping.clazz.equals(double.class)) {
                        RDFNode v = nodes.iterator().next();
                        if(v != null) field.setDouble(result, Double.parseDouble(v.toString()));
                    } else if(mapping.clazz.equals(long.class)) {
                        RDFNode v = nodes.iterator().next();
                        if(v != null) field.setLong(result, Long.parseLong(v.toString()));
                    } else if(mapping.clazz.equals(float.class)) {
                        RDFNode v = nodes.iterator().next();
                        if(v != null) field.setFloat(result, Float.parseFloat(v.toString()));
                    } else if(mapping.clazz.equals(boolean.class)) {
                        RDFNode v = nodes.iterator().next();
                        if(v != null) field.setBoolean(result, Boolean.parseBoolean(v.toString()));
                    } else if(mapping.mapper != null) {
                        RDFNode v = nodes.iterator().next();
                        if(v != null && v instanceof URI) field.set(result, mapping.mapper.findOne((URI)v));
                    } else {
                        throw new RuntimeException("Mapping for class not yet implemented: " + mapping.clazz);
                    }
                }
            }

            return result;

        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Cannot instantiate class",e);
        }
    }

    public Set<T> findAll(Set<URI> uris) throws EvaluationException {
        HashSet<T> result = new HashSet<>();
        for(URI uri : uris) {
            result.add(findOne(uri));
        }
        return result;
    }

    public Set<T> findAll() throws EvaluationException {

        if(sparqlQuery == null) throw new RuntimeException("Cannot list untyped classes");

        HashSet<T> result = new HashSet<>();

        //get list of ids
        List<Map<String, RDFNode>> uris = dataClient.evaluateSPARQL(sparqlQuery);

        for(Map<String, RDFNode> row : uris) {
            result.add(findOne((URI)row.get("uri")));
        }

        return result;
    }

    public Page<T> findPage(int pageNumber, int pageSize) throws EvaluationException {
        if(sparqlQuery == null) throw new RuntimeException("Cannot list untyped classes");

        List<T> result = new ArrayList<>();

        //get count
        List<Map<String, RDFNode>> uris = dataClient.evaluateSPARQL(sparqlQueryCount);
        int count = Integer.parseInt(uris.get(0).get("count").toString());

        Page page = new Page();
        page.setNumberOfPages((int) Math.ceil(count / (double) pageSize));
        page.setPage(pageNumber);
        page.setPagesSize(pageSize);

        String query = sparqlQueryPage.replace("$LIMIT",String.valueOf(pageSize)).replace("$OFFSET",String.valueOf((page.getPage()-1)*pageSize));

        //get list of ids
        uris = dataClient.evaluateSPARQL(query);

        for(Map<String,RDFNode> row : uris) {
            result.add(findOne((URI)row.get("uri")));
        }

        page.setContent(result);

        return page;
    }

}

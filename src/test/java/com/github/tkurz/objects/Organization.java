package com.github.tkurz.objects;

import com.github.tkurz.lido.core.Path;
import com.github.tkurz.lido.core.Type;

import java.util.HashSet;

/**
 * ...
 * <p/>
 * Author: Thomas Kurz (tkurz@apache.org)
 */
@Type("http://schema.org/Organization")
public class Organization {

    public int noValue;

    @Path("<http://schema.org/telephone>")
    public HashSet<String> phone;

    @Path("<http://schema.org/name>")
    public String name;

    @Path("<http://schema.org/employee>")
    public HashSet<Employee> employees;

}

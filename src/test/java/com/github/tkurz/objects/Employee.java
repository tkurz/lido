package com.github.tkurz.objects;

import com.github.tkurz.lido.core.Path;
import com.github.tkurz.lido.core.Type;

/**
 * ...
 * <p/>
 * Author: Thomas Kurz (tkurz@apache.org)
 */
@Type("http://schema.org/Person")
public class Employee {

    @Path("<http://schema.org/name>")
    public String name;

    @Path("<http://schema.org/weight>")
    public double weight;

}

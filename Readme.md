LIDO - The Linked Data Object Mapper for Java
=============================================

With Lido you can easily map Linked Data resources to Java Objects.

Example
-------
Create a POJOs with annotations e.g. Employee and Organization.

```
@Type("http://schema.org/Person")
public class Employee {

    @Path("<http://schema.org/name>")
    public String name;

    @Path("<http://schema.org/description>")
    public LangString langString;

    @Path("<http://schema.org/weight>")
    public double weight;

}

@Type("http://schema.org/Organization")
public class Organization {

    @Path("<http://schema.org/name>")
    public String name;

    @Path("<http://schema.org/employee>")
    public HashSet<Employee> employees;

    @Path("<http://schema.org/telephone>")
    public HashSet<String> phone;

}
```

Create an LDPathMapper with a Class that implements DataClient. In this case, we want to get the organizations (that also includes all the Employee objects, as you can see).
You have several access methods like ```findAll```, ```findPage(size,number)```, ```findOne(uri)``` and ```findSome(list:uri))```.
```
    //create mapper for organizations
    LDPathMapper<Organization> organizations = new LDPathMapper<>(dataClient, Organization.class);

    //get all organizations
    Set<Organization> all_organizations = organizations.findAll();

    //get pages organizations
    Page<Organization> paged_organizations = organizations.findPage(1,3);

    //create mapper for employees
    LDPathMapper<Employee> employees = new LDPathMapper<>(dataClient, Employee.class);

    //get one employee
    Employee employee = employees.findOne(new URI("http://example.org/e1"));

    //get some employees
    Set<Employee> some_employees = employees.findSome(ImmutableSet.of(
            new URI("http://example.org/e1"),
            new URI("http://example.org/e1")
    ));

```
package com.github.tkurz;

import com.github.tkurz.lido.exception.EvaluationException;
import com.github.tkurz.lido.client.RedlinkDataClient;
import com.github.tkurz.lido.core.DataClient;
import com.github.tkurz.lido.core.LDPathMapper;
import com.github.tkurz.lido.core.Page;
import com.github.tkurz.objects.Employee;
import com.github.tkurz.objects.Organization;
import io.redlink.sdk.RedLink;
import io.redlink.sdk.RedLinkFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * Unit test for simple App.
 */
public class TestMapping {

    @Test
    public void testSimpleMapping() throws EvaluationException {

        //create a redlink data client
        String apiKey = "EoG3Dxxir8taiUpVHbx68VwpBqw6eI2a8bc05f63";
        RedLink.Data redlinkClient = RedLinkFactory.createDataClient(apiKey);

        //create a lido dataClient
        DataClient dataClient = new RedlinkDataClient(redlinkClient);

        //create an object mapper
        LDPathMapper<Employee> personMapper = new LDPathMapper<>(dataClient, Employee.class);

        Set<Employee> persons = personMapper.findAll();

        Assert.assertEquals(2,persons.size(),0.0);

        //create organization mapper
        LDPathMapper<Organization> organizationMapper = new LDPathMapper<>(dataClient, Organization.class);

        Page<Organization> organizationPage1 = organizationMapper.findPage(1,1);

        Assert.assertEquals(2, organizationPage1.getNumberOfPages());
        Assert.assertEquals(1, organizationPage1.getPage());
        Assert.assertTrue(organizationPage1.hasNext());
        Assert.assertFalse(organizationPage1.hasPrevious());

        Page<Organization> organizationPage2 = organizationMapper.findPage(2,1);

        Assert.assertEquals(2, organizationPage2.getNumberOfPages());
        Assert.assertEquals(2, organizationPage2.getPage());
        Assert.assertTrue(organizationPage2.hasPrevious());
        Assert.assertFalse(organizationPage2.hasNext());

    }

}

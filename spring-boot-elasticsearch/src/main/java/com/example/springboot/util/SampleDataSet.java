package com.example.springboot.util;

import com.example.springboot.entity.Department;
import com.example.springboot.entity.Employee;
import com.example.springboot.entity.Organization;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 测试数据生成类
 */
@Component
public class SampleDataSet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleDataSet.class);

    @Autowired
    ElasticsearchRestTemplate template;

    @PostConstruct
    public void init() {
        for (int i = 0; i < 10; i++) {
            bulk(i);
        }
    }

    public void bulk(int ii) {
        try {
            // check if the index is existed
            if (template.indexOps(Employee.class).exists()) {
                return;
            }else {
                template.indexOps(Employee.class).create();
            }
            ObjectMapper mapper = new ObjectMapper();
            List<IndexQuery> queries = new ArrayList<>();
            List<Employee> employees = rndEmployees();
            for (Employee employee : employees) {
                IndexQuery indexQuery = new IndexQuery();
                indexQuery.setId(employee.getId().toString());
                indexQuery.setSource(mapper.writeValueAsString(employee));
                //Set the index name & doc type

                queries.add(indexQuery);
            }
            if (queries.size() > 0) {
                template.bulkIndex(queries,template.getIndexCoordinatesFor(Employee.class));
            }
            LOGGER.info("BulkIndex completed: {}", ii);
        } catch (Exception e) {
            LOGGER.error("Error bulk index", e);
        }
    }

    /**
     * 创建100 个 雇员
     * @return
     */
    private List<Employee> rndEmployees(){
        List<Employee> employees = new ArrayList<>();
        int id = 0;
        LOGGER.info("Starting from id: {}", id);
        for (int i = id; i < 100 + id; i++) {
            Random r = new Random();
            Faker faker = new Faker();
            Employee employee = new Employee();
            employee.setId((long) i);
            employee.setName(faker.name().username());
            employee.setAge(r.nextInt(60));
            employee.setPosition(faker.job().position());
            int departmentId = r.nextInt(5000);
            employee.setDepartment(new Department((long) departmentId, faker.company().name()));
            int organizationId = departmentId % 100;
            employee.setOrganization(new Organization((long) organizationId, "TestO" + organizationId, "Test Street No. " + organizationId));
            employees.add(employee);
        }
        return employees;

    }


}

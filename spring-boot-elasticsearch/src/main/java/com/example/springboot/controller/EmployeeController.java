package com.example.springboot.controller;

import com.example.springboot.entity.Employee;
import com.example.springboot.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.script.mustache.SearchTemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ldb
 */
@RestController
@RequestMapping("/employees")
@Slf4j
public class EmployeeController {

    @Autowired
    private ElasticsearchRestTemplate template;

    @Autowired
    private RestHighLevelClient client;

    @PostMapping
    public Employee add(@RequestBody Employee employee) {
        return template.save(employee);
    }


    /**
     * 单字段name  match query 查询
     * @param name
     * @return
     */
    @GetMapping("/match")
    public List<Employee> findByName(String name) {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                        .withQuery(QueryBuilders.matchQuery("name", name)).build();
        SearchHits<Employee> search = template.search(query, Employee.class,
                IndexCoordinates.of(Constants.EMPLOYEE_INDEX));
        log.info("query:{}",query.getQuery().toString());
        return search.stream().map(item->item.getContent()).collect(Collectors.toList());
    }

    /**
     * 创建一个inline search template , 并执行
     * @return
     * @throws IOException
     */
    @GetMapping("/inlineScript")
    public String inlineScript() throws IOException {
        SearchTemplateRequest request = new SearchTemplateRequest();
        request.setRequest(new SearchRequest(Constants.EMPLOYEE_INDEX));
        request.setScriptType(ScriptType.INLINE);
        request.setScript(
                "{" +
                        "  \"query\": { \"match\" : { \"{{field}}\" : \"{{value}}\" } }," +
                        "  \"size\" : \"{{size}}\"" +
                        "}");
        Map<String, Object> scriptParams = new HashMap<>();
        scriptParams.put("field", "department.name");
        scriptParams.put("value", "Auer-Smith");
        scriptParams.put("size", 5);
        request.setScriptParams(scriptParams);
        SearchTemplateResponse searchTemplateResponse = client.searchTemplate(request, RequestOptions.DEFAULT);
        return searchTemplateResponse.getResponse().toString();
    }

    /**
     * 创建一个inline search template , 并渲染，不执行
     * simulate = true
     * @return
     * @throws IOException
     */
    @GetMapping("/inlineScriptWithRender")
    public String inlineScriptWithRender(String name) throws IOException {
        SearchTemplateRequest request = new SearchTemplateRequest();
        request.setRequest(new SearchRequest(Constants.EMPLOYEE_INDEX));
        request.setScriptType(ScriptType.INLINE);
        request.setSimulate(true);
        request.setScript(
                "{" +
                        "  \"query\": { \"match\" : { \"{{field}}\" : \"{{value}}\" } }," +
                        "  \"size\" : \"{{size}}\"" +
                        "}");
        Map<String, Object> scriptParams = new HashMap<>();
        scriptParams.put("field", "department.name");
        scriptParams.put("value", "Auer-Smith");
        scriptParams.put("size", 5);
        request.setScriptParams(scriptParams);
        SearchTemplateResponse renderResponse  = client.searchTemplate(request, RequestOptions.DEFAULT);
        return renderResponse .getSource().utf8ToString();
    }

    /**
     * 创建一个可存储到es的search template
     * 测试方便，用了get请求，实际应该为post请求
     * @param templateName 模板名字
     * @throws IOException
     */
    @GetMapping("/scoreScript")
    public void scoreScript(String templateName) throws IOException {
        Request scriptRequest = new Request("POST", "_scripts/"+templateName);
        scriptRequest.setJsonEntity(
                "{" +
                        "  \"script\": {" +
                        "    \"lang\": \"mustache\"," +
                        "    \"source\": {" +
                        "      \"query\": { \"match\" : { \"{{field}}\" : \"{{value}}\" } }," +
                        "      \"size\" : \"{{size}}\"" +
                        "    }" +
                        "  }" +
                        "}");
        Response scriptResponse = client.getLowLevelClient().performRequest(scriptRequest);

    }

    /**
     * 调用search template
     * @param templateName 模板名字
     * @param indexName 查询的索引
     * @return
     * @throws IOException
     */
    @GetMapping("/callScript")
    public String callScript(String templateName,String indexName) throws IOException {
        SearchTemplateRequest request = new SearchTemplateRequest();
        request.setRequest(new SearchRequest(indexName));
        request.setScriptType(ScriptType.STORED);
        request.setScript(templateName);

        Map<String, Object> scriptParams = new HashMap<>();
        scriptParams.put("field", "department.name");
        scriptParams.put("value", "Auer-Smith");
        scriptParams.put("size", 5);
        request.setScriptParams(scriptParams);
        SearchTemplateResponse searchTemplateResponse = client.searchTemplate(request, RequestOptions.DEFAULT);
        return searchTemplateResponse.getResponse().toString();
    }

    /**
     * constantScore term query查询
     * @param name
     * @return
     */
    @GetMapping("/{name}/filter")
    public List<Employee> findByName2(@PathVariable("name") String name) {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("name",name))).build();
        SearchHits<Employee> search = template.search(query, Employee.class,
                IndexCoordinates.of(Constants.EMPLOYEE_INDEX));
        log.info("query:{}",query.getQuery().toString());
        return search.stream().map(item->item.getContent()).collect(Collectors.toList());
    }

    /**
     * 嵌套字段，match query 查询
     * @param organizationName
     * @return
     */
    @GetMapping("/organization/{organizationName}")
    public List<Employee> findByOrganizationName(@PathVariable("organizationName") String organizationName) {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("organization.name", organizationName)).build();
        SearchHits<Employee> hits  = template.search(query, Employee.class,
                IndexCoordinates.of(Constants.EMPLOYEE_INDEX));
        log.info("query:{}",query.getQuery().toString());
        return hits.stream().map(item->item.getContent()).collect(Collectors.toList());
    }

    @PostMapping("/person")
    public String save(@RequestBody Employee employee) {
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(employee.getId().toString())
                .withObject(employee)
                .build();
        String documentId = template.index(indexQuery, template.getIndexCoordinatesFor(employee.getClass()));
        return documentId;
    }

    /**
     * 通过索引 id查询 ，索引id 会赋值到对象的id上，因为有@Id
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Employee findById(@PathVariable("id") String id) {
        Employee person = template.get(id, Employee.class);
        return person;
    }
}

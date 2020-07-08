package com.example.springboot.controller;

import com.example.springboot.entity.DocBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.example.springboot.util.Constants.buildSearchRequest;

@Slf4j
@RestController
@RequestMapping("/elastic")
public class ElasticController {

    @Autowired
    private ElasticsearchRestTemplate template;

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/init")
    public void init() {
        List<DocBean> list = new ArrayList<>();
        list.add(new DocBean(1L, "XX0193", "XX8064", "xxxxxx", 1));
        list.add(new DocBean(2L, "XX0210", "XX7475", "xxxxxxxxxx", 1));
        list.add(new DocBean(3L, "XX0257", "XX8097", "xxxxxxxxxxxxxxxxxx", 1));
        template.save(list);

    }

    @GetMapping("/all")
    public Iterator<DocBean> all() throws IOException {
        SearchRequest searchRequest = buildSearchRequest("ems");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse response =
                client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHit = response.getHits().getHits();
        List<DocBean> res = new ArrayList<>();
        for (SearchHit hit : searchHit) {
            res.add(objectMapper.convertValue(hit.getSourceAsMap(), DocBean.class));
        }
        return res.iterator();
    }


}
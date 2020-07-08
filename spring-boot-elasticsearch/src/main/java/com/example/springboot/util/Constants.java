package com.example.springboot.util;

import org.elasticsearch.action.search.SearchRequest;

/**
 * 常量类
 *
 * @author ldb
 * @date 2020/07/06
 */
public class Constants {

    public static final String EMPLOYEE_INDEX = "employee";


    public static SearchRequest buildSearchRequest(String index) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        return searchRequest;
    }
}

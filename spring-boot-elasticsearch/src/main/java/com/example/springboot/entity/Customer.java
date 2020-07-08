package com.example.springboot.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @Document注解会对实体中的所有属性建立索引
 * indexName = "customer"，表示创建一个名称为customer的索引
 * type = "customer"，表示在索引中创建一个名为customer的type
 * shards = 1，表示只使用一个分片
 * replicas = 0，表示不使用复制
 * refreshInterval = "-1"，表示禁用索引刷新
 */
@Data
@Document(indexName = "customer", type = "customer", shards = 1, replicas = 0, refreshInterval = "-1")
public class Customer
{
    // @Id注解，在ElasticSearch里相当于该列是主键了，在查询时可以直接用主键查询
    @Id
    private String id;
    
    private String username;
    
    private String address;
    
    private int age;
    
    public Customer()
    {
    }
    
    public Customer(String username, String address, int age)
    {
        this.username = username;
        this.address = address;
        this.age = age;
    }
}
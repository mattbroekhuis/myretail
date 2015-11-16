package com.myretail.webservice.product.entity

import org.springframework.data.cassandra.mapping.PrimaryKey
import org.springframework.data.cassandra.mapping.Table

@Table(value = "product_price")
class ProductPriceEntity {
    @PrimaryKey
    String productId
    //would use enum, but https://jira.spring.io/browse/DATACASS-141
    String currencyCode
    double price
}

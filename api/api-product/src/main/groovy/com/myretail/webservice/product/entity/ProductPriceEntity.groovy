package com.myretail.webservice.product.entity

import com.myretail.webservice.product.dto.CurrencyCode
import org.springframework.data.cassandra.mapping.PrimaryKey
import org.springframework.data.cassandra.mapping.Table


@Table(value = "product_price")
class ProductPriceEntity {
    @PrimaryKey
    String id
    CurrencyCode currencyCode
    long productId
    double price
}

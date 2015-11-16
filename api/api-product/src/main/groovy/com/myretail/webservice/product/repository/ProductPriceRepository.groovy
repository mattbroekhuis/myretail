package com.myretail.webservice.product.repository

import com.myretail.webservice.product.entity.ProductPriceEntity
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface ProductPriceRepository extends CrudRepository<ProductPriceEntity, String> {
    @Query("select * from product_price where productId = ?0")
    ProductPriceEntity findByProductId(String productId)
}

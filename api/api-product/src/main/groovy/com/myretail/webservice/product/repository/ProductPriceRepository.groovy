package com.myretail.webservice.product.repository

import com.myretail.webservice.product.entity.ProductPriceEntity
import org.springframework.data.repository.CrudRepository

interface ProductPriceRepository extends CrudRepository<ProductPriceEntity, Long> {

}

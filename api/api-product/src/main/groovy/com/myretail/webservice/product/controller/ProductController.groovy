package com.myretail.webservice.product.controller

import com.myretail.webservice.product.dto.Price
import com.myretail.webservice.product.dto.Product
import com.myretail.webservice.product.entity.ProductPriceEntity
import com.myretail.webservice.product.remote.RemoteProductApi
import com.myretail.webservice.product.repository.ProductPriceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/product")
class ProductController {
    @Autowired
    ProductPriceRepository repository

    @Autowired
    RemoteProductApi remoteProductApi

    /** do we throw a 404 if we don't have the pricing information? or output just the title? **/
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Product get(@PathVariable String id) {
        getOne(id)
    }

    Product getOne(String id) {
        //throws exception if product doesn't exist
        String productTitle = remoteProductApi.productTitle(id)
        Product product = new Product(productId: id, name: productTitle)

        ProductPriceEntity productPriceEntity = repository.findByProductId(id)
        if (productPriceEntity) {
            product.current_price = new Price(currency_code: productPriceEntity.currencyCode, value: productPriceEntity.price)
        }
        product
    }

    @RequestMapping(value = "/{id}/current_price", method = RequestMethod.PUT)
    void putPrice(@PathVariable String id, @RequestBody Price price) {
        ProductPriceEntity productPriceEntity = repository.findByProductId(id)
        if (productPriceEntity) {
            productPriceEntity.price = price.value
            productPriceEntity.currencyCode = price.currency_code
        } else {
            productPriceEntity = new ProductPriceEntity(productId: id, price: price.value, currencyCode: price.currency_code)
        }

        repository.save(productPriceEntity)
    }
}

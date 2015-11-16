package com.myretail.webservice.product.controller

import com.myretail.framework.exception.NotFound404Exception
import com.myretail.webservice.product.dto.Price
import com.myretail.webservice.product.dto.Product
import com.myretail.webservice.product.entity.ProductPriceEntity
import com.myretail.webservice.product.repository.ProductPriceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/product")
class ProductController {
    @Autowired
    ProductPriceRepository repository

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    Product get(@PathVariable String id) {
        ProductPriceEntity productPriceEntity = repository.findByProductId(id)
        if (!productPriceEntity) {
            throw new NotFound404Exception()
        }
        new Product(id: id, name: "fetch this", current_price: new Price(currency_code: productPriceEntity.currencyCode, value: productPriceEntity.price))
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

    //https://www.tgtappdata.com/v1/products/pdp/TCIN/13860427
}

package com.myretail.webservice.product.controller

import com.myretail.webservice.product.dto.CurrencyCode
import com.myretail.webservice.product.dto.Price
import com.myretail.webservice.product.dto.Product
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/product")
class ProductController {
//    @Autowired
//    ProductPriceDao productDao

    @RequestMapping("/{id}")
    public Product one(@PathVariable long id) {
        //call external api and populate
        String name = "Super sweet product"
        Price price = new Price([value: 50, currency_code: CurrencyCode.USD])
        Product product = new Product([id: id, name: name, current_price: price])
        product
    }

    //https://www.tgtappdata.com/v1/products/pdp/TCIN/13860427
}

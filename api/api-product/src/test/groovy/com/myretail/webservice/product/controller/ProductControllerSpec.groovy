package com.myretail.webservice.product.controller

import com.myretail.webservice.product.enumeration.CurrencyCode
import com.myretail.webservice.product.entity.ProductPriceEntity
import com.myretail.webservice.product.remote.RemoteProductApi
import com.myretail.webservice.product.repository.ProductPriceRepository
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ProductControllerSpec extends Specification {
    //spring apparently likes to tack on the utf charset, which makes their test framework not match with their own media types
    //should be fixed long ago in 3.x (https://jira.spring.io/browse/SPR-10165), but i can't get to the comments/work on their jira because it keeps giving me errors
    //so will just use this shim for now
    static String JSON_MEDIA_TYPE = APPLICATION_JSON_VALUE + ";charset=UTF-8";

    def ProductPriceRepository repository = Mock(ProductPriceRepository)
    def RemoteProductApi remote = Mock(RemoteProductApi)

    def underTest = new ProductController(repository: repository, remoteProductApi: remote)

    def mockMvc = MockMvcBuilders.standaloneSetup(underTest).build()


    def "reading by id"() {
        given:
        def expectedPricingEntry = new ProductPriceEntity(productId: '123', price: 50.0, currencyCode: CurrencyCode.USD)
        def expectedProductTitle = "the title of the thing"
        when:
        def response = mockMvc.perform(
                get('/product/123')
                        .accept(APPLICATION_JSON)
        )
        then:
        remote.productTitle("123") >> expectedProductTitle
        repository.findByProductId("123") >> expectedPricingEntry
        response.andExpect(status().isOk())
        response.andExpect(content().contentType(JSON_MEDIA_TYPE))
        response.andReturn().response.contentAsString == '''{"productId":"123","name":"the title of the thing","current_price":{"value":50.0,"currency_code":"USD"}}'''
    }
}

package com.myretail.webservice.product.controller

import com.myretail.framework.docker.ExtendedDockerClientImpl
import com.myretail.webservice.product.dto.CurrencyCode
import com.myretail.webservice.product.dto.Price
import com.myretail.webservice.product.dto.Product
import com.myretail.webservice.product.dto.Status
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification


class ProductControllerIT extends Specification {
    @Shared
    String host
    @Shared
    int port
    @Shared
    String urlBase

    def setupSpec() {
        if (System.getProperty("checkDockerEnvForHost")) {
            host = ExtendedDockerClientImpl.dockerHostIP
        } else {
            host = System.getProperty("integrationTest.servlet.host") ?: "localhost"
        }
        port = Integer.parseInt(System.getProperty("integrationTest.servlet.port") ?: "8080")
        urlBase = "http://${host}:${port}"
    }

    def "the server is up and running"() {
        when:
        RestTemplate restTemplate = new RestTemplate();
        Status status = null;
        try {
            status = restTemplate.getForObject("${urlBase}/status", Status)
        } catch (Exception e) {
            println "unable to access the app server @ ${urlBase}/status are the environment variables set right?"
            e.printStackTrace()
        }

        then:
        status
    }

    def "create some product prices"() {
        when:
        RestTemplate restTemplate = new RestTemplate();
        Price body = new Price(value: price, currency_code: currencyCode)

        restTemplate.put("$urlBase/product/${productId}/current_price", body)

        Product product = restTemplate.getForObject("$urlBase/product/${productId}", Product)
        then:
        noExceptionThrown()
        product.id == productId
        product.current_price.value == price
        product.current_price.currency_code == currencyCode
        where:
        productId | price  | currencyCode
        "1"       | 50.00d | CurrencyCode.USD
        "1"       | 50.00d | CurrencyCode.USD
        "5"       | 50.00d | CurrencyCode.CAN
    }
}
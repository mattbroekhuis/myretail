package com.myretail.webservice.product.controller

import com.myretail.framework.docker.ExtendedDockerClientImpl
import com.myretail.webservice.product.enumeration.CurrencyCode
import com.myretail.webservice.product.dto.Price
import com.myretail.webservice.product.dto.Product
import com.myretail.webservice.product.dto.Status
import groovy.json.JsonSlurper
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
    @Shared
    List<String> knownRemoteProductIds

    def setupSpec() {
        if (System.getProperty("checkDockerEnvForHost")) {
            host = ExtendedDockerClientImpl.dockerHostIP
        } else {
            host = System.getProperty("integrationTest.servlet.host") ?: "localhost"
        }
        port = Integer.parseInt(System.getProperty("integrationTest.servlet.port") ?: "8080")
        urlBase = "http://${host}:${port}"

        //grab some product ids
        knownRemoteProductIds = new JsonSlurper().parseText("https://www.tgtappdata.com/v1/products/list?searchTerm=big+lebowski".toURL().text).products.collect {
            it.tcin
        }

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
        given:
        Collections.shuffle(knownRemoteProductIds)
        String productId = knownRemoteProductIds[0]
        when:
        RestTemplate restTemplate = new RestTemplate();

        Price body = new Price(value: price, currency_code: currencyCode)

        restTemplate.put("$urlBase/product/${productId}/current_price", body)

        Product product = restTemplate.getForObject("$urlBase/product/${productId}", Product)
        then:
        noExceptionThrown()
        product.productId == productId
        product.current_price.value == price
        product.current_price.currency_code == currencyCode
        product.name
        where:
        price  | currencyCode
        50.00d | CurrencyCode.USD
        50.00d | CurrencyCode.USD  //idempotent
        80.00d | CurrencyCode.CAN
    }


    def "populate all for demo"() {
        when:
        RestTemplate restTemplate = new RestTemplate();
        knownRemoteProductIds.each {
            Price body = new Price(value: 100, currency_code: CurrencyCode.USD)

            restTemplate.put("$urlBase/product/${it}/current_price", body)
        }

        then:
        noExceptionThrown()

    }
}
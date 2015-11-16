package com.myretail.webservice.product.remote

import com.myretail.framework.exception.NotFound404Exception
import spock.lang.Specification
import spock.lang.Unroll


class RemoteProductApiSpec extends Specification {


    def "happy json"() {
        given:
        RemoteProductApi api = new RemoteProductApi(jsonForUrl: { responseFromServer })
        when:
        String title = api.productTitle("1234")
        then:
        title == "lebowski"
        where:
        responseFromServer = '''[{"title":"lebowski", "anotherAttribute":"something_else"}]'''
    }

    @Unroll
    def "bad path #label yields 404 exception"() {
        given:
        RemoteProductApi api = new RemoteProductApi(jsonForUrl: { responseFromServer })
        when:
        api.productTitle("1234")
        then:
        thrown(NotFound404Exception)
        where:
        label               | responseFromServer
        "not a list"        | '''{"title":"lebowski", "anotherAttribute":"something_else"}'''
        "not found message" | '''{"message": "no products found"}'''

    }


}
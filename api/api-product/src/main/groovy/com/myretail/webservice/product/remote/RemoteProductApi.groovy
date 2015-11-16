package com.myretail.webservice.product.remote

import com.myretail.framework.exception.NotFound404Exception
import groovy.json.JsonSlurper
import org.springframework.stereotype.Component

@Component
class RemoteProductApi {
    /** url given for demo -> https://www.tgtappdata.com/v1/products/pdp/TCIN/13860428/1375?redsky-api-key=DEV24df89be43a6cca455DEV

     this url specifies a singular id, yet returns a list. Removing the last parameter 1375 changes nothing. Removing the api key seems to have not effect.
     Omitting those parts, and will just yank the first thing off the list
     **/

    def jsonForUrl = { String url ->
        url.toURL().text
    }

    String productTitle(String productId) {
        String jsonText = jsonForUrl("https://www.tgtappdata.com/v1/products/pdp/TCIN/${productId}")
        def slurper = new JsonSlurper().parseText(jsonText)

        //my opinion -- should throw 404 status, or if its a list should return empty list but instead gives 200 with message
        if ((slurper.message && slurper.message == "no products found") || !(slurper instanceof List) || !((List) slurper)[0]) {
            throw new NotFound404Exception()
        }
        slurper[0].title
    }
}

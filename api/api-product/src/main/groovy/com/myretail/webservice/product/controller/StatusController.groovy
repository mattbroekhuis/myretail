package com.myretail.webservice.product.controller

import com.myretail.webservice.product.dto.Status
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/status")
class StatusController {
    //here i'd make an actual heartbeat call out to cassandra to verify it's up.
    // but now i see this https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html#_auto_configured_healthindicators
    // which would be better.
    @RequestMapping
    public Status get() {
        Status status = new Status()
        status.databaseOkay = true
        status.systemOkay = true
        status.dbResponseTime = 10
        status
    }
}

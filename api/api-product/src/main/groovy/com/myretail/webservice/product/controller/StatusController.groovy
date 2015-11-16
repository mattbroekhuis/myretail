package com.myretail.webservice.product.controller

import com.myretail.webservice.product.dto.Status
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController("/status")
class StatusController {
    @RequestMapping
    public Status get() {
        Status status = new Status()
        status.databaseOkay = true
        status.systemOkay = true
        status.dbResponseTime = 10
        status
    }
}

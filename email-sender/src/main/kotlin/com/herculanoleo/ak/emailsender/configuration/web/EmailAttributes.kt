package com.herculanoleo.ak.emailsender.configuration.web

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "email")
class EmailAttributes {
    var enabled: Boolean = false
    var from: String = ""
    var personalName: String = ""
}

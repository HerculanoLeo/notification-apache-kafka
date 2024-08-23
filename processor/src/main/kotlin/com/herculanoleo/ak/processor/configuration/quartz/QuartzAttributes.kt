package com.herculanoleo.ak.processor.configuration.quartz

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.quartz")
data class QuartzAttributes(
    var cron: Cron = Cron(),
)

data class Cron(
    var notificationResend: String = "0 0/1 * * * ?"
)

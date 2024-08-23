package com.herculanoleo.ak.processor.configuration.quartz

import org.quartz.JobDetail
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.CronTriggerFactoryBean

@Configuration
class TriggerFactories {

    @Bean
    fun CronTriggerFactoryBean(
        @Qualifier("notificationResendJobDetails") jobDetail: JobDetail,
        att: QuartzAttributes
    ) = cronTrigger(jobDetail, att.cron.notificationResend)

    private fun cronTrigger(job: JobDetail, cron: String): CronTriggerFactoryBean {
        val trigger = CronTriggerFactoryBean()
        trigger.setJobDetail(job)
        trigger.setCronExpression(cron)
        return trigger
    }
}

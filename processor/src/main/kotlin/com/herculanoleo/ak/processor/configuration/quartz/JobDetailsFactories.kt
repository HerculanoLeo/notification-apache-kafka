package com.herculanoleo.ak.processor.configuration.quartz

import com.herculanoleo.ak.processor.jobs.NotificationResendJob
import org.quartz.Job
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.quartz.JobDetailFactoryBean
import kotlin.reflect.KClass

@Configuration
class JobDetailsFactories {
    @Bean("notificationResendJobDetails")
    fun notificationResendJobDetails(): JobDetailFactoryBean {
        return build(NotificationResendJob::class)
    }

    fun <T : Job> build(clazz: KClass<T>): JobDetailFactoryBean {
        val jobDetailFactory = JobDetailFactoryBean()
        jobDetailFactory.setJobClass(clazz.java)
        jobDetailFactory.setDurability(true)
        return jobDetailFactory
    }
}

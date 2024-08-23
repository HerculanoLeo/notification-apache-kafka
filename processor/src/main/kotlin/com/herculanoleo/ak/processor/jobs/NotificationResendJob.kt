package com.herculanoleo.ak.processor.jobs

import com.herculanoleo.ak.processor.models.dtos.NotificationRequest
import com.herculanoleo.ak.processor.service.NotificationService
import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext

@DisallowConcurrentExecution
class NotificationResendJob(private val notificationService: NotificationService) : Job {
    override fun execute(context: JobExecutionContext) {
        val notifications = notificationService.findAll(NotificationRequest(null, null, 3, listOf("R", "E")))
        for (notification in notifications) {
            notificationService.resend(notification.id)
        }
    }
}

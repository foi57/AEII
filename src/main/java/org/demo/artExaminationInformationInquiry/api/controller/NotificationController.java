package org.demo.artExaminationInformationInquiry.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.demo.artExaminationInformationInquiry.api.entity.Notification;
import org.demo.artExaminationInformationInquiry.api.service.INotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-25
 */
@Slf4j
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    INotificationService notificationService;
    NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @GetMapping("/select")
    public Page<Notification> selectUserNotifications(Long id, int page, int size) {
        Page<Notification> notificationsPage= notificationService.selectUserNotifications(id, page, size);
        log.debug("{}", notificationsPage);
        return notificationsPage;
    }

}

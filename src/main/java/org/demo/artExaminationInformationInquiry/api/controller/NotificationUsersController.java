package org.demo.artExaminationInformationInquiry.api.controller;

import org.demo.artExaminationInformationInquiry.api.service.INotificationUsersService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-26
 */
@RestController
@RequestMapping("/api/notificationUsers")
public class NotificationUsersController {
    INotificationUsersService notificationUsersService;
    NotificationUsersController(INotificationUsersService notificationUsersService){
        this.notificationUsersService = notificationUsersService;
    }
    @PutMapping("/read")
    public Boolean read(Long notificationId, Long userId){
        return notificationUsersService.readNotification(notificationId,userId);
    }
    @GetMapping("/unread")
    public int unread( Long userId){
        return notificationUsersService.selectUnread(userId).size();
    }
    
}

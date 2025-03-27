package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Notification;
import org.demo.artExaminationInformationInquiry.api.mapper.NotificationMapper;
import org.demo.artExaminationInformationInquiry.api.service.INotificationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.demo.artExaminationInformationInquiry.api.service.INotificationUsersService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-25
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements INotificationService {

    INotificationUsersService notificationUsersService;

    NotificationServiceImpl(INotificationUsersService notificationUsersService){
        this.notificationUsersService = notificationUsersService;
    }
    @Override
    public Page<Notification> selectUserNotifications(Long userId, int page, int size) {
        Page<Notification> page1 = new Page<>(page, size);
        Page<Notification> page2 = lambdaQuery().eq(Notification::getUserId, userId).orderByDesc(Notification::getCreateTime).page(page1);
        page2.getRecords().forEach(notification -> {
            notification.setIsRead(notificationUsersService.selectRead(notification.getNotificationId(),userId));
        });
        return page2;
    }

}

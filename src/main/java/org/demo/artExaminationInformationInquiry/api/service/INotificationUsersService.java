package org.demo.artExaminationInformationInquiry.api.service;

import org.demo.artExaminationInformationInquiry.api.entity.NotificationUsers;
import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.artExaminationInformationInquiry.api.entity.UniversityCollection;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-26
 */
public interface INotificationUsersService extends IService<NotificationUsers> {
    Boolean selectRead(Long notificationId, Long userId);
    Boolean readNotification(Long notificationId, Long userId);
    List<NotificationUsers> selectUnread(Long userId);
}

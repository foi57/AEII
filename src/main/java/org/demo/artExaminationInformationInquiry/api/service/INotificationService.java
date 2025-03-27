package org.demo.artExaminationInformationInquiry.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Notification;
import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.artExaminationInformationInquiry.api.entity.Users;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-25
 */
public interface INotificationService extends IService<Notification> {
    Page<Notification> selectUserNotifications(Long userId, int page, int size);

}

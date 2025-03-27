package org.demo.artExaminationInformationInquiry.api.service.impl;

import org.demo.artExaminationInformationInquiry.api.entity.NotificationUsers;
import org.demo.artExaminationInformationInquiry.api.entity.UniversityCollection;
import org.demo.artExaminationInformationInquiry.api.mapper.NotificationUsersMapper;
import org.demo.artExaminationInformationInquiry.api.service.INotificationUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-26
 */
@Service
public class NotificationUsersServiceImpl extends ServiceImpl<NotificationUsersMapper, NotificationUsers> implements INotificationUsersService {


    @Override
    public Boolean selectRead(Long notificationId, Long userId) {
        return lambdaQuery().eq(NotificationUsers::getNotificationId,notificationId).eq(NotificationUsers::getUserId,userId).one().getIsRead();
    }

    @Override
    public Boolean readNotification(Long notificationId, Long userId) {
        return lambdaUpdate().set(NotificationUsers::getIsRead,true).eq(NotificationUsers::getNotificationId,notificationId).eq(NotificationUsers::getUserId,userId).update();
    }

    @Override
    public List<NotificationUsers> selectUnread(Long userId) {
        return lambdaQuery().eq(NotificationUsers::getUserId,userId).eq(NotificationUsers::getIsRead,false).list();
    }

}

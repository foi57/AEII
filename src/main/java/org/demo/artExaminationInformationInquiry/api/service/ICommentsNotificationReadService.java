package org.demo.artExaminationInformationInquiry.api.service;

import org.demo.artExaminationInformationInquiry.api.entity.CommentsNotificationRead;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-06
 */
public interface ICommentsNotificationReadService extends IService<CommentsNotificationRead> {
    Boolean insert(CommentsNotificationRead commentsNotificationRead);
    Boolean update(CommentsNotificationRead commentsNotificationRead);
}

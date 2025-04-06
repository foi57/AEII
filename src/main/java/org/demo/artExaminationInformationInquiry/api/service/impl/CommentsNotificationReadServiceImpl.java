package org.demo.artExaminationInformationInquiry.api.service.impl;

import org.demo.artExaminationInformationInquiry.api.entity.CommentsNotificationRead;
import org.demo.artExaminationInformationInquiry.api.mapper.CommentsNotificationReadMapper;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsNotificationReadService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-06
 */
@Service
public class CommentsNotificationReadServiceImpl extends ServiceImpl<CommentsNotificationReadMapper, CommentsNotificationRead> implements ICommentsNotificationReadService {

    @Override
    public Boolean insert(CommentsNotificationRead commentsNotificationRead) {
        return save(commentsNotificationRead);
    }

    @Override
    public Boolean update(CommentsNotificationRead commentsNotificationRead) {
        return updateById(commentsNotificationRead);
    }

}

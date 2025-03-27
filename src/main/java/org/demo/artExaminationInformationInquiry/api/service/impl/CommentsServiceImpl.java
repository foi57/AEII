package org.demo.artExaminationInformationInquiry.api.service.impl;

import org.demo.artExaminationInformationInquiry.api.entity.Comments;
import org.demo.artExaminationInformationInquiry.api.mapper.CommentsMapper;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-27
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements ICommentsService {

    @Override
    public Boolean insertComment(Comments comments) {
        return save(comments);
    }
}

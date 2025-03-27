package org.demo.artExaminationInformationInquiry.api.service;

import org.demo.artExaminationInformationInquiry.api.entity.Comments;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-27
 */
public interface ICommentsService extends IService<Comments> {
    Boolean insertComment(Comments comments);
}

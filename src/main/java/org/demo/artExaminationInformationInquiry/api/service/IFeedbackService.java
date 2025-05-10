package org.demo.artExaminationInformationInquiry.api.service;

import org.demo.artExaminationInformationInquiry.api.entity.Feedback;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-29
 */
public interface IFeedbackService extends IService<Feedback> {

    Boolean insertFeedback(Feedback feedback); // 插入反馈信息
    Page<Feedback> selectFeedbackPage(int pageNum, int pageSize); // 分页查询反馈信息
}

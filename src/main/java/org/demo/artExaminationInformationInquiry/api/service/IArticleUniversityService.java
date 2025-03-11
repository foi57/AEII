package org.demo.artExaminationInformationInquiry.api.service;

import org.demo.artExaminationInformationInquiry.api.entity.ArticleUniversity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-08
 */
public interface IArticleUniversityService extends IService<ArticleUniversity> {
    List<ArticleUniversity> selectArticleUniversityListByArticleId(Long articleId);
}

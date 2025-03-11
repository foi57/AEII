package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.demo.artExaminationInformationInquiry.api.entity.ArticleUniversity;
import org.demo.artExaminationInformationInquiry.api.mapper.ArticleUniversityMapper;
import org.demo.artExaminationInformationInquiry.api.service.IArticleUniversityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-08
 */
@Service
public class ArticleUniversityServiceImpl extends ServiceImpl<ArticleUniversityMapper, ArticleUniversity> implements IArticleUniversityService {
    public List<ArticleUniversity> selectArticleUniversityListByArticleId(Long articleId){
        QueryWrapper<ArticleUniversity> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("article_id",articleId);
        return baseMapper.selectList(queryWrapper);
    }
}

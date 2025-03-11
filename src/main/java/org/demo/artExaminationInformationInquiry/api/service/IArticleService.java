package org.demo.artExaminationInformationInquiry.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-07
 */
public interface IArticleService extends IService<Article> {
     void insertArticle(Article article, String[] affiliatedUniversities);
     Page<Article> selectArticleListByUserId(String title,Long id, Integer pageNum, Integer pageSize);
     Article selectArticleById(Long id);
     Page<Article> selectArticleListByType(String title,String type,Integer pageNum, Integer pageSize);
     void updateArticle(Map<String,Object> articleMap);
     Page<Article> selectArticleListByName(String name,Integer pageNum, Integer pageSize);
}

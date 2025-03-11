package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Article;
import org.demo.artExaminationInformationInquiry.api.entity.ArticleUniversity;
import org.demo.artExaminationInformationInquiry.api.entity.Users;
import org.demo.artExaminationInformationInquiry.api.mapper.ArticleMapper;
import org.demo.artExaminationInformationInquiry.api.mapper.ArticleUniversityMapper;
import org.demo.artExaminationInformationInquiry.api.mapper.UsersMapper;
import org.demo.artExaminationInformationInquiry.api.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-07
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {
    private final UsersMapper usersMapper;
    private final ArticleMapper articleMapper;
    private final ArticleUniversityMapper articleUniversityMapper;
    ArticleServiceImpl(ArticleMapper articleMapper,ArticleUniversityMapper articleUniversityMapper,UsersMapper usersMapper){
        this.articleMapper=articleMapper;
        this.articleUniversityMapper=articleUniversityMapper;
        this.usersMapper=usersMapper;
    }
    @Transactional
    @Override
    public void insertArticle(Article article, String[] affiliatedUniversities) {
        articleMapper.insert(article);
        Long articleId=article.getArticleId();
        if (articleId!=null && affiliatedUniversities!=null){
           List<ArticleUniversity> articleUniversities= Arrays.stream(affiliatedUniversities).map(universityName -> new ArticleUniversity(articleId, universityName)).toList();
           articleUniversityMapper.batchInsert(articleUniversities);
        }
    }

    @Transactional
    @Override
    public Page<Article> selectArticleListByUserId(String title,Long id, Integer pageNum, Integer pageSize) {

        Page<Article> page=new Page<>(pageNum,pageSize);
         page = lambdaQuery()
                .eq(Article::getArticleSource, id)
                 .like(Article::getArticleTitle, title)
                .orderByDesc(Article::getArticleReleased) // 新增排序条件
                .page(page);
        page.getRecords().forEach(article -> {
            Users user = usersMapper.selectById(article.getArticleSource());
            if (user != null) {
                article.setUserName(user.getUsersName());  // 需要在Article实体类添加userName字段
            }

        });

        return page;
    }

    @Override
    public Article selectArticleById(Long id) {
        Article article=  articleMapper.selectById(id);
        if (article!=null){
            Users user = usersMapper.selectById(article.getArticleSource());
            if (user!= null) {
                article.setUserName(user.getUsersName());  // 需要在Article实体类添加userName字段
            }
            QueryWrapper<ArticleUniversity> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("article_id",article.getArticleId());
            List<ArticleUniversity> articleUniversities=articleUniversityMapper.selectList(queryWrapper);
            article.setAffiliatedUniversities(articleUniversities);
        }
        return article;
    }

    @Override
    public Page<Article> selectArticleListByType(String title,String type, Integer pageNum, Integer pageSize) {
        Page<Article> page=new Page<>(pageNum,pageSize);
        page = lambdaQuery()
                .like(Article::getArticleType, type)
                .like(Article::getArticleTitle, title)
                .orderByDesc(Article::getArticleReleased) // 新增排序条件
                .page(page);
        page.getRecords().forEach(article -> {
            Users user = usersMapper.selectById(article.getArticleSource());
            if (user != null) {
                article.setUserName(user.getUsersName());  // 需要在Article实体类添加userName字段
            }

        });
        return page;
    }

    @Transactional
    @Override
    public void updateArticle(Map<String,Object> articleMap) {
        Long articleId = Long.parseLong(articleMap.get("articleId").toString());
        Article oldArticle=articleMapper.selectById(articleId);
        if (oldArticle!=null){
            oldArticle.setArticleTitle((String) articleMap.get("articleTitle"));
            oldArticle.setArticleContent(articleMap.get("articleContent").toString());
            oldArticle.setArticleType(articleMap.get("articleType").toString());
            articleUniversityMapper.delete(new QueryWrapper<ArticleUniversity>().eq("article_id",articleId));
            List<?> affiliatedUniversities = (List<?>) articleMap.get("affiliatedUniversities");
            if (!affiliatedUniversities.isEmpty()){
                List<ArticleUniversity> articleUniversities = affiliatedUniversities.stream()
                        .map(universityName -> new ArticleUniversity(articleId, universityName.toString()))
                        .collect(Collectors.toList());

                articleUniversityMapper.batchInsert(articleUniversities);
            }
        }
    }

    @Override
    public Page<Article> selectArticleListByName(String name, Integer pageNum, Integer pageSize) {
        Page<Article> page=new Page<>(pageNum,pageSize);
        page = lambdaQuery()
               .like(Article::getArticleTitle, name)
               .orderByDesc(Article::getArticleReleased) // 新增排序条件
               .page(page);
        return page;
    }
}

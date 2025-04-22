package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.*;
import org.demo.artExaminationInformationInquiry.api.mapper.ArticleMapper;
import org.demo.artExaminationInformationInquiry.api.mapper.ArticleUniversityMapper;
import org.demo.artExaminationInformationInquiry.api.mapper.UniversityCollectionMapper;
import org.demo.artExaminationInformationInquiry.api.mapper.UsersMapper;
import org.demo.artExaminationInformationInquiry.api.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final UniversityCollectionServiceImpl universityCollectionService;
    private final NotificationServiceImpl notificationService;
    private final UniversityServiceImpl universityService;
    private final NotificationUsersServiceImpl notificationUsersService;
    ArticleServiceImpl(ArticleMapper articleMapper,ArticleUniversityMapper articleUniversityMapper,UsersMapper usersMapper,UniversityCollectionServiceImpl universityCollectionService,NotificationServiceImpl notificationService,UniversityServiceImpl universityService
    ,NotificationUsersServiceImpl notificationUsersService){
        this.articleMapper=articleMapper;
        this.articleUniversityMapper=articleUniversityMapper;
        this.usersMapper=usersMapper;
        this.universityCollectionService=universityCollectionService;
        this.notificationService=notificationService;
        this.universityService=universityService;
        this.notificationUsersService=notificationUsersService;
    }
    @Transactional
    @Override
    public void insertArticle(Article article, String[] affiliatedUniversities) {
        articleMapper.insert(article);
        Long articleId=article.getArticleId();
        List<ArticleUniversity> articleUniversities = null;
        if (articleId!=null && affiliatedUniversities!=null){
           articleUniversities= Arrays.stream(affiliatedUniversities).map(universityName -> new ArticleUniversity(articleId, universityName)).toList();
           articleUniversityMapper.batchInsert(articleUniversities);
        }
        if (affiliatedUniversities != null) {
            List<Long> UniversityIds= Arrays.stream(affiliatedUniversities).map(name -> universityService.selectUniversityByName(name).getUniversityId()).toList();
            List<UniversityCollection> universityCollections = universityCollectionService.lambdaQuery().in(UniversityCollection::getUniversityId, UniversityIds).list();
            List<Notification> notifications = universityCollections.stream()
                    .map(uc -> new Notification()
                            .setUserId(uc.getUsersId())
                            .setContent(article.getArticleTitle())
                            .setLink("/article/detail/" + article.getArticleId())
                            .setCategory("article"))
                    .toList();

            // 修改后正确代码：
            List<Notification> savedNotifications = new ArrayList<>();
            for (Notification notification : notifications) {
                notificationService.save(notification); // 单条保存以获取主键
                savedNotifications.add(notification);
            }

            List<NotificationUsers> notificationUsers = savedNotifications.stream()
                   .map(n -> new NotificationUsers()
                           .setNotificationId(n.getNotificationId()) // 此时已获得数据库生成的主键
                           .setUserId(n.getUserId()))
                   .toList();
            notificationUsersService.saveBatch(notificationUsers);
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
        QueryWrapper<Article> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("article_id", id)
            .select("article_id", "article_title", "article_content",
                    "article_released", "article_source", "article_type",
                    "attachments"); // 明确包含attachments字段
        Article article = articleMapper.selectOne(queryWrapper1);

        if (article != null) {
            // 补充附件信息处理
            if (article.getAttachments() == null) {
                article.setAttachments(Collections.emptyList());
            }
            // 补充用户名信息处理
            Users user = usersMapper.selectById(article.getArticleSource());
            if (user!= null) {
                article.setUserName(user.getUsersName());
            }
            // 保持原有关联院校查询逻辑
            QueryWrapper<ArticleUniversity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("article_id", article.getArticleId());
            List<ArticleUniversity> articleUniversities = articleUniversityMapper.selectList(queryWrapper);
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
            oldArticle.setAttachments((List<Map<String, String>>) articleMap.get("attachments"));
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

    @Override
    public Page<Article> selectArticleListByUniversityName(String universityName,String articleType, Integer pageNum, Integer pageSize) {
        return lambdaQuery()
                // 使用 exists 子查询替代 ID列表查询
                .exists(
                        "SELECT 1 FROM article_university au " +
                                "WHERE au.article_id = article.article_id " +  // 关联主表字段
                                "AND au.university_name = {0}",
                        universityName
                )
                .like(Article::getArticleType, articleType)
                .orderByDesc(Article::getArticleReleased)
                .page(new Page<>(pageNum, pageSize));
    }

    @Override
    public Page<Article> advancedSearchArticles(String keyword, String type, 
                                              Date startDate, Date endDate, 
                                              Integer pageNum, Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        
        // 构建查询条件
        page = lambdaQuery()
                // 如果有指定类型，则添加类型过滤
                .like(type != null && !type.isEmpty(), Article::getArticleType, type)
                // 在标题或内容中搜索关键词
                .and(keyword != null && !keyword.isEmpty(), wrapper -> wrapper
                    .like(Article::getArticleTitle, keyword)
                    .or()
                    .like(Article::getArticleContent, keyword)
                )
                // 添加时间范围过滤
                .ge(startDate != null, Article::getArticleReleased, startDate)
                .le(endDate != null, Article::getArticleReleased, endDate)
                .orderByDesc(Article::getArticleReleased)
                .page(page);
        
        // 补充用户信息
        page.getRecords().forEach(article -> {
            Users user = usersMapper.selectById(article.getArticleSource());
            if (user != null) {
                article.setUserName(user.getUsersName());
            }
        });
        
        return page;
    }
}

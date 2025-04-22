package org.demo.artExaminationInformationInquiry.api.service.impl;

import org.demo.artExaminationInformationInquiry.api.entity.Article; // 假设需要关联查询文章信息
import org.demo.artExaminationInformationInquiry.api.entity.HotArticle;
import org.demo.artExaminationInformationInquiry.api.mapper.ArticleMapper; // 假设需要关联查询文章信息
import org.demo.artExaminationInformationInquiry.api.mapper.HotArticleMapper;
import org.demo.artExaminationInformationInquiry.api.service.IHotArticleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  热门文章 服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-20
 */
@Service
@Slf4j
public class HotArticleServiceImpl extends ServiceImpl<HotArticleMapper, HotArticle> implements IHotArticleService {

    @Autowired
    private ArticleMapper articleMapper; // 注入 ArticleMapper 以获取文章标题等信息

    @Override
    public List<HotArticle> selectHotArticlesWithDetails() {
        QueryWrapper<HotArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort_order"); // 按 sort_order 排序
        List<HotArticle> hotArticles = baseMapper.selectList(queryWrapper);

       
        hotArticles = hotArticles.stream().map(hotArticle -> hotArticle.setArticleTitle(articleMapper.selectById(hotArticle.getArticleId()).getArticleTitle())).collect(Collectors.toList());

        return hotArticles;
    }

    @Override
    @Transactional
    public boolean addHotArticle(Long articleId) {
        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            log.warn("尝试添加热门文章失败：文章不存在, articleId={}", articleId);
            return false;
        }
        // 检查是否已经是热门文章
        QueryWrapper<HotArticle> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("article_id", articleId);
        if (baseMapper.selectCount(checkWrapper) > 0) {
             log.warn("尝试添加热门文章失败：该文章已是热门文章, articleId={}", articleId);
             return false; // 或者返回特定错误信息
        }

        HotArticle hotArticle = new HotArticle();
        hotArticle.setArticleId(articleId);

        // 设置排序值，默认为最大值 + 1
        Integer maxSortOrder = baseMapper.getMaxSortOrder();
        int newSortOrder = (maxSortOrder == null) ? 1 : maxSortOrder + 1;
        hotArticle.setSortOrder(newSortOrder);
        // hotArticle.setCreatedAt(LocalDateTime.now()); // 如果有创建时间字段

        return baseMapper.insert(hotArticle) > 0;
    }

    @Override
    public boolean deleteHotArticle(Long hotArticleId) {
        // 可以先根据 hotArticleId 查询，再根据 articleId 删除，取决于业务逻辑
        return baseMapper.deleteById(hotArticleId) > 0;
    }

    @Override
    @Transactional
    public boolean moveHotArticle(Long hotArticleId, String direction) {
        HotArticle current = baseMapper.selectById(hotArticleId);
        if (current == null || current.getSortOrder() == null) {
            log.warn("尝试移动不存在或 sortOrder 为空的热门文章: id={}", hotArticleId);
            return false;
        }

        HotArticle adjacent = null;
        if ("up".equalsIgnoreCase(direction)) {
            adjacent = baseMapper.findPrevious(current.getSortOrder());
        } else if ("down".equalsIgnoreCase(direction)) {
            adjacent = baseMapper.findNext(current.getSortOrder());
        }

        if (adjacent == null) {
            log.info("热门文章 {} 无法移动 '{}', 已在顶部或底部。", hotArticleId, direction);
            return false;
        }

        // 交换 sortOrder
        Integer tempOrder = current.getSortOrder();
        current.setSortOrder(adjacent.getSortOrder());
        adjacent.setSortOrder(tempOrder);

        boolean updateCurrent = baseMapper.updateById(current) > 0;
        boolean updateAdjacent = baseMapper.updateById(adjacent) > 0;

        if (!updateCurrent || !updateAdjacent) {
            log.error("移动热门文章时更新数据库失败: currentId={}, adjacentId={}", current.getId(), adjacent.getId());
            // 抛出异常让事务回滚
            throw new RuntimeException("移动热门文章时更新数据库失败");
        }
        return true;
    }
}

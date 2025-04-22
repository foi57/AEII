package org.demo.artExaminationInformationInquiry.api.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.demo.artExaminationInformationInquiry.api.entity.Article;
import org.demo.artExaminationInformationInquiry.api.entity.HotArticle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  热门文章 Mapper 接口
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-20
 */
@Mapper
public interface HotArticleMapper extends BaseMapper<HotArticle> {

    /**
     * 获取当前最大的排序值
     * @return 最大排序值，如果表中没有数据则返回 null
     */
    @Select("SELECT MAX(sort_order) FROM hot_article")
    Integer getMaxSortOrder();

    /**
     * 查找上一个热门文章（用于上移）
     * @param sortOrder 当前文章的排序值
     * @return 上一个热门文章实体
     */
    @Select("SELECT * FROM hot_article WHERE sort_order < #{sortOrder} ORDER BY sort_order DESC LIMIT 1")
    HotArticle findPrevious(int sortOrder);

    /**
     * 查找下一个热门文章（用于下移）
     * @param sortOrder 当前文章的排序值
     * @return 下一个热门文章实体
     */
    @Select("SELECT * FROM hot_article WHERE sort_order > #{sortOrder} ORDER BY sort_order ASC LIMIT 1")
    HotArticle findNext(int sortOrder);

}


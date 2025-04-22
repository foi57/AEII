package org.demo.artExaminationInformationInquiry.api.service;

import org.demo.artExaminationInformationInquiry.api.entity.HotArticle;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * <p>
 *  热门文章 服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-20
 */
public interface IHotArticleService extends IService<HotArticle> {

    /**
     * 获取排序后的热门文章列表（可包含文章详情）
     * @return 热门文章列表
     */
    List<HotArticle> selectHotArticlesWithDetails(); // 或者返回包含文章信息的 DTO 列表

    /**
     * 添加热门文章
     * @param articleId 要添加的文章 ID
     * @return 是否添加成功
     */
    boolean addHotArticle(Long articleId);

    /**
     * 删除热门文章
     * @param hotArticleId 热门文章记录的 ID
     * @return 是否删除成功
     */
    boolean deleteHotArticle(Long hotArticleId); // 参数可能是 hotArticleId 或 articleId

    /**
     * 移动热门文章排序
     * @param hotArticleId 要移动的热门文章记录 ID
     * @param direction 移动方向 ("up" 或 "down")
     * @return 是否移动成功
     */
    boolean moveHotArticle(Long hotArticleId, String direction);
}

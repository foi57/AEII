package org.demo.artExaminationInformationInquiry.api.controller;

import org.demo.artExaminationInformationInquiry.api.entity.HotArticle;
import org.demo.artExaminationInformationInquiry.api.service.IHotArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  热门文章 前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-20
 */
@RestController
@RequestMapping("/api/hotArticle")
public class HotArticleController {

    
    private IHotArticleService hotArticleService;

    HotArticleController(IHotArticleService hotArticleService) {
        this.hotArticleService = hotArticleService;
    }
    /**
     * 获取热门文章列表
     * @return
     */
    @GetMapping("/list")
    public List<HotArticle> getHotArticleList() {
        // 这里可以调用 service 层获取带详情的方法
        return hotArticleService.selectHotArticlesWithDetails();
    }

    /**
     * 添加热门文章
     * @param payload 包含 articleId 的请求体
     * @return
     */
    @PostMapping("/add")
    public boolean addHotArticle(@RequestBody Map<String, Long> payload) {
         Long articleId = payload.get("articleId");
         if (articleId == null) {
             // 处理错误，例如返回 Bad Request
             return false;
         }
        return hotArticleService.addHotArticle(articleId);
    }

    /**
     * 删除热门文章
     * @param id 热门文章记录的 ID
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteHotArticle(@PathVariable("id") Long id) {
        return hotArticleService.deleteHotArticle(id);
    }

    /**
     * 移动热门文章排序
     * @param id 热门文章记录的 ID
     * @param payload 包含 direction 的请求体
     * @return
     */
    @PutMapping("/move/{id}")
    public boolean moveHotArticle(@PathVariable("id") Long id, @RequestBody Map<String, String> payload) {
        String direction = payload.get("direction");
         if (direction == null || (!"up".equalsIgnoreCase(direction) && !"down".equalsIgnoreCase(direction))) {
             // 处理错误
             return false;
         }
        return hotArticleService.moveHotArticle(id, direction);
    }
}

package org.demo.artExaminationInformationInquiry.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.demo.artExaminationInformationInquiry.api.entity.Article;
import org.demo.artExaminationInformationInquiry.api.service.IArticleService;
import org.demo.artExaminationInformationInquiry.api.service.IArticleUniversityService;
import org.slf4j.Logger;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-07
 */
@RestController
@RequestMapping("/api/article")
public class ArticleController {

    IArticleService articleService;
    IArticleUniversityService articleUniversityService;
    ArticleController(IArticleService articleService, IArticleUniversityService articleUniversityService) {
        this.articleService = articleService;
        this.articleUniversityService = articleUniversityService;
    }

    Logger logger=org.slf4j.LoggerFactory.getLogger(ArticleController.class);
    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestPart("file") MultipartFile file) {
        logger.debug("uploadFile: {}", file);
        try {
            // 获取项目根路径
            String uploadDir = System.getProperty("user.dir") + "/file/article/images/";

            // 创建目录（如果不存在）
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            InputStream inputStream = file.getInputStream();
            String hash = DigestUtils.md5Hex(inputStream);  // 直接对文件内容哈希
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String fileName = hash + "." + ext;

            // 检查文件是否已存在
            Path targetPath = Paths.get(uploadDir, fileName);
            if (!Files.exists(targetPath)) {
                Files.copy(file.getInputStream(), targetPath);
            }


            return ResponseEntity.ok().body(
                    Map.of("url", "/article/images/" + fileName)
            );

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "文件上传失败"));
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertArticle(@RequestBody Map<String,Object> articleMap) {
        logger.debug("insertArticle: {}", articleMap);
        Article article1=new Article();
        article1.setArticleTitle((String) articleMap.get("articleTitle"));
        article1.setArticleContent((String) articleMap.get("articleContent"));
        article1.setArticleReleased(LocalDate.now());
        article1.setArticleSource((Integer) articleMap.get("articleSource"));
        article1.setArticleType((String) articleMap.get("articleType"));
        article1.setAttachments((List<Map<String, String>>) articleMap.get("attachments"));
        List<?> affiliatedUniversitiesList = (List<?>) articleMap.get("affiliatedUniversities");
        String[] affiliatedUniversities=null;
        if (affiliatedUniversitiesList != null && !affiliatedUniversitiesList.isEmpty()) {
             affiliatedUniversities = (affiliatedUniversitiesList)
                    .stream()
                    .map(Object::toString)
                    .toArray(String[]::new);
        }


        try {
            articleService.insertArticle(article1,affiliatedUniversities);
            return ResponseEntity.ok("ok");
        }catch (Exception e){
            logger.error(e.toString());
            return ResponseEntity.status(400).body("插入失败");
        }

    }
    @PostMapping("/select")
    public ResponseEntity<Page<Article>> selectArticle(@RequestBody Map<String,Object> article){
        String articleTitle=(String) article.get("articleTitle");
        long universityId= -1L;
        String articleType="";
        int pageNum=1;
        int pageSize=99999999;
        Date startData =null;
        Date endData =null;
        if (article.get("articleSource") !=null) {
            universityId = ((Number) article.get("articleSource")).longValue();
        }
        if (article.get("articleType")!=null) {
            articleType = (String) article.get("articleType");
        }
        if (article.get("pageNum")!=null) {
            pageNum = ((Number) article.get("pageNum")).intValue();    
        }
        if (article.get("pageSize")!=null) {
            pageSize = ((Number) article.get("pageSize")).intValue(); 
        }
        if (article.get("startDate")!=null) {
            startData = (Date) article.get("startDate"); 
        }
        if (article.get("endDate")!=null) {
            endData = (Date) article.get("endDate"); 
        }
        logger.debug("articleTitle: {} universityId: {} articleType: {} pageNum: {} pageSize: {} startData: {} endData: {}", articleTitle, universityId, articleType, pageNum, pageSize, startData, endData);
        if (articleType.equals("myPublish")){
            return ResponseEntity.ok(articleService.selectArticleListByUserId(articleTitle,universityId,pageNum,pageSize));
        }else {
            return ResponseEntity.ok(articleService.selectArticleListByType(articleTitle,articleType,pageNum,pageSize));
        }
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<Article> selectArticleDetail(@PathVariable Long id) {
        Article article = articleService.selectArticleById(id); // 改用服务层方法
        logger.debug("articleDetail: {}", article);
        return ResponseEntity.ok(article);
    }
    @PostMapping("/update")
    public ResponseEntity<String> updateArticle(@RequestBody Map<String,Object> article){
        logger.debug("article: {}", article);
        articleService.updateArticle(article);
        return null;
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteArticle(@PathVariable("id") Long id){
        articleService.removeById(id);
        return ResponseEntity.ok("ok");
    }
    @GetMapping("/selectArticlesByCategoryUniversityName")
    public Page<Article> selectArticlesByUniversity(@RequestParam String universityName,@RequestParam String articleType,@RequestParam Integer pageNum,@RequestParam Integer pageSize) {
        logger.debug("universityName: {} articleType: {} pageNum: {} pageSize: {}", universityName, articleType, pageNum, pageSize);
        return articleService.selectArticleListByUniversityName(universityName,articleType,pageNum,pageSize);
    }
    @PostMapping("/upload/file")
    public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
        logger.debug("uploadFile: {}", file);
        try {
            // 获取项目根路径
            String uploadDir = System.getProperty("user.dir") + "/file/article/files/";
            // 创建目录（如果不存在）
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String ext = FilenameUtils.getExtension(originalFilename);
            String fileName = originalFilename + UUID.randomUUID().toString() + "." + ext;
            // 保存文件
            Path targetPath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok("/article/files/" + fileName);
        }catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("文件上传失败");
        }
    }
    /**
     * 高级搜索文章
     */
    @GetMapping("/advancedSearch")
    public Page<Article> advancedSearchArticles(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return articleService.advancedSearchArticles(keyword, type, startDate, endDate, pageNum, pageSize);
    }
}

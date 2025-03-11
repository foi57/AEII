package org.demo.artExaminationInformationInquiry.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.demo.artExaminationInformationInquiry.api.entity.Article;
import org.demo.artExaminationInformationInquiry.api.entity.ArticleUniversity;
import org.demo.artExaminationInformationInquiry.api.service.IArticleService;
import org.slf4j.Logger;
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
import java.time.LocalDateTime;
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
    ArticleController(IArticleService articleService) {
        this.articleService = articleService;
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
    public ResponseEntity<String> insertArticle(@RequestBody Map<String,Object> article){
        logger.debug("article: {}", article);
        Article article1=new Article();
        article1.setArticleTitle((String) article.get("articleTitle"));
        article1.setArticleContent((String) article.get("articleContent"));
        article1.setArticleReleased(LocalDate.now());
        article1.setArticleSource((Integer) article.get("articleSource"));
        article1.setArticleType((String) article.get("articleType"));
        String[] affiliatedUniversities = ((List<?>) article.get("affiliatedUniversities"))
                .stream()
                .map(Object::toString)
                .toArray(String[]::new);

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
        Long universityId = ((Number) article.get("articleSource")).longValue();
        String articleType=(String) article.get("articleType");
        int pageNum=(Integer) article.get("pageNum");
        int pageSize=(Integer) article.get("pageSize");
       logger.debug("universityId: {} articleType: {} pageNum: {} pageSize: {}", universityId, articleType, pageNum, pageSize);
        if (articleType.equals("myPublish")){
            return ResponseEntity.ok(articleService.selectArticleListByUserId(articleTitle,universityId,pageNum,pageSize));
        }else {
            return ResponseEntity.ok(articleService.selectArticleListByType(articleTitle,articleType,pageNum,pageSize));
        }
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<Article> selectArticleDetail(@PathVariable("id") Long id){
        return ResponseEntity.ok(articleService.selectArticleById(id));
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

}

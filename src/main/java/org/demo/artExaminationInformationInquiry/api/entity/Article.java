package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-07
 */
@Getter
@Setter
@ToString
@TableName("article")
@ApiModel(value = "Article对象", description = "")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "article_id", type = IdType.AUTO)
    private Long articleId;

    @TableField("article_title")
    private String articleTitle;

    @TableField("article_content")
    private String articleContent;

    @TableField("article_released")
    private LocalDate articleReleased;

    @TableField("article_source")
    private Integer articleSource;

    @TableField("article_type")
    private String articleType;

    @TableField(exist = false) // 表示该字段不存在于数据库表中
    private String userName;

    @TableField(exist = false) // 表示该字段不存在于数据库表中
    private List<ArticleUniversity> affiliatedUniversities;

    @TableLogic
    @TableField(value = "deleted",fill = FieldFill.INSERT)
    private Integer deleted;
}

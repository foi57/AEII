package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
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
    private String articleSource;

    @TableField("article_type")
    private String articleType;

    @TableField("university_id")
    private Long universityId;
}

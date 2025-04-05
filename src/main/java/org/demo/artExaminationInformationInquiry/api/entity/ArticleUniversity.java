package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-08
 */
@Data
@TableName(value =  "article_university",autoResultMap = true)
@ApiModel(value = "ArticleUniversity对象", description = "")
public class ArticleUniversity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("article_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long articleId;

    @TableField("university_name")
    private String universityName;

    @TableField(exist = false)
    private List<String> universityNames; // 用于接收关联院校ID集合

    public ArticleUniversity(Long articleId, String universityName) {
        this.articleId = articleId;
        this.universityName = universityName;
    }
}

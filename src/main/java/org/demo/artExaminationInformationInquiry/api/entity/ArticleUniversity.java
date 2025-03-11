package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-08
 */
@Data
@TableName("article_university")
@ApiModel(value = "ArticleUniversity对象", description = "")
public class ArticleUniversity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("article_id")
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

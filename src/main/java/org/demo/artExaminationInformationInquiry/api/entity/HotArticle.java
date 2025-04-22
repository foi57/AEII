package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;
/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-20
 */
@Data
@Accessors(chain = true)
@TableName(value =  "hot_article",autoResultMap = true)
@ApiModel(value = "HotArticle对象", description = "")
public class HotArticle implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("article_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long articleId;

    @TableId("id")
    private Integer id;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField(exist = false)
    String articleTitle;
}

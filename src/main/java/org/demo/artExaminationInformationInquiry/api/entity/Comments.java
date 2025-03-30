package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;
/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-27
 */
@Data
@TableName(value = "comments",autoResultMap = true)
@Accessors(chain = true)
@ApiModel(value = "Comments对象", description = "")
public class Comments implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "comments_id", type = IdType.AUTO)
    private Long commentsId;

    @TableField("users_id")
    private Long usersId;

    @TableField("content")
    private String content;

    @TableField("article_id")
    private Long articleId;

    @TableField("reply_id")
    private Integer replyId;

    @TableField( value = "thumb",fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private Integer thumb;

    @TableField(value = "to_users_id", typeHandler = JacksonTypeHandler.class)
    private List<Long> toUsersId;

    @TableField(value = "picture", typeHandler = JacksonTypeHandler.class)
    private List<String> picture;

    @TableField(value = "create_time",fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private List<Comments> subComments;
}

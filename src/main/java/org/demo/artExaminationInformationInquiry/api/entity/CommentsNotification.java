package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.Data;
import lombok.experimental.Accessors;
/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-01
 */
@Data
@Accessors(chain = true)
@TableName(value =  "comments_notification",autoResultMap = true)
@ApiModel(value = "CommentsNotification对象", description = "")
public class CommentsNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("comments_id")
    private Long commentsId;

    @TableField("replyComments_id")
    private Long replyCommentsId;

    @TableField(value =  "users_id" ,typeHandler = JacksonTypeHandler.class)
    private List<Long> usersId;

    @TableField("isRead")
    private Boolean isRead;

    @TableField("category")
    private String category;

    @TableField(exist = false)
    Comments subComments;

    @TableField(value = "create_time",fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
}

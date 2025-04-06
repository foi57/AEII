package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2025-04-06
 */
@Data
@Accessors(chain = true)
@TableName(value =  "comments_notification_read",autoResultMap = true)
@ApiModel(value = "CommentsNotificationRead对象", description = "")
public class CommentsNotificationRead implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField("comments_notification_id")
    private Long commentsNotificationId;

    @TableField("users_id")
    private Long usersId;

    @TableField("isRead")
    private Boolean isRead;
}

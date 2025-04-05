package org.demo.artExaminationInformationInquiry.api.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;
/**
 * <p>
 * 
 * </p>
 *
 * @author ��ع��
 * @since 2025-03-27
 */
@Data
@TableName(value = "comments",autoResultMap = true)
@Accessors(chain = true)
@ApiModel(value = "Comments����", description = "")
public class Comments implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "comments_id", type = IdType.AUTO)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long commentsId;

    @TableField("users_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long usersId;

    @TableField("content")
    private String content;

    @TableField("article_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long articleId;

    @TableField("reply_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long replyId;

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

    @TableField(exist = false)
    private Boolean isThumbsUp;

    @TableField(exist = false)
    private Boolean isRead;
    
    @TableField(exist = false)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long notificationId;
    
    // 或者使用通用的transient属性设置方法
    @TableField(exist = false)
    private Map<String, Object> transientProperties = new HashMap<>();
    
    public void setTransient(String key, Object value) {
        transientProperties.put(key, value);
    }
    
    public Object getTransient(String key) {
        return transientProperties.get(key);
    }
    
}

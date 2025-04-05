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
 * @since 2025-03-30
 */
@Data
@Accessors(chain = true)
@TableName(value =  "comments_users",autoResultMap = true)
@ApiModel(value = "CommentsUsers对象", description = "")
public class CommentsUsers implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("comments_id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long commentsId;

    @TableField("users_id")
    private Long usersId;
}

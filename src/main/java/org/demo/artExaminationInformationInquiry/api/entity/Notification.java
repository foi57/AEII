package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;
/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-25
 */
@Data
@Accessors(chain = true)
@TableName(value = "notification",autoResultMap = true)
@ApiModel(value = "Notification对象", description = "")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "notificationId", type = IdType.AUTO) // 添加主键生成策略
    @JsonSerialize(using = ToStringSerializer.class)
    private Long notificationId;

    @TableField("userId")
    private Long userId;

    @TableField("category")
    private String category;

    @TableField("content")
    private String content;

    @TableField("link")
    private String link;

    @TableField(exist = false)
    private Boolean isRead;

    @TableField(value="create_time",fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}

package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;
/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-26
 */
@Data
@Accessors(chain = true)
@TableName("notification_users")
@ApiModel(value = "NotificationUsers对象", description = "")
public class NotificationUsers implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("notification_id")
    private Long notificationId;

    @TableField("user_id")
    private Long userId;


    @TableField(value = "isRead",fill = FieldFill.INSERT)
    private Boolean isRead;
}

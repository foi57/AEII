package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-02-18
 */
@Getter
@Setter
@TableName("users")
@ApiModel(value = "Users对象", description = "")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "users_id", type = IdType.AUTO)
    private Long usersId;

    @TableField("users_avatar")
    private String usersAvatar;

    @TableField("users_name")
    private String usersName;

    @TableField("users_password")
    private String usersPassword;

    @TableField("users_email")
    private String usersEmail;

    @TableField("users_phone")
    private Integer usersPhone;

    @TableField("users_creationTime")
    private LocalDateTime userCreationTime;

    @TableField("users_role")
    private String usersRole;
}

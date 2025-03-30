package org.demo.artExaminationInformationInquiry.api.entity;

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
 * @since 2025-03-30
 */
@Data
@Accessors(chain = true)
@TableName("comments_users")
@ApiModel(value = "CommentsUsers对象", description = "")
public class CommentsUsers implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("comments_id")
    private Long commentsId;

    @TableField("users_id")
    private Long usersId;
}

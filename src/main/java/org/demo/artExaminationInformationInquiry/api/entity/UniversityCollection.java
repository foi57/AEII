package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;
/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-24
 */
@Data
@Accessors(chain = true)
@TableName("university_collection")
@ApiModel(value = "UniversityCollection对象", description = "")
public class UniversityCollection implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("users_id")
    private Long usersId;

    @TableField("university_id")
    private Long universityId;
}

package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;

import lombok.Data;


/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-02-28
 */
@Data
@TableName("major")
@ApiModel(value = "Major对象", description = "")
public class Major implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "major_id", type = IdType.AUTO)
    private Long majorId;

    @TableField("major_code")
    private String majorCode;

    @TableField("major_name")
    private String majorName;

    @TableField("major_category")
    private String majorCategory;

    @TableField("duration")
    private String duration;

    @TableField("major_introduction")
    private String majorIntroduction;

    @TableField("major_course")
    private String majorCourse;

    @TableField("major_employment")
    private String majorEmployment;
}

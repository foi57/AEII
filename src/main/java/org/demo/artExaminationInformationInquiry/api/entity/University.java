package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-04
 */
@Getter
@Setter
@ToString
@TableName("university")
@ApiModel(value = "University对象", description = "")
public class University implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "university_id", type = IdType.AUTO)
    private Long universityId;

    @TableField("university_schoolBadge")
    private String universitySchoolbadge;

    @TableField("university_name")
    private String universityName;

    @TableField("university_level")
    private String universityLevel;

    @TableField("university_type")
    private String universityType;

    @TableField("university_area")
    private String universityArea;

    @TableField("university_introduction")
    private String universityIntroduction;

    @TableField("university_phone")
    private String universityPhone;

    @TableField("university_email")
    private String universityEmail;

    @TableField("university_web")
    private String universityWeb;

    @TableField("university_features")
    private String universityFeatures;
}

package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2025-03-11
 */
@Getter
@Setter
@ToString
@TableName("university_major")
@ApiModel(value = "UniversityMajor对象", description = "")
public class UniversityMajor implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("university_id")
    private Long universityId;

    @TableField("major_id")
    private Long majorId;
}

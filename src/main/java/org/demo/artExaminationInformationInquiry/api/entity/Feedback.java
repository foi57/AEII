package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;
/**
 * <p>
 * 
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-29
 */
@Data
@TableName(value = "feedback" ,autoResultMap = true) 
@Accessors(chain = true)
@ApiModel(value = "Feedback对象", description = "")
public class Feedback implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "feedbackId", type = IdType.AUTO)
    private Long feedbackId;

    @TableField("content")
    private String content;


    @TableField(value =  "createTime",fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField("usersId")
    private Long usersId;

    @TableField(exist = false)
    private String usersname;

    @TableField("contact")
    private String contact;

    
    @TableField(value =  "images", typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private List<String> images;
}

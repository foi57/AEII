package org.demo.artExaminationInformationInquiry.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2025-04-19
 */
@Data
@TableName("carousel")
@Accessors(chain = true)
@ApiModel(value = "Carousel对象", description = "")
public class Carousel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("picture")
    private String picture;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("link")
    private String link;

    @TableField(value = "sort_order")
    private Integer sortOrder;
}

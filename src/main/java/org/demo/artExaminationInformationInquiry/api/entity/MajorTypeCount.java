package org.demo.artExaminationInformationInquiry.api.entity;

import java.util.List;

import lombok.Data;

@Data
public class MajorTypeCount {
    private String typeName;        // 专业类型名称（如：美术类、音乐类）
    private Integer typeCount;      // 该类型的总专业数量
    private List<MajorCount> majors;// 该类型下的具体专业列表
}
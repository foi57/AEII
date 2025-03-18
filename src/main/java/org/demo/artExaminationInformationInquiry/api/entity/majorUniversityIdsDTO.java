package org.demo.artExaminationInformationInquiry.api.entity;

import lombok.Data;

import java.util.List;
@Data
public class majorUniversityIdsDTO {
    private Long majorId;
    private List<Long> universityIds;
}

package org.demo.artExaminationInformationInquiry;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.demo.artExaminationInformationInquiry")
@MapperScan("org.demo.artExaminationInformationInquiry.api.mapper")
public class ArtExaminationInformationInquiryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArtExaminationInformationInquiryApplication.class, args);
	}

}

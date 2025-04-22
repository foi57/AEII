package org.demo.artExaminationInformationInquiry.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.demo.artExaminationInformationInquiry.api.entity.Carousel;
import org.demo.artExaminationInformationInquiry.api.service.ICarouselService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-19
 */
@RestController
@Slf4j
@RequestMapping("/api/carousel")
public class CarouselController {
    ICarouselService carouselService;
    CarouselController(ICarouselService carouselService) {
        this.carouselService = carouselService;
    }

    @PostMapping("/insert")
    public String postMethodName(MultipartFile file,String link) {

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "file", "carousel");
      
        try {
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            Carousel carousel=new Carousel();
            carousel.setLink(link);
            carousel.setPicture(fileName);
            carousel.setSortOrder(carouselService.getMaxSortOrder()+1) ;
            carouselService.save(carousel);
            return "上传成功";
        } catch (IOException e) {
            log.error("文件上传失败: {}", fileName, e); 
            return "上传失败";
        }
    }
    
    @GetMapping("/get")
    public List<Carousel> getCarouselList() {
        return carouselService.selectCarousel();
    }
    
    @DeleteMapping("/delete/{id}")
    public Boolean deleteCarousel(@RequestParam("id") Integer id) {
        return carouselService.deleteCarousel(id);
    }

    @PutMapping("/move")
    public String moveCarousel(int id, String direction) {
        carouselService.moveCarousel(id,direction);
        return "移动成功";
    }
    
}

package org.demo.artExaminationInformationInquiry.api.service;

import java.util.List;

import org.demo.artExaminationInformationInquiry.api.entity.Carousel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-19
 */
public interface ICarouselService extends IService<Carousel> {
    Boolean insertCarouse(Carousel carousel);
    List<Carousel> selectCarousel();
    Carousel selectCarouselById(Integer id);
    Boolean deleteCarousel(Integer id);
    Boolean moveCarousel(Integer id,String direction);
    Carousel selectNextCarousel(Integer sortOrder);
    Carousel selectPreviousCarousel(Integer sortOrder);
    Integer getMaxSortOrder();
}

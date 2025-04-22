package org.demo.artExaminationInformationInquiry.api.service.impl;

import java.util.List;

import org.demo.artExaminationInformationInquiry.api.entity.Carousel;
import org.demo.artExaminationInformationInquiry.api.mapper.CarouselMapper;
import org.demo.artExaminationInformationInquiry.api.service.ICarouselService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-19
 */
@Service
@Slf4j
public class CarouselServiceImpl extends ServiceImpl<CarouselMapper, Carousel> implements ICarouselService {

    @Override
    public Boolean insertCarouse(Carousel carousel) {
        return save(carousel);
    }

    @Override
    public List<Carousel> selectCarousel() {
        QueryWrapper<Carousel> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort_order");
        return list(queryWrapper);
    }

    @Override
    public Boolean deleteCarousel(Integer id) {
        return removeById(id);
    }

    @Override
    @Transactional
    public Boolean moveCarousel(Integer id, String direction) {
        Carousel current = getById(id);
        if (current == null || current.getSortOrder() == null) {
            log.warn("尝试移动不存在或 sortOrder 为空的轮播图: id={}", id);
            return false; // 数据异常
        }

        Carousel adjacent = null;
        if ("up".equals(direction)) {
            // 上移
            adjacent = selectPreviousCarousel(current.getSortOrder());
        } else if ("down".equals(direction)) {
            // 下移
            adjacent = selectNextCarousel(current.getSortOrder());
            }
            if (adjacent == null) {
                log.info("轮播图 {} 无法移动 '{}', 已在顶部或底部。", id, direction);
                return false; // 已经是顶部或底部
            }
          // 交换 sortOrder
          Integer tempOrder = current.getSortOrder();
          current.setSortOrder(adjacent.getSortOrder());
          adjacent.setSortOrder(tempOrder);
  
          // 更新数据库
          boolean updateCurrent = updateById(current);
          boolean updateAdjacent = updateById(adjacent);
          
          if (!updateCurrent || !updateAdjacent) {
               log.error("移动轮播图时更新数据库失败: currentId={}, adjacentId={}", current.getId(), adjacent.getId());
               return false; 
          }
                 
        return false;
    }

    @Override
    public Carousel selectCarouselById(Integer id) {
        return getById(id);
    }

    @Override
    public Carousel selectNextCarousel(Integer sortOrder) {
        return baseMapper.findNext(sortOrder); 
    }

    @Override
    public Carousel selectPreviousCarousel(Integer sortOrder) {
        return baseMapper.findPrevious(sortOrder);
    }

    @Override
    public Integer getMaxSortOrder() {
       Integer maxSortOrder = baseMapper.getMaxSortOrder();
        return maxSortOrder == null ? 0 : maxSortOrder;
    }
    
    
}

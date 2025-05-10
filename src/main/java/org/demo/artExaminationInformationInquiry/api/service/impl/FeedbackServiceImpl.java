package org.demo.artExaminationInformationInquiry.api.service.impl;

import java.util.List;

import org.demo.artExaminationInformationInquiry.api.entity.Feedback;
import org.demo.artExaminationInformationInquiry.api.entity.Users;
import org.demo.artExaminationInformationInquiry.api.mapper.FeedbackMapper;
import org.demo.artExaminationInformationInquiry.api.service.IFeedbackService;
import org.demo.artExaminationInformationInquiry.api.service.IUsersService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-04-29
 */
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements IFeedbackService {
    private final IUsersService usersService;

    @Override
    public Page<Feedback> selectFeedbackPage(int page, int size) {
        Page<Feedback> fPage= lambdaQuery()
                .orderByDesc(Feedback::getCreateTime) // 根据 creatTime 字段降序排序
                .page(new Page<>(page, size)); 

        fPage.getRecords().forEach(feedback -> {
            Long userId = feedback.getUsersId(); // 获取用户 ID
            Users user = usersService.getById(userId); // 根据用户 ID 查询用户信息 
            if (user != null) { // 如果用户存在，设置用户名到 feedback 对象中
                feedback.setUsersname(user.getUsersName()); // 设置用户名到 feedback 对象中
            }
        });
        return fPage;
    }

    public FeedbackServiceImpl(IUsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    @Transactional
    public Boolean insertFeedback(Feedback feedback) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user = usersService.selectUsersByName(auth.getName());
        feedback.setUsersId(user.getUsersId());
        return save(feedback);
    }
}

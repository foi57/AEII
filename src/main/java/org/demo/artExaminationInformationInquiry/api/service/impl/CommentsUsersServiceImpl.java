package org.demo.artExaminationInformationInquiry.api.service.impl;

import org.demo.artExaminationInformationInquiry.api.entity.CommentsUsers;
import org.demo.artExaminationInformationInquiry.api.mapper.CommentsUsersMapper;
import org.demo.artExaminationInformationInquiry.api.service.ICommentsUsersService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-03-30
 */
@Service
public class CommentsUsersServiceImpl extends ServiceImpl<CommentsUsersMapper, CommentsUsers> implements ICommentsUsersService {
    
    @Override
    public Boolean insertCommentUsers(CommentsUsers commentsUsers) {
        return save(commentsUsers);
    }

    @Override 
    public Boolean deleteCommentUsersById(Long id) {
       QueryWrapper<CommentsUsers> queryWrapper = new QueryWrapper<>();
       queryWrapper.eq("usersId",id);
       return remove(queryWrapper);
    }
    
}

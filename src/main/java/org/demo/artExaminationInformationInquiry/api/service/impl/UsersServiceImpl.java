package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import org.demo.artExaminationInformationInquiry.api.entity.Users;
import org.demo.artExaminationInformationInquiry.api.mapper.UsersMapper;
import org.demo.artExaminationInformationInquiry.api.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-02-18
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {
    UsersMapper usersMapper;
    Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);
    public UsersServiceImpl(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    public Users selectUsersByName(String usersName) {
        logger.debug("usersName:{}", usersName);
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("users_name", usersName);
        return usersMapper.selectOne(queryWrapper);
    }
    public Users selectUsersByNamePassword(String usersName, String usersPassword) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("users_name", usersName);
        queryWrapper.eq("users_password", usersPassword);
        return usersMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Users> selectUsersByRole(String usersRole,int pageNum, int pageSize) {
        return lambdaQuery().eq(Users::getUsersRole, usersRole).page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize)).getRecords();
    }
}

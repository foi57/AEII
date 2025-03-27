package org.demo.artExaminationInformationInquiry.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Users;
import org.demo.artExaminationInformationInquiry.api.mapper.UsersMapper;
import org.demo.artExaminationInformationInquiry.api.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("users_name", usersName);
       return  usersMapper.selectOne(queryWrapper);
    }
    public List<Users> selectUsersByNameFuzzy(String usersName) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("users_name", usersName);
        return usersMapper.selectList(queryWrapper);
    }
    @Override
    public Users selectUsersByNameRole(String usersName, String usersRole) {
        return lambdaQuery().eq(Users::getUsersName, usersName).eq(Users::getUsersRole, usersRole).one();
    }

    public Users selectUsersByNamePassword(String usersName, String usersPassword) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("users_name", usersName);
        queryWrapper.eq("users_password", usersPassword);
        return usersMapper.selectOne(queryWrapper);
    }

    @Override
    public Map<String, Object> selectUsersByRole(String usersRole, int pageNum, int pageSize) {
        Map<String, Object> map = new java.util.HashMap<>();
        Long count = lambdaQuery().eq(Users::getUsersRole, usersRole).count();
        map.put("count", count);
        map.put("data", lambdaQuery().eq(Users::getUsersRole, usersRole).page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize)).getRecords());
        return map;
    }
    public void insertUser(Users users){
        usersMapper.insert(users);
    }
    public void updateUser(Users users){
       usersMapper.updateById(users);
    }
    public void deleteUser(Long usersId){
        usersMapper.deleteById(usersId);
    }
    public String selectPassword(String usersName){
        return lambdaQuery().eq(Users::getUsersName, usersName).one().getUsersPassword();
    }
    public void updatePassword(String usersName, String usersPassword){
        lambdaUpdate().eq(Users::getUsersName, usersName).set(Users::getUsersPassword, usersPassword).update();
    }

    @Override
    public Page<Users> selectUsersByNamePage(String usersName, int pageNum, int pageSize) {
        Page<Users> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("users_name", usersName);
        return lambdaQuery().page(page);
    }
}

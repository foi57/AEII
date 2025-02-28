package org.demo.artExaminationInformationInquiry.api.service;

import org.demo.artExaminationInformationInquiry.api.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-02-18
 */
public interface IUsersService extends IService<Users> {
    public List<Users> selectUsersByNameFuzzy(String usersName);
    Users selectUsersByName(String usersName);
    Users selectUsersByNameRole(String usersName, String usersRole);
    Users selectUsersByNamePassword(String usersName, String usersPassword);
    Map<String,Object> selectUsersByRole(String usersRole, int pageNum, int pageSize);
    void insertUser(Users users);
    void updateUser(Users users);
    void deleteUser(Long id);
    String selectPassword(String usersName);
    void updatePassword(String usersName, String usersPassword);
}

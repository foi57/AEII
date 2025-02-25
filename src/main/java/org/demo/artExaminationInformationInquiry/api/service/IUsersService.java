package org.demo.artExaminationInformationInquiry.api.service;

import org.demo.artExaminationInformationInquiry.api.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-02-18
 */
public interface IUsersService extends IService<Users> {

    Users selectUsersByName(String usersName);
    Users selectUsersByNamePassword(String usersName, String usersPassword);
    List<Users> selectUsersByRole(String usersRole,int pageNum, int pageSize);
}

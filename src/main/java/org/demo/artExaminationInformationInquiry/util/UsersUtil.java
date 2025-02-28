package org.demo.artExaminationInformationInquiry.util;

import org.demo.artExaminationInformationInquiry.api.entity.Users;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class UsersUtil {
    public Users BuildUsers(String name, String password, String email, String phone, String role){
        Users users = new Users();
        users.setUsersName(name);
        users.setUsersPassword(password);
        users.setUsersEmail(email);
        users.setUsersPhone(phone);
        users.setUsersRole(role);
        users.setDeleted(0);
        users.setUserCreationTime(LocalDateTime.now());
        return users;
    }
}

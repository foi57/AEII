package org.demo.artExaminationInformationInquiry.config.SecurityConfig;
import org.demo.artExaminationInformationInquiry.api.entity.Users;
import org.demo.artExaminationInformationInquiry.api.service.IUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MyUserDetailsService implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);
    private final IUsersService iUsersService;

    public MyUserDetailsService(IUsersService iUsersService) {
        this.iUsersService = iUsersService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Users user = iUsersService.selectUsersByName(username);

        return new org.springframework.security.core.userdetails.User(user.getUsersName(), user.getUsersPassword(), getAuthorities(user));
    }
    private  Collection<? extends GrantedAuthority> getAuthorities(Users user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        String role = user.getUsersRole();
        if (!role.startsWith("ROLE_")){
            role = "ROLE_" + role;
        }
            authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
        return authorities;
    }
}

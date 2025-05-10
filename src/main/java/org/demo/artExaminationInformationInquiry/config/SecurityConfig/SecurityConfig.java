package org.demo.artExaminationInformationInquiry.config.SecurityConfig;

import org.demo.artExaminationInformationInquiry.api.service.impl.UsersServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig{


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

    http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                    // 仅限高级管理员的接口
                    .requestMatchers("/api/users/delete").hasAnyRole("seniorAdmin")
                    
                    // 高级管理员和普通管理员都可以访问的接口
                    .requestMatchers(
                            "/api/university/uploadImg",
                            "/api/article/upload/image",
                            "/api/university/updateUniversity",
                            "/api/university/insertUniversity",
                            "/api/major/updateMajor",
                            "/api/major/deleteMajor",
                            "/api/major/insertMajor",
                            "/api/universityMajor/deleteUniversityMajor",
                            "/api/universityMajor/insertUniversityMajor",
                            "/api/article/update",
                            "/api/article/delete/**"
                    ).hasAnyRole("seniorAdmin","admin")
                    
                    // 公开接口，无需认证
                    .requestMatchers(
                            "/api/users/login",
                            "/api/users/register",
                            "/auth/refresh",
                            "/images/university/**",
                            "/article/images/**",
                            "/article/files/**",
                            "/api/article/select",
                            "/users/avatar/**",
                            "/comments/images/**",
                            "/api/article/detail/**",
                            "/api/comments/selectListByArticleId",
                            "/api/users/selectById",
                            "/api/users/sendVerificationCode",
                            "/api/users/resetPassword",
                            "/api/university/selectUniversityList",
                            "/api/major/selectMajorListByName",
                            "/api/major/selectMajorListByUniversityId",
                            "/api/article/selectArticlesByCategoryUniversi",
                            "/api/notification/select",
                            "/carousel/images/**",
                            "/api/carousel/get",
                            "/api/hotArticle/list",
                            "/api/major/selectMajorListCount",
                            "/images/university/",
                            "/api/university/selectById",
                            "/api/universityCollection/selectByUserIdUniversityId",
                            "/feedback/images/**"
                    ).permitAll()

                    // 其他所有请求需要认证
                    .anyRequest().authenticated()
           )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
    ;

      return http.build();
  }
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

    @Bean
    public UserDetailsService userDetailsService(UsersServiceImpl usersService) {
        return new MyUserDetailsService(usersService); // 替换为您的 UserDetailsService 实现
    }
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOriginPattern("*");
    configuration.setAllowCredentials(true);
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}





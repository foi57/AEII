package org.demo.artExaminationInformationInquiry.api.controller;

import org.apache.ibatis.annotations.Param;
import org.demo.artExaminationInformationInquiry.api.entity.Users;
import org.demo.artExaminationInformationInquiry.api.service.IUsersService;
import org.demo.artExaminationInformationInquiry.config.SecurityConfig.JwtTokenUtil;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 黄毓峰
 * @since 2025-02-18
 */
@RestController
@RequestMapping("/api/users")
public class UsersController {
   Logger logger=org.slf4j.LoggerFactory.getLogger(UsersController.class);
   IUsersService usersService;
   JwtTokenUtil jwtTokenUtil;
   UsersController(IUsersService usersService, JwtTokenUtil jwtTokenUtil) {
      this.usersService = usersService;
      this.jwtTokenUtil=jwtTokenUtil;
   }
   // 管理员登录
   @PostMapping("/login")
   public ResponseEntity<Map<String,String>> login(@RequestParam("userName") String userName, @RequestParam("password") String password ) {
      Users users;
      try {
         users = usersService.selectUsersByNamePassword(userName, password);
      }catch (Exception e){
         logger.error("登录失败");
         return ResponseEntity.badRequest().build();
      }
      String token = jwtTokenUtil.generateToken(users.getUsersName());
      Map<String,String> map=new java.util.HashMap<>();
      map.put("userName",users.getUsersName());
      map.put("token",token);

         return ResponseEntity.ok(map);

   }

   @GetMapping("/selectByRole")
   public List<Users> selectByRole(@Param("role") String role,@Param("page") int pageNum,@Param("size") int pageSize){
      return usersService.selectUsersByRole(role,pageNum,pageSize);
   }
}

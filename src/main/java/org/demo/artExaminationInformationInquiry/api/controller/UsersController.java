package org.demo.artExaminationInformationInquiry.api.controller;

import org.apache.ibatis.annotations.Param;
import org.demo.artExaminationInformationInquiry.api.entity.Users;
import org.demo.artExaminationInformationInquiry.api.service.IUsersService;
import org.demo.artExaminationInformationInquiry.config.SecurityConfig.JwtTokenUtil;
import org.demo.artExaminationInformationInquiry.util.UsersUtil;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
   UsersUtil usersUtil;
   UsersController(IUsersService usersService, JwtTokenUtil jwtTokenUtil,UsersUtil usersUtil) {
      this.usersService = usersService;
      this.jwtTokenUtil=jwtTokenUtil;
      this.usersUtil=usersUtil;
   }
   // 管理员登录
   @PostMapping("/login")
   public ResponseEntity<Map<String,Object>> login(@RequestParam("userName") String userName, @RequestParam("password") String password ) {
      Users users;
      try {
         users = usersService.selectUsersByNamePassword(userName, password);
      }catch (Exception e){
         logger.error("登录失败");
         return ResponseEntity.badRequest().build();
      }
      String token = jwtTokenUtil.generateToken(users.getUsersName());
      Map<String,Object> map=new java.util.HashMap<>();
      map.put("user",users);
      map.put("token", token);

         return ResponseEntity.ok(map);

   }

   @GetMapping("/selectByRole")
   public Map<String,Object> selectByRole(@RequestParam("role") String role,@RequestParam("page") Integer pageNum,@RequestParam("size") Integer pageSize){
      return usersService.selectUsersByRole(role,pageNum,pageSize);
   }
   @GetMapping("/selectByName")
   public ResponseEntity<List<Users>> selectUserByName(@RequestParam("name") String name){
      List<Users> usersList=usersService.selectUsersByNameFuzzy(name);
      return ResponseEntity.ok(usersList);
   }
   @PostMapping("/insert")
   public ResponseEntity<String> insertUser(@RequestParam("name") String name,@RequestParam("password") String password,@RequestParam("email") String email,@RequestParam("phone")String phone,@RequestParam("role") String role){
       Users users = usersUtil.BuildUsers(name,password,email,phone,role);
       logger.debug(users.toString());

       try {
          if (usersService.selectUsersByName(users.getUsersName()) != null) {
             logger.debug("用户名已存在");
             return ResponseEntity.status(400).body("用户名已存在");
          }
          usersService.insertUser(users);
          return ResponseEntity.ok("success");
       }catch (Exception e){
          logger.error("插入失败");
          return ResponseEntity.status(400).body("插入失败");
       }
   }
   @PostMapping("/update")
   public ResponseEntity<String> updateUser(@RequestParam("id") Long id,@RequestParam("name") String name,@RequestParam("email") String email,@RequestParam("phone")String phone,@RequestParam("role") String role){
        try {
           Users users = usersService.selectUsersByName(name);
           if (!Objects.equals(users.getUsersId(), id)){
              logger.error("更新错误");
              throw new Exception("更新错误");
           }
           users.setUsersEmail(email);
           users.setUsersPhone(phone);
           users.setUsersRole(role);
           users.setUsersName(name);
           usersService.updateUser(users);
           return ResponseEntity.ok("success");
        }catch (Exception e){
           logger.error("更新失败");
           return ResponseEntity.status(400).body("更新失败");
        }
   }

   @PostMapping("/delete")
   public ResponseEntity<String> deleteUser(@RequestParam("id") Long id){
      try {
         usersService.deleteUser(id);
         return ResponseEntity.ok("success");
      }catch (Exception e){
         logger.error("删除失败");
         return ResponseEntity.status(400).body("删除失败");
      }
   }
   @GetMapping("/selectPassword")
   public ResponseEntity<String> selectPassword(@RequestParam("name") String name){
      try {
         String password=usersService.selectPassword(name);
         return ResponseEntity.ok(password);
      }catch (Exception e){
         logger.error("查询失败");
         return ResponseEntity.status(400).body("查询失败");
      }
   }
   @PostMapping("/updatePassword")
   public ResponseEntity<String> updatePassword(@RequestParam("name") String name,@RequestParam("password") String password){
      try {
         usersService.updatePassword(name,password);
         return ResponseEntity.ok("success");
      } catch (Exception e) {
         logger.error("更新失败");
         return ResponseEntity.status(400).body("更新失败");
      }
   }
}

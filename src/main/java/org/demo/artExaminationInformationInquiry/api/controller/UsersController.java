package org.demo.artExaminationInformationInquiry.api.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.artExaminationInformationInquiry.api.entity.Users;
import org.demo.artExaminationInformationInquiry.api.service.IUsersService;
import org.demo.artExaminationInformationInquiry.config.SecurityConfig.JwtTokenUtil;
import org.demo.artExaminationInformationInquiry.util.UsersUtil;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
   
   // 添加邮件发送服务
   private final JavaMailSender mailSender;
   
   // 存储验证码的Map，键为邮箱，值为验证码和过期时间
   private final Map<String, Map<String, Object>> verificationCodes = new ConcurrentHashMap<>();
   
   UsersController(IUsersService usersService, JwtTokenUtil jwtTokenUtil, UsersUtil usersUtil, JavaMailSender mailSender) {
      this.usersService = usersService;
      this.jwtTokenUtil = jwtTokenUtil;
      this.usersUtil = usersUtil;
      this.mailSender = mailSender;
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
      map.put("accessToken", token);
      String refreshToken = jwtTokenUtil.generateRefreshToken(users.getUsersName());
      map.put("refreshToken", refreshToken);
         return ResponseEntity.ok(map);

   }

   @GetMapping("/selectByRole")
   public Map<String,Object> selectByRole(@RequestParam("role") String role,@RequestParam("page") Integer pageNum,@RequestParam("size") Integer pageSize){
      logger.debug("角色:{}页码{}页数:{}", role, pageNum, pageSize);
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
   @PostMapping("/uploadAvatar")
   public ResponseEntity<String> updateAvatar( @RequestParam("id") Long id,
                                               @RequestParam("file") MultipartFile file){
      try {
         String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
         Path uploadPath = Paths.get(System.getProperty("user.dir") + "\\file\\user\\");
         Files.createDirectories(uploadPath);
         Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
         usersService.lambdaUpdate().eq(Users::getUsersId,id).set(Users::getUsersAvatar,fileName).update();
         return ResponseEntity.ok(fileName);
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
   }
   @GetMapping("/selectByNamePage")
   public ResponseEntity<Page<Users>> selectByNamePage(@RequestParam("name") String name, @RequestParam("page") Integer pageNum, @RequestParam("size") Integer pageSize){
       logger.debug("name:{}pageNum:{}pageSize:{}", name, pageNum, pageSize);
      Page<Users> usersPage=usersService.selectUsersByNamePage(name,pageNum,pageSize);
      return ResponseEntity.ok(usersPage);
   }
   @GetMapping("/selectById")
   public ResponseEntity<Users> selectById(@RequestParam("id") Long id){
      Users users=usersService.selectUsersById(id);
      return ResponseEntity.ok(users);
   }

   /**
    * 发送验证码到邮箱
    * @param email 用户邮箱
    * @return 发送结果
    */
   @PostMapping("/sendVerificationCode")
   public ResponseEntity<String> sendVerificationCode(@RequestParam("email") String email) {
      try {
         // 检查邮箱是否存在
         Users user = usersService.selectUsersByEmail(email);
         if (user == null) {
            return ResponseEntity.status(400).body("该邮箱未注册");
         }
         
         // 生成6位随机验证码
         String verificationCode = generateVerificationCode();
         
         // 发送邮件
         sendEmail(email, "密码重置验证码", "您的验证码是: " + verificationCode + "，有效期10分钟，请勿泄露给他人。");
         
         // 存储验证码，设置10分钟后过期
         Map<String, Object> codeInfo = new HashMap<>();
         codeInfo.put("code", verificationCode);
         codeInfo.put("expireTime", System.currentTimeMillis() + 10 * 60 * 1000);
         verificationCodes.put(email, codeInfo);
         
         logger.info("验证码已发送到邮箱: {}", email);
         return ResponseEntity.ok("验证码已发送");
      } catch (Exception e) {
         logger.error("发送验证码失败: {}", e.getMessage());
         return ResponseEntity.status(500).body("发送验证码失败");
      }
   }
   
   /**
    * 重置密码
    * @param requestBody 包含邮箱、验证码和新密码的请求体
    * @return 重置结果
    */
   @PostMapping("/resetPassword")
   public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> requestBody) {
      String email = requestBody.get("email");
      String verificationCode = requestBody.get("verificationCode");
      String newPassword = requestBody.get("newPassword");
      
      try {
         // 验证参数
         if (email == null || verificationCode == null || newPassword == null) {
            return ResponseEntity.status(400).body("参数不完整");
         }
         
         // 检查邮箱是否存在
         Users user = usersService.selectUsersByEmail(email);
         if (user == null) {
            return ResponseEntity.status(400).body("该邮箱未注册");
         }
         
         // 验证验证码
         Map<String, Object> codeInfo = verificationCodes.get(email);
         if (codeInfo == null) {
            return ResponseEntity.status(400).body("请先获取验证码");
         }
         
         String storedCode = (String) codeInfo.get("code");
         long expireTime = (long) codeInfo.get("expireTime");
         
         if (System.currentTimeMillis() > expireTime) {
            verificationCodes.remove(email);
            return ResponseEntity.status(400).body("验证码已过期");
         }
         
         if (!verificationCode.equals(storedCode)) {
            return ResponseEntity.status(400).body("验证码错误");
         }
         
         // 更新密码
         usersService.updatePassword(user.getUsersName(), newPassword);
         
         // 清除验证码
         verificationCodes.remove(email);
         
         logger.info("用户 {} 密码重置成功", user.getUsersName());
         return ResponseEntity.ok("密码重置成功");
      } catch (Exception e) {
         logger.error("重置密码失败: {}", e.getMessage());
         return ResponseEntity.status(500).body("重置密码失败");
      }
   }
   
   /**
    * 生成6位随机验证码
    * @return 验证码
    */
   private String generateVerificationCode() {
      Random random = new Random();
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < 6; i++) {
         sb.append(random.nextInt(10));
      }
      return sb.toString();
   }
   
   /**
    * 发送邮件
    * @param to 收件人
    * @param subject 主题
    * @param content 内容
    */
   private void sendEmail(String to, String subject, String content) {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom("2858712518@qq.com");
      message.setTo(to);
      message.setSubject(subject);
      message.setText(content);
      mailSender.send(message);
   }

}

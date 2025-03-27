package org.demo.artExaminationInformationInquiry.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class uploadFileUtil {
    public String upload(MultipartFile file, String Dir) throws IOException {
        String uploadDir = System.getProperty("user.dir") + Dir;

        // 创建目录（如果不存在）
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一文件名
        InputStream inputStream = file.getInputStream();
        String hash = DigestUtils.md5Hex(inputStream);  // 直接对文件内容哈希
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName =  hash + "." + ext;

        // 检查文件是否已存在
        Path targetPath = Paths.get(uploadDir, fileName);
        if (!Files.exists(targetPath)) {
            Files.copy(file.getInputStream(), targetPath);
        }
        return fileName;
    }
}

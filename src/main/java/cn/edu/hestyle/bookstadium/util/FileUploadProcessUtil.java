package cn.edu.hestyle.bookstadium.util;

import cn.edu.hestyle.bookstadium.controller.exception.FileUploadFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Random;

/**
 * @author hestyle
 * @projectName book_stadium
 * @date 2021/3/5 5:33 下午
 */
public class FileUploadProcessUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadProcessUtil.class);

    public static String saveFile(MultipartFile file, String dirPath, long fileMaxSize, List<String> fileTypes) {
        // 检查文件类型、大小
        if (file.isEmpty()) {
            logger.warn("文件上传失败，文件为空！");
            throw new FileUploadFailedException("上传失败，没有选择上传的文件，或选中的文件为空!");
        }
        if (file.getSize() > fileMaxSize) {
            logger.warn("文件上传失败，上传的文件超过最大限制！");
            throw new FileUploadFailedException("上传失败，上传的文件大小超过最大限制！");
        }
        if (!fileTypes.contains(file.getContentType())) {
            logger.warn("上传失败，文件类型非法，contentType = " + file.getContentType() + "只允许上传图片！");
            throw new FileUploadFailedException("上传失败，文件类型非法，只允许上传图片文件！");
        }
        // 检查保存上传文件的文件夹存在(一个是在resource一个是在target下)
        String pathNameTemp = null;
        try {
            pathNameTemp = ResourceUtils.getURL("classpath:").getPath() + "static/" + dirPath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String pathNameTruth = pathNameTemp.replace("target", "src").replace("classes", "main/resources");
        File parentTemp = new File(pathNameTemp);
        File parentTruth = new File(pathNameTruth);
        if (!parentTemp.exists()) {
            parentTemp.mkdirs();
        }
        if (!parentTruth.exists()) {
            parentTruth.mkdirs();
        }
        // 重新命令、保存上传文件
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
        String saveFileName = System.currentTimeMillis() + "" + (new Random().nextInt(90000000) + 10000000) + suffix;
        File destTemp = new File(parentTemp, saveFileName);
        File destTruth = new File(parentTruth, saveFileName);
        try {
            // 写到target，再copy至resource
            file.transferTo(destTemp);
            FileCopyUtils.copy(destTemp, destTruth);
            System.err.println("文件上传完成！");
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileUploadFailedException("上传失败，文件类型非法，只允许上传图片文件！");
        }
        return "/book_stadium_online/" + dirPath + "/" + saveFileName;
    }
}

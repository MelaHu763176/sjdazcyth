package priv.muller.controller.req;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

@Data
@Accessors(chain = true)
public class FileUploadReq {

    /**
     * 文件名
     */
    private String filename;

    /**
     * 文件唯一标识（md5）
     */
    private String identifier;

    /**
     * 用户id
     */
    private Long accountId;

    /**
     * 父级目录id
     */
    private Long parentId;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件
     */
    private MultipartFile file;

}
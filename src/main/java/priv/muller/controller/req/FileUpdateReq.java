package priv.muller.controller.req;

import lombok.Data;

@Data
public class FileUpdateReq {

    /**
     * 用户id
     */
    private Long accountId;

    /**
     * 文件id
     */
    private Long fileId;

    /**
     * 新的文件名
     */
    private String newFilename;


}
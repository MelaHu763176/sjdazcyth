package priv.muller.service;

import priv.muller.controller.req.*;

public interface AccountFileService {

    /**
     * 创建文件夹
     * @param req
     */
    Long createFolder(FolderCreateReq req);
    /**
     * 普通小文件上传
     * @param req
     */
    void fileUpload(FileUploadReq req);
    /**
     * 保存文件和文件关联关系
     * @param req
     * @param storeFileObjectKey
     */
    void saveFileAndAccountFile(FileUploadReq req, String storeFileObjectKey);


}

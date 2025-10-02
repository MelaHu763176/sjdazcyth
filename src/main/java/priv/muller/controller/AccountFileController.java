package priv.muller.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import priv.muller.controller.req.*;
import priv.muller.service.AccountFileService;
import priv.muller.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priv.muller.interceptor.LoginInterceptor;

@RestController
@RequestMapping("/api/file/v1")
@Tag(name = "文件管理接口", description = "提供文件和文件夹的管理功能")
public class AccountFileController {
    @Autowired
    private AccountFileService accountFileService;

    /**
     * 普通小文件上传接口
     */
    @PostMapping("upload")
    @Operation(summary = "普通小文件上传", description = "普通小文件上传")
    public JsonData upload(FileUploadReq req) {
        req.setAccountId(LoginInterceptor.threadLocal.get().getId());
        accountFileService.fileUpload(req);
        return JsonData.buildSuccess();
    }

}

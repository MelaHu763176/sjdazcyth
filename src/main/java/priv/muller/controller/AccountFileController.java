package priv.muller.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import priv.muller.controller.req.*;
import priv.muller.dto.AccountFileDTO;
import priv.muller.dto.FolderTreeNodeDTO;
import priv.muller.service.AccountFileService;
import priv.muller.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priv.muller.interceptor.LoginInterceptor;

import java.util.List;

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

    /**
     * 查询文件列表接口
     */
    @GetMapping("list")
    @Operation(summary = "查询文件列表", description = "根据父文件夹ID查询文件列表")
    public JsonData list(
            @Parameter(description = "父文件夹ID", required = true, example = "1") @RequestParam(value = "parent_id") Long parentId) {
        Long accountId = LoginInterceptor.threadLocal.get().getId();
        List<AccountFileDTO> list = accountFileService.listFile(accountId, parentId);
        return JsonData.buildSuccess(list);
    }

    /**
     * 创建文件夹
     */
    @PostMapping("create_folder")
    @Operation(summary = "创建文件夹", description = "根据请求参数创建文件夹")
    public JsonData createFolder(
            @Parameter(description = "文件夹创建请求对象", required = true) @RequestBody FolderCreateReq req) {
        Long accountId = LoginInterceptor.threadLocal.get().getId();
        req.setAccountId(accountId);
        accountFileService.createFolder(req);
        return JsonData.buildSuccess();
    }

    /**
     * 文件重命名
     */
    @PostMapping("rename_file")
    @Operation(summary = "文件重命名", description = "根据请求参数重命名文件")
    public JsonData renameFile(
            @Parameter(description = "文件重命名请求对象", required = true) @RequestBody FileUpdateReq req) {
        Long accountId = LoginInterceptor.threadLocal.get().getId();
        req.setAccountId(accountId);
        accountFileService.renameFile(req);
        return JsonData.buildSuccess();
    }

    /**
     * 文件树接口
     */
    @GetMapping("/folder/tree")
    @Operation(summary = "获取文件树", description = "获取文件和文件夹的树形结构")
    public JsonData folderTree() {
        Long accountId = LoginInterceptor.threadLocal.get().getId();
        List<FolderTreeNodeDTO> list = accountFileService.folderTree(accountId);
        return JsonData.buildSuccess(list);
    }


}

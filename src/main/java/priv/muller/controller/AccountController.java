package priv.muller.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import priv.muller.controller.req.AccountLoginReq;
import priv.muller.controller.req.AccountRegisterReq;
import priv.muller.dto.AccountDTO;
import priv.muller.interceptor.LoginInterceptor;
import priv.muller.service.AccountService;
import priv.muller.util.JsonData;
import priv.muller.util.JwtUtil;

@RestController
@RequestMapping("/api/account/v1")
public class AccountController {


    @Autowired
    private AccountService accountService;

    /**
     * 注册接口
     */
    @PostMapping("register")
    public JsonData register(@RequestBody AccountRegisterReq req){
        accountService.register(req);
        return JsonData.buildSuccess();
    }

    /**
     * 头像上传接口
     */
    @PostMapping("upload_avatar")
    public JsonData uploadAvatar(@RequestParam("file") MultipartFile file){

        String url = accountService.uploadAvatar(file);

        return JsonData.buildSuccess(url);
    }


    /**
     * 登录模块
     */
    @PostMapping("login")
    public JsonData login(@RequestBody AccountLoginReq req){

        AccountDTO accountDTO = accountService.login(req);

        //生成token jwt ssm  一般前端存储在localStorage里面，或 sessionStorage里面
        String token = JwtUtil.geneLoginJWT(accountDTO);

        return JsonData.buildSuccess(token);
    }


    /**
     * 获取用户详情接口
     */
    @GetMapping("detail")
    public JsonData detail(){

        AccountDTO accountDTO = accountService.queryDetail(LoginInterceptor.threadLocal.get().getId());

        return JsonData.buildSuccess(accountDTO);
    }



}
package priv.muller.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priv.muller.controller.req.RecycleDelReq;
import priv.muller.controller.req.RecycleRestoreReq;
import priv.muller.dto.AccountFileDTO;
import priv.muller.interceptor.LoginInterceptor;
import priv.muller.service.RecycleService;
import priv.muller.util.JsonData;

import java.util.List;

@RestController
@RequestMapping("/api/recycle/v1/")
public class RecycleController {

    @Autowired
    private RecycleService recycleService;

    /**
     * 获取回收站列表
     */
    @GetMapping("list")
    public JsonData list(){
        Long accountId = LoginInterceptor.threadLocal.get().getId();

        List<AccountFileDTO> list = recycleService.listRecycleFiles(accountId);

        return JsonData.buildSuccess(list);
    }


    /**
     * 彻底删除回收站文件
     */
    @PostMapping("delete")
    public JsonData delete(@RequestBody RecycleDelReq req){

        req.setAccountId(LoginInterceptor.threadLocal.get().getId());

        recycleService.delete(req);

        return JsonData.buildSuccess();
    }


    /**
     * 还原回收站文件
     */
    @PostMapping("restore")
    public JsonData restore(@RequestBody RecycleRestoreReq req){

        req.setAccountId(LoginInterceptor.threadLocal.get().getId());

        recycleService.restore(req);

        return JsonData.buildSuccess();
    }



}
package priv.muller.service;

import org.springframework.web.multipart.MultipartFile;
import priv.muller.controller.req.AccountLoginReq;
import priv.muller.controller.req.AccountRegisterReq;
import priv.muller.dto.AccountDTO;

public interface AccountService {
    void register(AccountRegisterReq req);

    String uploadAvatar(MultipartFile file);

    AccountDTO login(AccountLoginReq req);

    AccountDTO queryDetail(Long id);
}

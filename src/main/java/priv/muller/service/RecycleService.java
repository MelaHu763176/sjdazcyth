package priv.muller.service;


import priv.muller.controller.req.RecycleDelReq;
import priv.muller.controller.req.RecycleRestoreReq;
import priv.muller.dto.AccountFileDTO;

import java.util.List;

public interface RecycleService {
    List<AccountFileDTO> listRecycleFiles(Long accountId);

    void delete(RecycleDelReq req);

    void restore(RecycleRestoreReq req);
}

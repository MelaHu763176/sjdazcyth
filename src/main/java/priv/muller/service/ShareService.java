package priv.muller.service;


import priv.muller.controller.req.*;
import priv.muller.dto.AccountFileDTO;
import priv.muller.dto.ShareDTO;
import priv.muller.dto.ShareDetailDTO;
import priv.muller.dto.ShareSimpleDTO;

import java.util.List;

public interface ShareService {

    List<ShareDTO> listShare();

    ShareDTO createShare(ShareCreateReq req);

    void cancelShare(ShareCancelReq req);

    ShareSimpleDTO simpleDetail(Long shareId);

    String checkShareCode(ShareCheckReq req);

    ShareDetailDTO detail(Long shareId);

    List<AccountFileDTO> listShareFile(ShareFileQueryReq req);

    void transferShareFile(ShareFileTransferReq req);
}

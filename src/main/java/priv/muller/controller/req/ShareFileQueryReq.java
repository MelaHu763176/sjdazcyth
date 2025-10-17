package priv.muller.controller.req;

import lombok.Data;

@Data
public class ShareFileQueryReq {
    private Long shareId;

    /**
     * 进入的目标文件夹ID
     */
    private Long parentId;
}
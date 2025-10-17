package priv.muller.controller.req;

import lombok.Data;

import java.util.List;
@Data
public class ShareFileTransferReq {

    private Long shareId;

    private Long accountId;

    /**
     * 目标文件夹ID
     */
    private Long parentId;

    /**
     * 转存的文件ID
     */
    private List<Long> fileIds;
}
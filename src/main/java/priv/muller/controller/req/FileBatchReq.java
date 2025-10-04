package priv.muller.controller.req;

import lombok.Data;

import java.util.List;

@Data
public class FileBatchReq {

    /**
     * 文件id列表
     */
    private List<Long> fileIds;

    /**
     * 目标父级id
     */
    private Long targetParentId;

    /**
     * 用户id
     */
    private Long accountId;
}
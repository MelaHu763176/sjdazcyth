package priv.muller.controller.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileChunkMergeReq {

    private String identifier;

    private Long parentId;

    private Long accountId;

}
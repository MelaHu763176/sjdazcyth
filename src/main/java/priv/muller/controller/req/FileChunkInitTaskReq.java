package priv.muller.controller.req;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileChunkInitTaskReq {

    private Long accountId;

    private String filename;

    private String identifier;

    /***
     * 总大小
     */
    private Long totalSize;

    /**
     * 分片大小
     */
    private Long chunkSize;

}
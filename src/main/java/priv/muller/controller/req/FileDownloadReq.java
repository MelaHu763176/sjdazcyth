package priv.muller.controller.req;

import lombok.Data;

import java.util.List;

@Data
public class FileDownloadReq {
    private Long accountId;
    private List<Long> fileIds;
}

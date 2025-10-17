package priv.muller.controller.req;

import lombok.Data;

import java.util.List;

@Data
public class RecycleDelReq {

    private List<Long> fileIds;

    private Long accountId;

}
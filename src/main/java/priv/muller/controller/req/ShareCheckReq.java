package priv.muller.controller.req;

import lombok.Data;

@Data
public class ShareCheckReq {

    private Long shareId;

    private String shareCode;
}
package priv.muller.controller.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareCreateReq {

    /**
     * 分享名称
     */
    private String shareName;


    /**
     * 分享类型，是否需要提取码
     */
    private String shareType;

    /**
     * 分享有效天数0永久，1-7天，2-30天
     */
    private Integer shareDayType;

    /**
     * 文件id列表
     */
    private List<Long> fileIds;

    /**
     * 分享人id
     */
    private Long accountId;

}
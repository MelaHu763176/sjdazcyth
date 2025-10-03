package priv.muller.controller.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "用户登录请求对象")
public class AccountLoginReq {

    /**
     * 密码
     */
    @Schema(description = "用户密码", required = true, example = "123456")
    private String password;

    /**
     * 手机号
     */
    @Schema(description = "用户手机号", required = true, example = "13800138000")
    private String phone;

}

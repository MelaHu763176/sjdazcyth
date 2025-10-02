package priv.muller.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@TableName("storage")
@Schema(name = "StorageDO", description = "存储信息表")
public class StorageDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "所属用户")
    @TableField("account_id")
    private Long accountId;

    @Schema(description = "占用存储大小")
    @TableField("used_size")
    private Long usedSize;

    @Schema(description = "总容量大小，字节存储")
    @TableField("total_size")
    private Long totalSize;

    @Schema(description = "创建时间")
    @TableField("gmt_create")
    private Date gmtCreate;

    @Schema(description = "更新时间")
    @TableField("gmt_modified")
    private Date gmtModified;
}

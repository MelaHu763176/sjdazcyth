package priv.muller.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import priv.muller.model.ShareFileDO;

import java.util.List;

public interface ShareFileMapper extends BaseMapper<ShareFileDO> {

    void insertBatch(List<ShareFileDO> shareFileDOS);
}

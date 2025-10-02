package priv.muller.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import priv.muller.model.AccountFileDO;

import java.util.List;

public interface AccountFileMapper extends BaseMapper<AccountFileDO> {

    void insertFileBatch(@Param("newAccountFileDOList") List<AccountFileDO> newAccountFileDOList);


    /**
     * 查询被删除的文件
     * @param accountId
     * @param fileIdList
     * @return
     */
    List<AccountFileDO> selectRecycleFiles(@Param("accountId") Long accountId,@Param("fileIdList") List<Long> fileIdList);

    /**
     * 查询被删除的文件
     * @param parentId 父文件夹ID
     * @return
     */
    List<AccountFileDO> selectRecycleChildFiles(@Param("parentId") Long parentId,@Param("accountId")Long accountId);

    /**
     * 彻底删除文件
     * @param recycleFileIds
     */
    void deleteRecycleFiles(List<Long> recycleFileIds);

    /**
     * 更新回收站的文件名
     * @param id
     * @param fileName
     * @return
     */
    boolean updateRecycleFileNameById(@Param("id") Long id, @Param("fileName") String fileName);

    /**
     * 批量恢复文件
     * @param allFileIds
     */
    void restoreFiles(@Param("allFileIds") List<Long> allFileIds);
}

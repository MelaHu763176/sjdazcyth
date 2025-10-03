package priv.muller.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import priv.muller.component.StoreEngine;
import priv.muller.config.AccountConfig;
import priv.muller.config.MinioConfig;
import priv.muller.controller.req.AccountLoginReq;
import priv.muller.controller.req.AccountRegisterReq;
import priv.muller.controller.req.FolderCreateReq;
import priv.muller.dto.AccountDTO;
import priv.muller.dto.StorageDTO;
import priv.muller.enums.AccountRoleEnum;
import priv.muller.enums.BizCodeEnum;
import priv.muller.exception.BizException;
import priv.muller.mapper.AccountFileMapper;
import priv.muller.mapper.AccountMapper;
import priv.muller.mapper.StorageMapper;
import priv.muller.model.AccountDO;
import priv.muller.model.AccountFileDO;
import priv.muller.model.StorageDO;
import priv.muller.service.AccountFileService;
import priv.muller.service.AccountService;
import priv.muller.util.CommonUtil;
import priv.muller.util.SpringBeanUtil;

import java.util.List;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private StoreEngine fileStoreEngine;

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private AccountFileService accountFileService;

    @Autowired
    private StorageMapper storageMapper;
    @Autowired
    private AccountFileMapper accountFileMapper;

    /**
     * 1、查询手机号是否重复
     * 2、加密密码
     * 3、插入数据库
     * 4、其他相关初始化操作
     * @param req
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(AccountRegisterReq req) {

        //1、查询手机号是否重复
        List<AccountDO> accountDOList = accountMapper.selectList(new QueryWrapper<AccountDO>().eq("phone", req.getPhone()));
        if(!accountDOList.isEmpty()){
            throw new BizException(BizCodeEnum.ACCOUNT_REPEAT);
        }

        AccountDO accountDO = SpringBeanUtil.copyProperties(req, AccountDO.class);

        //加密密码
        String digestAsHex = DigestUtils.md5DigestAsHex((AccountConfig.ACCOUNT_SALT + req.getPassword()).getBytes());
        accountDO.setPassword(digestAsHex);
        accountDO.setRole(AccountRoleEnum.COMMON.name());
        accountMapper.insert(accountDO);

        //创建默认的存储空间
        StorageDO storageDO = new StorageDO();
        storageDO.setAccountId(accountDO.getId());
        storageDO.setUsedSize(0L);
        storageDO.setTotalSize(AccountConfig.DEFAULT_STORAGE_SIZE);
        storageMapper.insert(storageDO);

        //初始化根目录
        FolderCreateReq createRootFolderReq = FolderCreateReq.builder()
                .accountId(accountDO.getId())
                .parentId(AccountConfig.ROOT_PARENT_ID)
                .folderName(AccountConfig.ROOT_FOLDER_NAME)
                .build();

        accountFileService.createFolder(createRootFolderReq);
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        String filename = CommonUtil.getFilePath(file.getOriginalFilename());
        fileStoreEngine.upload(minioConfig.getAvatarBucketName(), filename, file);
        return minioConfig.getEndpoint()+"/"+minioConfig.getAvatarBucketName()+"/"+filename;
    }

    @Override
    public AccountDTO login(AccountLoginReq req) {

        //处理密码
        String digestAsHex = DigestUtils.md5DigestAsHex((AccountConfig.ACCOUNT_SALT + req.getPassword()).getBytes());
        AccountDO accountDO = accountMapper.selectOne(new QueryWrapper<AccountDO>().eq("phone", req.getPhone()).eq("password", digestAsHex));
        if(accountDO == null){
            throw new BizException(BizCodeEnum.ACCOUNT_PWD_ERROR);
        }
        return SpringBeanUtil.copyProperties(accountDO,AccountDTO.class);
    }

    @Override
    public AccountDTO queryDetail(Long id) {

        //账号详情
        AccountDO accountDO = accountMapper.selectById(id);
        AccountDTO accountDTO = SpringBeanUtil.copyProperties(accountDO, AccountDTO.class);

        //获取存储信息
        StorageDO storageDO = storageMapper.selectOne(new QueryWrapper<StorageDO>().eq("account_id", id));
        accountDTO.setStorageDTO(SpringBeanUtil.copyProperties(storageDO, StorageDTO.class));

        //获取根文件信息
        AccountFileDO accountFileDO = accountFileMapper.selectOne(new QueryWrapper<AccountFileDO>()
                .eq("account_id", id).eq("parent_id", AccountConfig.ROOT_PARENT_ID));
        accountDTO.setRootFileId(accountFileDO.getId());
        accountDTO.setRootFileName(accountFileDO.getFileName());

        return accountDTO;
    }
}
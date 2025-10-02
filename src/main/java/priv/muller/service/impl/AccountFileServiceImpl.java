package priv.muller.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import priv.muller.component.StoreEngine;
import priv.muller.dto.AccountFileDTO;
import priv.muller.enums.FileTypeEnum;
import priv.muller.enums.FolderFlagEnum;
import priv.muller.mapper.AccountFileMapper;
import priv.muller.mapper.FileMapper;
import priv.muller.mapper.StorageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.muller.config.MinioConfig;
import priv.muller.controller.req.FileUploadReq;
import priv.muller.enums.BizCodeEnum;
import priv.muller.exception.BizException;
import priv.muller.model.AccountFileDO;
import priv.muller.model.FileDO;
import priv.muller.model.StorageDO;
import priv.muller.service.AccountFileService;
import priv.muller.util.CommonUtil;
import priv.muller.util.SpringBeanUtil;

import java.util.Objects;

@Slf4j
@Service
public class AccountFileServiceImpl implements AccountFileService {

    @Autowired
    private MinioConfig minioConfig;

    @Autowired
    private StorageMapper storageMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private StoreEngine fileStoreEngine;


    @Autowired
    private AccountFileMapper accountFileMapper;
    /**
     * 文件上传
     * 1、上传到存储引擎
     * 2、保存文件关系
     * 3、保存账号和文件的关系
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fileUpload(FileUploadReq req) {
        //1.检查用户的存储空间是否足够
        boolean storageEnough = checkAndUpdateCapacity(req.getAccountId(),req.getFileSize());
        if(storageEnough){
            //2.将文件先上传到存储引擎
            String storeFileObjectKey = storeFile(req);
            //3.保存用户和文件的关系
            //保存文件关系 + 保存账号和文件的关系
            saveFileAndAccountFile( req,storeFileObjectKey);
        }else {
            throw new BizException(BizCodeEnum.FILE_STORAGE_NOT_ENOUGH);
        }


    }
    /**
     * 检查存储空间和更新存储空间
     * @param accountId
     * @param fileSize
     * @return
     */
    public boolean checkAndUpdateCapacity(Long accountId, Long fileSize) {
        StorageDO storageDO = storageMapper.selectOne(new QueryWrapper<StorageDO>().eq("account_id", accountId));
        Long totalSize = storageDO.getTotalSize();
        if(storageDO.getUsedSize() + fileSize <= totalSize){
            storageDO.setUsedSize(storageDO.getUsedSize() + fileSize);
            storageMapper.updateById(storageDO);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 上传文件到存储引擎，返回存储的文件路径
     * @param req
     * @return
     */
    private String storeFile(FileUploadReq req) {

        String objectKey = CommonUtil.getFilePath(req.getFilename());
        fileStoreEngine.upload(minioConfig.getBucketName(), objectKey, req.getFile());
        return objectKey;
    }

    /**
     * 保存文件和账号文件的关系到数据库
     * @param req
     * @param storeFileObjectKey
     */
    @Override
    public void saveFileAndAccountFile(FileUploadReq req, String storeFileObjectKey) {
        //保存文件
        FileDO fileDO = saveFile(req,storeFileObjectKey);

        //保存文件账号关系
        AccountFileDTO accountFileDTO = AccountFileDTO.builder()
                .accountId(req.getAccountId())
                .parentId(req.getParentId())
                .fileId(fileDO.getId())
                .fileName(req.getFilename())
                .isDir(FolderFlagEnum.NO.getCode())
                .fileSuffix(fileDO.getFileSuffix())
                .fileSize(req.getFileSize())
                .fileType(FileTypeEnum.fromExtension(fileDO.getFileSuffix()).name())
                .build();
        saveAccountFile(accountFileDTO);
    }

    private FileDO saveFile(FileUploadReq req, String storeFileObjectKey) {
        FileDO fileDO = new FileDO();
        fileDO.setAccountId(req.getAccountId());
        fileDO.setFileName(req.getFilename());
        fileDO.setFileSize(req.getFile() !=null ? req.getFile().getSize():req.getFileSize());
        fileDO.setFileSuffix(CommonUtil.getFileSuffix(req.getFilename()));
        fileDO.setObjectKey(storeFileObjectKey);
        fileDO.setIdentifier(req.getIdentifier());
        fileMapper.insert(fileDO);
        return fileDO;
    }

    /**
     * 处理用户和文件的关系，存储文件和文件夹都是可以的
     *
     * 1、检查父文件是否存在
     * 2、检查文件是否重复
     * 3、保存相关文件关系
     *
     * @param accountFileDTO
     * @return
     */
    private Long saveAccountFile(AccountFileDTO accountFileDTO) {
        //检查父文件是否存在
        checkParentFileId(accountFileDTO);

        AccountFileDO accountFileDO = SpringBeanUtil.copyProperties(accountFileDTO, AccountFileDO.class);

        //检查文件是否重复 aa  aa(1) aa(2)
        processFileNameDuplicate(accountFileDO);

        //保存相关文件关系
        accountFileMapper.insert(accountFileDO);

        return accountFileDO.getId();
    }

    /**
     * 检查父文件是否存在
     * @param accountFileDTO
     */
    private void checkParentFileId(AccountFileDTO accountFileDTO) {
        if(accountFileDTO.getParentId()!=0){
            AccountFileDO accountFileDO = accountFileMapper.selectOne(
                    new QueryWrapper<AccountFileDO>()
                            .eq("id", accountFileDTO.getParentId())
                            .eq("account_id", accountFileDTO.getAccountId()));

            if(accountFileDO == null){
                throw new BizException(BizCodeEnum.FILE_NOT_EXISTS);
            }
        }
    }
    /**
     * 处理文件是否重复,
     *  文件夹重复和文件名重复处理规则不一样
     * @param accountFileDO
     */
    public Long processFileNameDuplicate(AccountFileDO accountFileDO) {

        Long selectCount = accountFileMapper.selectCount(new QueryWrapper<AccountFileDO>()
                .eq("account_id", accountFileDO.getAccountId())
                .eq("parent_id", accountFileDO.getParentId())
                .eq("is_dir", accountFileDO.getIsDir())
                .eq("file_name", accountFileDO.getFileName()));

        if(selectCount>0){
            //处理重复文件夹
            if(Objects.equals(accountFileDO.getIsDir(), FolderFlagEnum.YES.getCode())){
                accountFileDO.setFileName(accountFileDO.getFileName()+"_"+System.currentTimeMillis());
            }else {
                //处理重复文件名,提取文件拓展名
                String[] split = accountFileDO.getFileName().split("\\.");
                accountFileDO.setFileName(split[0]+"_"+System.currentTimeMillis()+"."+split[1]);
            }
        }

        return selectCount;

    }
}
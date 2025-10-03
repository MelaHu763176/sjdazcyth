# sjdazcyth

数据档案一体化平台

# minio部署

```shell
docker run 
-d --restart=always 
--name minio 
--hostname minio-server -p 9000:9000 -p 9001:9001 
-v /minio/data:/bitnami/minio/data 
-e MINIO_ROOT_USER="minio_root" 
-e MINIO_ROOT_PASSWORD="minio_123456" 
-e MINIO_DEFAULT_BUCKETS="bucket" 
-e "MINI0_SERVER_URL=http://192.168.19.152:9000" 
bitnami/minio:2023.12.7
```



# AWS-S3对象存储服务API

AWS-S3通用存储协议介绍和项目依赖配置

● 什么是Amazon S3 ○ Amazon S3（Amazon Simple Storage Service）是亚马逊提供的一种对象存储服务，行业领先的可扩展性、数据可用性和性能 ○ 就类似阿里云OSS、七牛云OSS、MinIO等多个存储服务一样

● Amazon S3协议 ○ 是Amazon Simple Storage Service（简称Amazon S3）的接口规范 ○ 它是一种基于HTTP协议的RESTful API，用于访问Amazon Web Services（AWS）提供的对象存储服务 ○ S3-API: https://docs.aws.amazon.com/zh\_cn/AmazonS3/latest/API/API\_Operations\_Amazon\_Simple\_Storage\_Service.html ○ 支持阿里云OSS、七牛云OSS（对象存储服务） ■ 在一定程度上与Amazon S3协议兼容，可以使用S3 API来操作OSS多数操作 ■ 存在一些差异，如ACL权限定义、存储类型处理，需要单独处理

● 支持MinIO ○ 兼容Amazon S3协议的对象存储服务器，它提供了与Amazon S3完全相同的S3 API兼容性 ○ 在公共云、私有云中，MinIO支持广泛的S3 API，包括S3 Select和AWS Signature V4，复杂的查询和身份验证。 ○ Amazon S3构建的应用程序可以无缝迁移到MinIO，无需任何代码更改

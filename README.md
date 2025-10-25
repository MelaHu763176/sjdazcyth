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

# 文件秒传设计

真实文件和用户文件目录关系是分开的，真实文件存储后会返回一个唯一标识，然后用户与这个唯一标识关联。对于全盘而言，假设有相同的文件上传，可以实现秒传，因为盘内已存在该文件，而区分文件是否一样则通过这个唯一标识判断。

那么如何提取该唯一标识呢？

有几种方式，第一哈希函数，虽然能保证唯一性，但碰撞概率比较低，且计算消耗较高；

第二种基于文件内容生成，可能存在误判，不同文件产生相同的指纹，且计算消耗也高；

最后一种则是基于文件元数据组合，即通过文件大小、创建时间，修改时间。但可能存在不稳定，因为修改时间可能会随时改变

# 数据表关系

![1759478662960.png](assets/1759478662960.png)

# 关于网盘容量和根目录初始化配置的设计思考

新用户注册，有默认网盘存储容量和根目录，那么这个是什么时候进行初始化呢？

答：可在用户注册时初始化网盘的存储空间，在简单场景中在代码中直接写死调用，但如果用户大对于场景丰富，可以使用消息队列。即用户注册时发送一个注册的消息，由网盘存储空间的消费者订阅消费，然后触发相关的业务，根目录初始化也可以放在此处初始化。举一反三，其他场景如折扣、拉新都可以通过消费这个消息来触发各自业务。

# 关于使用拦截器使用ThreadLocal传递用户信息

为了解决http的无状态用户信息传递的问题，和传统的web项目不同，传统项目可以通过session获取的方式获取当前请求的会话信息，进而提取用户信息。但对于前后端分离项目而言，就不能使用此种方式了。可以通过前端存储加密字符串，在后端解密，从而获取用户信息。这种加密方式也就是所使用的jwt签名令牌方式，目的以最大限度的保存用户信息；

那么这种jwt解密的用户信息如何在conroller、servcie、mapper中传递呢?如果单靠传参的方式，代码的可维护性不高，另一种方式则使用的ThreadLocal来传递。

基于ThreadLocal的特性，他允许每个线程内独立保存信息，在同个线程内的其他方法可获取相同的信息。

```
应用场景：ThreadLocal用作每个线程内需要独立保存信息，方便同个线程的其他方法获取该信息的场景。每个线程获取到的信息可能都是不一样的，前面执行的方法保存了信息后，后续方法可以通过ThreadLocal直接获取到。类似与全局变量的概念，比如用户登录令牌解密后的信息传递。
```

搭配拦截器，我们就能轻易实现用户信息在各个业务环节（conroller/service/mapper）中都能获取。

结合前面的前后端token的场景，整个场景可以为用户登录完成后后端将用户信息签名传递token到前端保存，当前端请求时，携带该token传递到后端，后端接收到该请求时，会由拦截器先行接收，对token进行解密，还原为原用户信息，然后存储到ThreadLocal线程对象中，然后开始主业务请求，在控制类后业务类中可通过TheadLocal.get来获得用户信息。最后完成请求后会对线程对象进行销毁。

# 关于越权处理须知

文件处理时，需加入account_id字段校验

# 关于重复文件夹或文件处理问题

如果用户重复创建或复制了相同文件夹或文件，对于新文件后面增加后缀即可

```java


//处理重复文件夹
if(Objects.equals(accountFileDO.getIsDir(), FolderFlagEnum.YES.getCode())){
     accountFileDO.setFileName(accountFileDO.getFileName()+"_"+System.currentTimeMillis());
}else {
//处理重复文件名,提取文件拓展名
     String[] split = accountFileDO.getFileName().split("\\.");
     accountFileDO.setFileName(split[0]+"_"+System.currentTimeMillis()+"."+split[1]);
}
```

# 关于文件重命名

检查文件是否存在

新旧文件名称不能相同

同层级文件名称不能相同

# 关于文件树的构建

移动、复制和转存操作时，需展示多层级文件夹列表和子文件夹列表，即将数据库中数据（如图1）读取构建成json数据（如图2）

实现方式有很多，可用递归和非递归，此处使用非递归，在内存中操作，通过分组或遍历进行处理

![image.png](assets/image.png)

```json
{
            "id": 1973745233480916993,
            "parentId": 0,
            "label": "全部文件夹",
            "children": [
                {
                    "id": 1974320085191368706,
                    "parentId": 1973745233480916993,
                    "label": "spring",
                    "children": []
                },
                {
                    "id": 1974319962231152642,
                    "parentId": 1973745233480916993,
                    "label": "ai",
                    "children": [
                        {
                            "id": 1974320255043903489,
                            "parentId": 1974319962231152642,
                            "label": "langchain",
                            "children": []
                        }
                    ]
                }
            ]
        }
```

```java
//查询用户全部文件夹
        List<AccountFileDO> folderList = accountFileMapper.selectList(new QueryWrapper<AccountFileDO>()
                .eq("account_id", accountId)
                .eq("is_dir", FolderFlagEnum.YES.getCode())
        );

        if(CollectionUtils.isEmpty(folderList)){
            return List.of();
        }
        //构建一个map， key是文件ID，value是文件对象 相当于一个数据源
        Map<Long, FolderTreeNodeDTO> folderMap = folderList.stream()
                .collect(Collectors.toMap(AccountFileDO::getId, accountFileDO ->
                        FolderTreeNodeDTO.builder()
                                .id(accountFileDO.getId())
                                .parentId(accountFileDO.getParentId())
                                .label(accountFileDO.getFileName())
                                .children(new ArrayList<>())
                                .build()
                ));

        //构建文件树，遍历数据源，为每个文件夹找到子文件夹
        for (FolderTreeNodeDTO node : folderMap.values()) {
            Long parentId = node.getParentId();

            if(parentId!=null && folderMap.containsKey(parentId)){
                //获取父文件
                FolderTreeNodeDTO parentNode = folderMap.get(parentId);
                //获取父文件夹的子节点位置
                List<FolderTreeNodeDTO> children = parentNode.getChildren();
                //将当前节点添加到对应的文件夹里面
                children.add(node);
            }

        }

        //过滤根节点，保留parentID是0的
        List<FolderTreeNodeDTO> rootFolderList = folderMap.values().stream()
                .filter(node -> Objects.equals(node.getParentId(), 0L))
                .collect(Collectors.toList());


        return rootFolderList;
```


# 查看他人分享文件列表业务设计
需思考如何实现两个问题：
1）通过别人分享的文件链接，查看对应分享的文件（注意:需要先登录自己网盘才可以查看分享信息）；
2）选择部分或者全部分享的文件，或者进入对应的文件夹，转存到自己网盘；

思考路径：
如果是需要提取取码的链接，如何第一次校验，后续可以不校验，实现安全访问？如何确保不被越权访问？



前后端交互逻辑和解决方案
用户访问分享链接，统一调用后端【基本分享信息】接口 /api/share/v1/visit?shareId=XXXX
上述接口会返回基本分享信息，包括：是否需要提取码、分享人信息等
前端根据访问的基本分享信息，是否需要提取码，分两个情况
情况一 
免提取码，基本分享信息返回里面有token，请求头携带过去即可直接使用访问对应的分享文件
调用 /api/share/v1/detail 接口，访问链接之间进入文件列表
情况二
需要提取码，基本分享信息里面无token，先进入提取码界面，可以看到分享人信息
输入提取码后校验，成功则后端会返回token，请求头携带过去即可直接使用访问对应的分享文件
调用 /api/share/v1/detail 接口，访问链接之间进入文件列表
进入分享文件的子文件，则统一调用接口 /api/share/v1/list_share_file (需要携带token)


业务逻辑：
检查分享状态
查询分享记录实体
插叙分享者信息
判断是否需要生成校验码，不需要的话可以直接生成分享token


# 关于网盘回收站的设计
文件删除：
1）用户删除文件或文件夹时，系统并非真正删除数据，而是将其移动到回收站。
2）文件在回收站中保留一定时间，用户可以选择手动清空回收站，释放存储空间。
3）如果是文件夹，就只显示文件夹，不显示里面的其他子文件
文件恢复：
1）用户可以从回收站中恢复误删的文件或文件夹，恢复到原来的位置。

接口说明
1）获取回收站文件列表：查询回收站中的文件信息。
2）恢复文件：将文件从回收站移动到原路径。
3）清空回收站：删除回收站中的所有文件。

实现说明
1）文件删除时，将文件信息插入回收站表，并更新文件表中的状态字段。
2）文件恢复时，将文件信息从回收站表删除，并更新文件表中的状态字段和路径字段。
    注意：恢复时需检查目标路径是否存在同名文件夹。执行恢复后需调整文件空间大小
3）文件彻底删除时，从回收站表和文件关联表中删除文件信息。



# 网盘搜索功能
用户可以根据文件名称，搜索网盘相关的文件
暂时采用Mysql，因此用普通的Like查询
也可以使用ElasticSearch，将Mysql数据同步到ES中完成更多搜索


# 网盘下载功能需求
网盘下载功能的业务逻辑 
1）用户能够通过网盘下载存储在网盘中的文件。 
2）支持常见文件格式的下载，包括文档、图片、视频、音频等

思考：
1）非文件夹下载如何操作？ 
2）文件夹下载如何操作？
3）文件下载可以通过前端直接请求 MinIO 或通过后端请求 MinIO 实现
4）前端直接请求 MinIO 下载（预签名 URL 方式，和上传一样）


方式一：
后端生成一个带有时间限制和权限的预签名 URL（Presigned URL） 
前端通过该 URL 直接访问 MinIO 下载文件。

优点 
● 文件传输不经过后端服务器，利用 MinIO 的带宽和并发能力，适合大文件（如视频、镜像）。 
● 前端可并行下载多个文件，无需后端干预。 
● 后端只需生成预签名 URL，无需处理文件流，降低 CPU 和内存消耗

缺点
预签名 URL 泄露：如果 URL 被恶意截获，可能被未授权用户访问，缩短 URL 有效期（如 5 分钟）
部分浏览器可能限制直接下载大文件（如内存不足），需前端分片下载或断点 续传



方式二：
客户端请求后端 API，后端从 MinIO 读取文件流
通过 HTTP Response 将文件流传给客户端。

优点
○ 可实时压缩文件（如 ZIP）、添加水印或加密。
○ 可动态拼接文件（如分片存储的文件合并）
○ 可在下载前校验用户角色、文件归属、付费状态等复杂逻辑。
○ 可记录所有下载日志，方便审计。

缺点
○ 大文件下载会占用后端带宽和连接资源，可能拖慢其他API。
○ 需处理文件流中断、超时、断点续传等问题。
○ 高并发下载时需横向扩展后端服务器，增加运维成本。

最终方案

非文件夹下载方案： 采用前端直接下载方式，后端请求minio获取预签名地址给前端，前端进行下载，适合单个文件下载

文件夹下载方案（拓展思路即可）：
前端处理多个文件会有问题，且需要从MinIO获取每个文件的预签名URL，然后在前端打包，这不太现实
也可以是用户请求打包多个文件，后端从MinIO获取这些文件，压缩成一个ZIP，提供给用户下载（资源占用多）
我们客户端是使用浏览器进行，并非自研客户端，如果是自研客户端则可以实现更多功能
需要客户端原生开发，包括文件夹处理等，不同系统的客户端也不一样，成本大
比如断点续传、直接到存储引擎下载文件夹等功能都有实现，类似百度网盘
客户端下载文件夹，可以获得文件夹里面全部的文件的关联关系
根据情况一层层文件夹下载和处理，就不用经过后端服务器压缩再返回了
因此Web端很少实现文件夹下载，限制很多

关于下载断点续传
在文件下载过程中，能够在下载中断后从上次停止的位置继续下载的技术
核心在于能够记录下载进度，并在重新开始下载时，从上次中断的位置继续，而不是从头开始
依赖于HTTP协议中的Range请求头，当浏览器发起下载请求时，可以通过Range头指定从文件的某个字节开始下载
用户存在多个浏览器，版本问题、厂商问题等导致不支持，因此web端不做这个功能


#SpringBoot整合大模型调用实战《上》

需求：
1、有了大模型接口，业务项目如何开发呢？
解决方案
自定义HttpClient客户端，在Java代码中通过HTTP请求发送指令并获取结果，适合快速开发和小规模应用
使用Spring-Starter,比如spring-ai-openai 进行整合，不用自己封装，比较多中大型项目采用
使用Spring AI Alibaba 创建应用，使用里面封装的工具进行调用，比SpringAI更好用

2、本地还是在线的模型选择？
开发的时候建议选择在线大模型，可以灵活切换，避免本地开发卡顿的情况

3、什么是SpringAI？
Spring 生态中面向 AI 工程的应用框架，简化生成式 AI 的集成与开发
支持跨模型兼容性（如 OpenAI、DeepSeek 等）和多种功能（聊天、嵌入、流式输出等）
地址：https://spring.io/projects/spring-ai

4、什么是大模型服务平台百炼？
是一站式的大模型开发及应用构建平台。不论是开发者还是业务人员，都能深入参与大模型应用的设计和构建。
可以通过简单的界面操作，在5分钟内开发出一款大模型应用，在几小时内训练出一个专属模型
地址：https://bailian.console.alyun.com/

项目配置实战
添加依赖
https://mvnrepository.com/artifact/com.alibaba/dashscope-sdk-java

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dashscope-sdk-java</artifactId>
    <version>2.18.2</version>
    <exclusions>
        <exclusion>
            <artifactId>lombok</artifactId>
            <groupId>org.projectlombok</groupId>
        </exclusion>
    </exclusions>
</dependency>
```
配置密钥
```yaml
ai:
  key: sk-011xxxxxxxxxxxxxa2

```
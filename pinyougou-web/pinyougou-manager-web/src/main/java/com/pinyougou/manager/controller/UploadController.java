package com.pinyougou.manager.controller;

import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UploadController {

    //注入属性文件中的属性名对应的属性值,服务器访问地址
    @Value("${fileServerUrl}")
    private String fileServerUrl;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Map<String, Object> upload
    (@RequestParam("file") MultipartFile multipartFile) {
        //封装前端需要的响应数据
        Map<String, Object> data = new HashMap<>();
        //先put500异常状态码,后面上传文件没问题了再put200状态码顶掉500的
        data.put("status", 500);
        try {

            /**获取源文件名*/
            String originalFilename = multipartFile.getOriginalFilename();
            /** 加载配置文件，产生该文件的绝对路径 */
            String confFilename = this.getClass()
                    .getResource("/fastdfs_client.conf").getPath();
            /** 初始化客户端全局的对象 */
            ClientGlobal.init(confFilename);
            /** 创建存储客户端对象 */
            StorageClient storageClient = new StorageClient();

            /** 上传文件到FastDFS服务器 */
            //FilenameUtils.getExtension 文件名工具类,获取后缀名再commons的io包.就不用自己截取后缀名了.
            String[] arr = storageClient.upload_file(multipartFile.getBytes(),
                    FilenameUtils.getExtension(originalFilename), null);


            /**拼接返回的url和ip地址,拼装成完整的url用/隔开,
             * 服务器访问地址写死不好,用配置文件加载里面的值就好了,编写属性文件,首先给spring容器加载,
             * 然后在实体类定义成员属性中用@Value拿该属性,注入进来这里引用就好了*/
            StringBuilder url = new StringBuilder(fileServerUrl);
            for (String str : arr) {
                url.append("/" + str);
            }
            data.put("status", 200);
            data.put("url", url.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}

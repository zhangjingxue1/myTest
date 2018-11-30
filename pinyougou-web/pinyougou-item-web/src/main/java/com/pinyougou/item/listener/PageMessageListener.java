package com.pinyougou.item.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

//消息监听器(生成静态html页面)
public class PageMessageListener implements
        SessionAwareMessageListener<TextMessage> {
    //注入静态页面存储路径
    @Value("${page.dir}")
    private String pageDir;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @Override
    public void onMessage(TextMessage textMessage, Session session) throws JMSException {
        try {
            System.out.println("=====PageMessageListener======");
            //获取消息内容
            String goodsId = textMessage.getText();
            System.out.println("goodsId:" + goodsId);
            //根据模版文件获取模版对象,这里要加后缀名.
            //因为没有用配置的freeMarker视图解析器通过控制器进来的,
            //这个是自己引入进来的,所以要自己返回数组模型,并且自己输出视图
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate("item.ftl");
            //获取数据模型
            Map<String, Object> dataModel = goodsService.getGoods(Long.valueOf(goodsId));
            //创建输出转换流,因为输出到页面不是UTF-8编码的,要用这个转换流输出因为有个转码的功能
            OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(pageDir + goodsId + ".html"), "UTF-8");
            //填充模版生成静态的html页面,把数据模型输出给ftl页面让他生成静态的html页面
            template.process(dataModel, writer);
            //关流
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package com.pinyougou.item.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

/**
 * 删除静态页面消息监听器
 */
public class DeleteMessageListener implements SessionAwareMessageListener<ObjectMessage> {

    @Value("${page.dir}")
    private String pageDir;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        try {
            //获取消息内容
            Long[] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println("===DeleteMessageListener===");
            //获取goodsId
            for (Long goodsId : goodsIds) {
                //找到这些文件
                File file = new File(pageDir + goodsId + ".html");
                //判断文件是否存在
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}

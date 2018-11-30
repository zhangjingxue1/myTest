package com.pinyougou.search.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.ItemSearchService;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.util.Arrays;

/**
 * 删除商品所有消息监听器
 */
public class DeleteMessageListener implements
        SessionAwareMessageListener<ObjectMessage> {

    @Reference(timeout = 30000)
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        Long[] ids = (Long[]) objectMessage.getObject();
        System.out.println("===DeleteMessageListener===");
        //调用收索服务接口删除商品索引
        itemSearchService.delete(Arrays.asList(ids));
    }
}

package com.qf.listener;

import com.qf.entity.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.val;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class RabbitMQListener {

    @Autowired
    private Configuration configuration;

    @RabbitListener(queues = "goods_queue2")
    public void rabbitmqMsg(Goods goods){
        System.out.println("接收到MQ消息："+goods);

        //获取request对象
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
//        HttpServletRequest request = servletRequestAttributes.getRequest();

        //生成静态页
        String giname = goods.getGimage();
        String[] images = giname.split("\\|");
        try {
            //获得模板对象
            Template template = configuration.getTemplate("goodsitem.ftl");
            //商品数据
            Map<String,Object> map = new HashMap<>();
            map.put("goods",goods);
            map.put("images",images);
//            map.put("context",request.getContextPath());
            //生成静态页
            //获得classpath路径

            String path = this.getClass().getResource("/static/page/").getPath() + goods.getId() +".html";
            template.process(map, new FileWriter(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

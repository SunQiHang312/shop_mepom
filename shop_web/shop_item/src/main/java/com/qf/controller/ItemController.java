package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.entity.Goods;
import com.qf.service.IGoodsService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping("/item")
public class ItemController {

    @Reference
    private IGoodsService goodsService;

    @Autowired
    private Configuration configuration;

    @RequestMapping("createHtml")
    public String createHtml(int gid, HttpServletRequest request){
        Goods goods = goodsService.queryById(gid);
        String gimage = goods.getGimage();
        String[] images = gimage.split("\\|");
        try {
            //获得模板对象
            Template template = configuration.getTemplate("goodsitem.ftl");
            //商品数据
            Map<String,Object> map = new HashMap<>();
            map.put("goods",goods);
            map.put("images",images);
            map.put("context",request.getContextPath());
            //生成静态页
            //获得classpath路径

            String path = this.getClass().getResource("/static/page/").getPath() + goods.getId() +".html";
            template.process(map, new FileWriter(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

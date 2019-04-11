package com.qf.com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.entity.Goods;
import com.qf.service.IGoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private IGoodsService goodsService;

    /*
    * 查询商品列表
    * */
    @RequestMapping("/list")
    public String goodsList(Model model){

        List<Goods> goods = goodsService.queryAll();
        model.addAttribute("goods",goods);
        return "goodslist";
    }

    /*
    *
    * 添加商品
    *
    * */
    @RequestMapping("/insert")
    public String insert(Goods goods){

        return null;
    }
}

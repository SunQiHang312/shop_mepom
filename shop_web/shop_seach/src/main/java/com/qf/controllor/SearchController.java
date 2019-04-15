package com.qf.controllor;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.entity.Goods;
import com.qf.service.ISearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Reference
    private ISearchService searchService;

    /*
    * 根据关键词搜索信息
    * */
    @RequestMapping("/searchByKeyWord")
    public String searchByKeyWord(String keyword, Model model){
//        System.out.println("搜索的关键字："+keyword);
        List<Goods> goods = searchService.seachGoods(keyword);
        model.addAttribute("goods",goods);
//        System.out.println("服务获得搜索结果："+goods);
        return "searchlist";
    }
}

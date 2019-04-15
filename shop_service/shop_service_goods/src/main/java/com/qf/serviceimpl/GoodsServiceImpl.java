package com.qf.serviceimpl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.qf.dao.GoodsMapper;
import com.qf.entity.Goods;
import com.qf.service.IGoodsService;
import com.qf.service.ISearchService;
import com.qf.shop_service_goods.RabbitMQConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;

import java.util.List;

@Service
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Reference
    private ISearchService searchService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public List<Goods> queryAll() {

        return goodsMapper.selectList(null);
    }

    @Override
    public int insert(Goods goods) {
        int result = goodsMapper.insert(goods);
//        同步索引库
//        searchService.insertGoods(goods);

//        将信息添加到rabbitmq
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.FANOUT_NAME,"",goods);
        return result;
    }

    @Override
    public Goods queryById(int gid) {

        return goodsMapper.selectById(gid);
    }
}

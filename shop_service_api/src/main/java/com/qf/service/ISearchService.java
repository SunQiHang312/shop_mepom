package com.qf.service;

import com.qf.entity.Goods;

import java.util.List;

public interface ISearchService {

    List<Goods> seachGoods(String keyword);

    int insertGoods(Goods goods);
}

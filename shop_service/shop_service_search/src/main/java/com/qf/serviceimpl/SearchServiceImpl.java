package com.qf.serviceimpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qf.entity.Goods;
import com.qf.service.ISearchService;
import lombok.val;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements ISearchService {

    @Autowired
    private SolrClient solrClient;

    @Override
    public List<Goods> seachGoods(String keyword) {
        System.out.println("搜索服务获得关键字："+keyword);
        SolrQuery solrQuery = new SolrQuery();
        if (keyword==null){
            //搜索全部
            solrQuery.setQuery(("*:*"));
        }else {
            //搜索具体关键字
            solrQuery.setQuery("gname:" + keyword+" || ginfo:"+keyword);
        }

        //开启高亮
        solrQuery.setHighlight(true);
        solrQuery.setHighlightSimplePre("<font color='red'>");
        solrQuery.setHighlightSimplePost("</font>");
        solrQuery.addHighlightField("gname");

        List<Goods> list = new ArrayList<>();
        try {
            QueryResponse result = solrClient.query(solrQuery);

            Map<String, Map<String, List<String>>> highlighting = result.getHighlighting();

            SolrDocumentList results = result.getResults();

            for (SolrDocument document : results){
                Goods goods = new Goods();
                goods.setId(Integer.parseInt(document.get("id")+""));
                goods.setGname(document.get("gname")+"");
                goods.setGprice(BigDecimal.valueOf(Double.parseDouble(document.get("gprice")+"")));
                goods.setGimage(document.get("gimage")+"");
                goods.setGsave(Integer.parseInt(document.get("gsave")+""));

                //判断高亮
                if(highlighting.containsKey(goods.getId()+"")){
                    Map<String,List<String>> stringListMap = highlighting.get(goods.getId() + "");
                    String gname = stringListMap.get("gname").get(0);
                    goods.setGname(gname);
                }

                list.add(goods);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int insertGoods(Goods goods) {
        SolrInputDocument document = new SolrInputDocument();
        document.setField("id",goods.getId());
        document.setField("gname",goods.getGname());
        document.setField("ginfo",goods.getGinfo());
        document.setField("gprice",goods.getGprice().doubleValue());
        document.setField("gsave",goods.getGsave());
        document.setField("gimage",goods.getGimage());

        try {
            solrClient.add(document);
            solrClient.commit();
            return 1;
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

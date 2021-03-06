package com.xxxtai.express.dao;

import com.xxxtai.express.constant.Constant;
import com.xxxtai.express.model.EdgeCost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Vector;

@Component
@Slf4j(topic = "develop")
public class CacheExecutor {
    @Resource
    private EdgeCostDAO edgeCostDAO;
    private Vector<EdgeCost> caches = new Vector<>();

    public synchronized void batchInsert(EdgeCost edgeCost){
        caches.add(edgeCost);
        if (caches.size() > Constant.EDGE_COST_CACHE_NUM) {
            edgeCostDAO.batchInsert(caches);
            caches.clear();
        }
    }
}

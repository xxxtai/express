package com.xxxtai.express.dao;

import com.xxxtai.express.model.EdgeCost;

import java.util.List;

public interface EdgeCostDAO {
    Integer insert(EdgeCost edgeCost);
    List<EdgeCost> getCostsByEdgeNum(Integer edgeNum);
}

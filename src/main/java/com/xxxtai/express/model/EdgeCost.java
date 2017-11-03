package com.xxxtai.express.model;

public class EdgeCost extends Base {
    private Integer edgeNum;
    private Long cost;
    private Integer agvNum;
    private Integer startNodeNum;
    private Integer endNodeNum;
    private Long targetCityCode;

    public Integer getEdgeNum() {
        return edgeNum;
    }

    public void setEdgeNum(Integer edgeNum) {
        this.edgeNum = edgeNum;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public Integer getAgvNum() {
        return agvNum;
    }

    public void setAgvNum(Integer agvNum) {
        this.agvNum = agvNum;
    }

    public Integer getStartNodeNum() {
        return startNodeNum;
    }

    public void setStartNodeNum(Integer startNodeNum) {
        this.startNodeNum = startNodeNum;
    }

    public Integer getEndNodeNum() {
        return endNodeNum;
    }

    public void setEndNodeNum(Integer endNodeNum) {
        this.endNodeNum = endNodeNum;
    }

    public Long getTargetCityCode() {
        return targetCityCode;
    }

    public void setTargetCityCode(Long targetCityCode) {
        this.targetCityCode = targetCityCode;
    }

    @Override
    public String toString(){
        return String.format("edgeNum:%s, agvNum:%s, cost:%s, startNodeNum:%s, endNodeNum:%s, targetCityCode:%s", this.edgeNum, this.agvNum, this.cost,this.startNodeNum, this.endNodeNum, this.targetCityCode);
    }
}

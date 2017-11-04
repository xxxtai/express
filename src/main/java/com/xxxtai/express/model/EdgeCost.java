package com.xxxtai.express.model;

public class EdgeCost extends Base {
    private Integer edgeNum;
    private Long cost;
    private Integer agvNum;
    private Integer startNodeNum;
    private Integer destinationNodeNum;
    private String targetCity;

    public Integer getEdgeNum() {
        return edgeNum;
    }

    public EdgeCost setEdgeNum(Integer edgeNum) {
        this.edgeNum = edgeNum;
        return this;
    }

    public Long getCost() {
        return cost;
    }

    public EdgeCost setCost(Long cost) {
        this.cost = cost;
        return this;
    }

    public Integer getAgvNum() {
        return agvNum;
    }

    public EdgeCost setAgvNum(Integer agvNum) {
        this.agvNum = agvNum;
        return this;
    }

    public Integer getStartNodeNum() {
        return startNodeNum;
    }

    public EdgeCost setStartNodeNum(Integer startNodeNum) {
        this.startNodeNum = startNodeNum;
        return this;
    }

    public Integer getDestinationNodeNum() {
        return destinationNodeNum;
    }

    public EdgeCost setDestinationNodeNum(Integer destinationNodeNum) {
        this.destinationNodeNum = destinationNodeNum;
        return this;
    }

    public String getTargetCity() {
        return targetCity;
    }

    public EdgeCost setTargetCity(String targetCity) {
        this.targetCity = targetCity;
        return this;
    }

    @Override
    public String toString(){
        return String.format("edgeNum:%s, agvNum:%s, cost:%s, startNodeNum:%s, destinationNodeNum:%s, targetCity:%s", this.edgeNum, this.agvNum, this.cost,this.startNodeNum, this.destinationNodeNum, this.targetCity);
    }
}

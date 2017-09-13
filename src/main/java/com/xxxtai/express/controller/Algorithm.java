package com.xxxtai.express.controller;


import com.xxxtai.express.model.Edge;
import com.xxxtai.express.model.Path;

public interface Algorithm {
    Path findRoute(Edge startEdge, int endNodeNum, boolean isBackToEntrance);
}

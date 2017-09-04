package com.xxxtai.controller;


import com.xxxtai.model.Edge;
import com.xxxtai.model.Path;

public interface Algorithm {
	Path findRoute(Edge startEdge, int endNodeNum, boolean isBackToEntrance);
}

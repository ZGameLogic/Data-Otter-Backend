package com.zgamelogic.controllers;

import com.zgamelogic.data.serializable.nodes.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.LinkedList;

@Slf4j
@RestController
@RequestMapping("node")
public class NodeController {

    private LinkedList<Node> nodes;

    @PostConstruct
    private void init(){
        nodes = new LinkedList<>();
    }

    @PostMapping("register")
    private Node registerNode(){
        Node node = new Node("node-" + nodes.size());
        nodes.add(node);
        log.info("Registering " + node.getId());
        return node;
    }
}

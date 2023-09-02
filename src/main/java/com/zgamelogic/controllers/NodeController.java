package com.zgamelogic.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.zgamelogic.data.serializable.nodes.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;


@Slf4j
@RestController
@RequestMapping("node")
public class NodeController {

    public final static String DATA_DIR = "data";

    @PostMapping("register")
    private Node registerNode(){
        LinkedList<Node> nodes = loadNodes();
        Node node = new Node("node-" + nodes.size());
        log.info("Registering " + node.getId());
        nodes.add(node);
        saveNodes(nodes);
        return node;
    }

    private LinkedList<Node> loadNodes(){
        File dataDir = new File(DATA_DIR);
        if(!dataDir.exists()) dataDir.mkdir();
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(new File(DATA_DIR + "/nodes.json"), new TypeReference<LinkedList<Node>>() {});
        } catch (IOException e) {
            return new LinkedList<>();
        }
    }

    private void saveNodes(LinkedList<Node> nodes){
        ObjectWriter ow = new ObjectMapper().writer(new DefaultPrettyPrinter());
        try {
            ow.writeValue(new File(DATA_DIR + "/nodes.json"), nodes);
        } catch (IOException e) {
            log.error("Unable to save node", e);
        }
    }
}

package com.zgamelogic.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zgamelogic.data.nodeConfiguration.NodeConfiguration;
import com.zgamelogic.data.nodeConfiguration.NodeConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.zgamelogic.data.Constants.MASTER_NODE_NAME;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NodeControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private NodeConfigurationRepository nodeConfigurationRepository;
    @MockBean
    private NodeConfiguration masterNode;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        Mockito.when(masterNode.getName()).thenReturn(MASTER_NODE_NAME);
        Mockito.when(masterNode.getId()).thenReturn(1L);
    }

    @Test
    void registerNode() throws Exception {
        NodeConfiguration payload = new NodeConfiguration("test");
        NodeConfiguration response = new NodeConfiguration(1L, "test");
        Mockito.when(nodeConfigurationRepository.save(Mockito.any(NodeConfiguration.class))).thenReturn(response);
        mockMvc.perform(
                post("/nodes").content(mapper.writeValueAsString(payload)).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}

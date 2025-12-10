package com.zgamelogic.services;

import io.fabric8.kubernetes.client.KubernetesClient;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KubernetesService {
    private final KubernetesClient client;

    @PostConstruct
    public void init(){
        client.apps().deployments().inNamespace("default").list().getItems().forEach(deployment -> {
            System.out.println(deployment.getMetadata().getName());
            System.out.println(deployment.getStatus());
        });
//        client.pods().inAnyNamespace().list().getItems().forEach(pod -> {
//            System.out.println(pod.getMetadata().getName() + " " + pod.getStatus());
//        });
        client.nodes().list().getItems().forEach(node -> {
            System.out.println("Name: " + node.getMetadata().getName());
            System.out.println("Labels: " + node.getMetadata().getLabels());
            System.out.println("Capacity: " + node.getStatus().getCapacity());
            System.out.println("Allocatable: " + node.getStatus().getAllocatable());
            System.out.println("Conditions: " + node.getStatus().getConditions());
            System.out.println("Addresses: " + node.getStatus().getAddresses());
            System.out.println("Kubelet Version: " + node.getStatus().getNodeInfo().getKubeletVersion());
            System.out.println("-----------------------------------");
        });
    }
}

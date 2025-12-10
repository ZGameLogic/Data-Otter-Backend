package com.zgamelogic.dataotter.kubernetes;

import com.zgamelogic.dataotter.kubernetes.events.DeploymentEvent;
import com.zgamelogic.dataotter.kubernetes.events.NodeEvent;
import com.zgamelogic.dataotter.kubernetes.events.PodEvent;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KubernetesService {
    private final KubernetesClient client;
    private final ApplicationEventPublisher publisher;

    @PostConstruct
    public void init(){
//        client.apps().deployments().inNamespace("default").list().getItems().forEach(deployment -> {
//            System.out.println(deployment.getMetadata().getName());
//            System.out.println(deployment.getStatus());
//        });
//        client.pods().inNamespace("default").list().getItems().forEach(pod -> {
//
//        });
//        client.pods().inAnyNamespace().list().getItems().forEach(pod -> {
//            System.out.println(pod.getMetadata().getName() + " " + pod.getStatus());
//        });
//        client.nodes().list().getItems().forEach(node -> {
//            System.out.println("Name: " + node.getMetadata().getName());
//            System.out.println("Labels: " + node.getMetadata().getLabels());
//            System.out.println("Capacity: " + node.getStatus().getCapacity());
//            System.out.println("Allocatable: " + node.getStatus().getAllocatable());
//            System.out.println("Conditions: " + node.getStatus().getConditions());
//            System.out.println("Addresses: " + node.getStatus().getAddresses());
//            System.out.println("Kubelet Version: " + node.getStatus().getNodeInfo().getKubeletVersion());
//            System.out.println("-----------------------------------");
//        });
        client.nodes().watch(new Watcher<>() {
            @Override
            public void eventReceived(Action action, Node node) {
                publisher.publishEvent(new NodeEvent(action, node));
            }

            @Override
            public void onClose(WatcherException e) {

            }
        });
        client.pods().inNamespace("default").watch(new Watcher<>() {
            @Override
            public void eventReceived(Action action, Pod resource) {
                publisher.publishEvent(new PodEvent(action, resource));
            }

            @Override
            public void onClose(WatcherException cause) {

            }
        });
        client.apps().deployments().inNamespace("default").watch(new Watcher<>() {
            @Override
            public void eventReceived(Action action, Deployment resource) {
                publisher.publishEvent(new DeploymentEvent(action, resource));
            }

            @Override
            public void onClose(WatcherException cause) {

            }
        });
    }
}

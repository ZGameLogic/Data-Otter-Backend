package com.zgamelogic.dataotter.kubernetes.events;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.Watcher;

public record DeploymentEvent(Watcher.Action action, Deployment deployment) {
}

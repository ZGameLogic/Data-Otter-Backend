package com.zgamelogic.dataotter.kubernetes.events;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.client.Watcher;

public record NodeEvent(Watcher.Action action, Node node) {
}

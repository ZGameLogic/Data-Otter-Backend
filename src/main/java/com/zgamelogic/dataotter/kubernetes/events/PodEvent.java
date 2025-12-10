package com.zgamelogic.dataotter.kubernetes.events;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.Watcher;

public record PodEvent(Watcher.Action action, Pod pod) {
}

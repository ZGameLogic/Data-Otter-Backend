package com.zgamelogic.data.nodeConfiguration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "nodes")
@NoArgsConstructor
@Getter
@ToString
public class NodeConfiguration {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    public NodeConfiguration(String name) {
        this.name = name;
    }

    public NodeConfiguration(long id) {
        this.id = id;
    }
}

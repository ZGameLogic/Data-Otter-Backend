package com.zgamelogic.data.agents;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Agent {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String os;

    public Agent(String name, String os) {
        this.name = name;
        this.os = os;
    }

    public Agent(long id){
        this.id = id;
    }
}

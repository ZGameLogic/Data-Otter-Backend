package com.zgamelogic.data.application;

import com.zgamelogic.data.tags.Tag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;

    @ManyToMany
    @JoinTable(
        name = "APPLICATION_TAGS",
        joinColumns = @JoinColumn(name = "application_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_name")
    )
    private Set<Tag> tags;

    public Application(long id){
        this.id = id;
    }

    public void update(Application application){
        if(application.getName() != null) this.name = application.getName();
        if(application.getDescription() != null) this.description = application.getDescription();
    }
}

package com.zgamelogic.data.application;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.data.tags.Tag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@Setter
public class Application {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "APPLICATION_TAGS",
        joinColumns = @JoinColumn(name = "application_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_name")
    )
    private Set<Tag> tags;

    @OneToMany(mappedBy = "id.application", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MonitorConfiguration> monitors;

    public Application(){
        tags = new HashSet<>();
        monitors = new HashSet<>();
    }

    public Application(long id){
        this();
        this.id = id;
    }

    public Application(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    public void update(Application application){
        if(application.getName() != null) this.name = application.getName();
        if(application.getDescription() != null) this.description = application.getDescription();
    }

    public void serialize(JsonGenerator gen) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", getId());
        gen.writeStringField("name", getName());
        gen.writeStringField("description", getDescription());
        gen.writeArrayFieldStart("monitor ids");
        for(MonitorConfiguration monitor : getMonitors()) gen.writeNumber(monitor.getId().getMonitorConfigurationId());
        gen.writeEndArray();
        gen.writeArrayFieldStart("tags");
        for(Tag tag : getTags()) gen.writeString(tag.getName());
        gen.writeEndArray();
        gen.writeEndObject();
    }

    public static Application deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        Application application = new Application();
        if(node.has("name")) application.setName(node.get("name").asText());
        if(node.has("description")) application.setDescription(node.get("description").asText());
        if(node.has("tags")){
            ObjectMapper om = new ObjectMapper();
            node.get("tags").elements().forEachRemaining(tagNode -> {
                try {
                    application.getTags().add(om.treeToValue(tagNode, Tag.class));
                } catch (JsonProcessingException ignored) {}
            });
        }
        return application;
    }
}

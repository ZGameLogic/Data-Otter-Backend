package com.zgamelogic.data.application;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.data.monitorConfiguration.MonitorConfiguration;
import com.zgamelogic.data.tags.Tag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(using = Application.ApplicationSerializer.class)
@JsonDeserialize(using = Application.ApplicationDeserializer.class)
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

    @OneToMany(mappedBy = "id.application", fetch = FetchType.EAGER)
    private Set<MonitorConfiguration> monitors;

    public Application(long id){
        this.id = id;
    }

    public Application(String name, String description) {
        this.name = name;
        this.description = description;
        tags = new HashSet<>();
        monitors = new HashSet<>();
    }

    public void update(Application application){
        if(application.getName() != null) this.name = application.getName();
        if(application.getDescription() != null) this.description = application.getDescription();
    }

    public static class ApplicationSerializer extends JsonSerializer<Application> {
        @Override
        public void serialize(Application value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("id", value.getId());
            gen.writeStringField("name", value.getName());
            gen.writeStringField("description", value.getDescription());
            gen.writeArrayFieldStart("monitors");
            for(MonitorConfiguration monitor : value.getMonitors()) gen.writeNumber(monitor.getId().getMonitorConfigurationId());
            gen.writeEndArray();
            gen.writeArrayFieldStart("tags");
            for(Tag tag : value.getTags()) gen.writeString(tag.getName());
            gen.writeEndArray();
            gen.writeEndObject();
        }
    }

    public static class ApplicationDeserializer extends JsonDeserializer<Application> {

        @Override
        public Application deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            String name = node.get("name").asText();
            String description = node.get("description").asText();
            Application application = new Application(name, description);
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
}

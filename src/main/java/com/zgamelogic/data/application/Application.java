package com.zgamelogic.data.application;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.data.tags.Tag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(using = Application.ApplicationSerializer.class)
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

    public static class ApplicationSerializer extends JsonSerializer<Application> {
        @Override
        public void serialize(Application value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("id", value.getId());
            gen.writeStringField("name", value.getName());
            gen.writeStringField("description", value.getDescription());
            gen.writeArrayFieldStart("tags");
            for(Tag tag : value.getTags()) {
                gen.writeStartObject();
                gen.writeStringField("name", tag.getName());
                if(tag.getDescription() != null) gen.writeStringField("description", tag.getDescription());
                gen.writeEndObject();
            }
            gen.writeEndArray();
            gen.writeEndObject();
        }
    }
}

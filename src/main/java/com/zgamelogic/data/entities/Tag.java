package com.zgamelogic.data.entities;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TAGS")
@JsonSerialize(using = Tag.TagSerializer.class)
@JsonDeserialize(using = Tag.TagDeserializer.class)
public class Tag {
    @Id
    private String name;
    private String description;

    @ManyToMany(mappedBy = "tags", cascade = CascadeType.REMOVE)
    private Set<Application> applications;

    public Tag(String name){
        this.name = name;
        applications = new HashSet<>();
    }

    public Tag(String name, String description){
        this(name);
        this.description = description;
    }

    public static class TagSerializer extends JsonSerializer<Tag> {
        @Override
        public void serialize(Tag value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("name", value.getName());
            if(value.getDescription() != null) gen.writeStringField("description", value.getDescription());
            gen.writeArrayFieldStart("applications");
            for(Application application : value.getApplications()) gen.writeNumber(application.getId());
            gen.writeEndArray();
            gen.writeEndObject();
        }
    }

    public static class TagDeserializer extends JsonDeserializer<Tag> {

        @Override
        public Tag deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            if(node.getNodeType().equals(JsonNodeType.STRING)) return new Tag(node.asText());
            String name = node.get("name").asText();
            JsonNode description = node.get("description");
            if(description == null) return new Tag(name);
            return new Tag(name, description.asText());
        }
    }
}

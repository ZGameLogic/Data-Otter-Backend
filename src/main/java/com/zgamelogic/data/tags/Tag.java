package com.zgamelogic.data.tags;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TAGS")
@JsonSerialize(using = Tag.TagSerializer.class)
public class Tag {
    @Id
    private String name;
    private String description;

    public Tag(String name){
        this.name = name;
    }

    public static class TagSerializer extends JsonSerializer<Tag> {
        @Override
        public void serialize(Tag value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeStringField("name", value.getName());
            if(value.getDescription() != null) gen.writeStringField("description", value.getDescription());
            gen.writeEndObject();
        }
    }
}

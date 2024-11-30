package com.zgamelogic.data.entities;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ROCKS")
@JsonSerialize(using = Rock.RockSerializer.class)
public class Rock {
    @EmbeddedId
    private RockId id;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String pebble;

    public Rock(long appId, String pebble) {
        id = new RockId(appId);
        this.pebble = pebble;
    }

    @Embeddable
    @Getter
    @NoArgsConstructor
    public static class RockId {
        private Date date;

        @ManyToOne(cascade = CascadeType.REMOVE)
        @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID")
        private Application application;

        public RockId(long appId){
            this.application = new Application(appId);
            date = new Date();
        }
    }

    public static class RockSerializer extends JsonSerializer<Rock> {
        @Override
        public void serialize(Rock value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("application id", value.getId().getApplication().getId());
            String date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss:SSSS").format(value.getId().getDate());
            gen.writeStringField("date", date);
            ObjectMapper om = new ObjectMapper();
            try {
                JsonNode jsonNode = om.readTree(value.getPebble());
                if (jsonNode.isObject()) {
                    gen.writeFieldName("pebble");
                    gen.writeTree(jsonNode);
                } else {
                    gen.writeStringField("pebble", value.getPebble());
                }
            } catch (Exception e) {
                gen.writeStringField("pebble", value.getPebble());
            }
            gen.writeEndObject();
        }
    }
}

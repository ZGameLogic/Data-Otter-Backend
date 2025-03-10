package com.zgamelogic.data.metric;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zgamelogic.data.application.Application;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "Metrics")
@NoArgsConstructor
@Getter
@ToString
@JsonDeserialize(using = Metric.MetricDeserializer.class)
@JsonSerialize(using = Metric.MetricSerializer.class)
public class Metric {
    @EmbeddedId
    private MetricId id;
    private String resource;
    private String data;
    private String unit;

    public Metric(long appId, String resource, String data, String unit) {
        id = new MetricId(appId);
        this.resource = resource;
        this.data = data;
        this.unit = unit;
    }

    @Embeddable
    @NoArgsConstructor
    @Getter
    @ToString
    public static class MetricId {
        private Date collected;

        @ManyToOne(cascade = CascadeType.REMOVE)
        @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID")
        private Application application;

        public MetricId(long appId){
            this.application = new Application(appId);
            collected = new Date();
        }
    }

    public static class MetricDeserializer extends JsonDeserializer<Metric> {

        @Override
        public Metric deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            JsonNode node = p.getCodec().readTree(p);
            long appId = node.get("application id").asLong();
            String resource = node.get("resource").asText();
            String data = node.get("data").asText();
            String unit = node.get("unit").asText();
            return new Metric(appId, resource, data, unit);
        }
    }

    public static class MetricSerializer extends JsonSerializer<Metric> {
        @Override
        public void serialize(Metric value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("application id", value.getId().getApplication().getId());
            gen.writeStringField("resource", value.getResource());
            gen.writeStringField("data", value.getData());
            gen.writeStringField("unit", value.getUnit());
            String date = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss:SSSS").format(value.getId().getCollected());
            gen.writeStringField("collected", date);
            gen.writeEndObject();
        }
    }
}

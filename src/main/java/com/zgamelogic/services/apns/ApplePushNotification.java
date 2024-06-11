package com.zgamelogic.services.apns;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.IOException;

@Data
@JsonSerialize(using = ApplePushNotification.ApplePushNotificationSerializer.class)
public class ApplePushNotification {

    private final String title;
    private final String subtitle;
    private final String body;

    protected static class ApplePushNotificationSerializer extends JsonSerializer<ApplePushNotification> {
        @Override
        public void serialize(ApplePushNotification notification, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                throws IOException {

            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectFieldStart("aps");
            jsonGenerator.writeStringField("sound", "bingbong.aiff");
            jsonGenerator.writeObjectFieldStart("alert");
            jsonGenerator.writeStringField("title", notification.getTitle());

            if (notification.getSubtitle() != null) {
                jsonGenerator.writeStringField("subtitle", notification.getSubtitle());
            }

            if (notification.getBody() != null) {
                jsonGenerator.writeStringField("body", notification.getBody());
            }

            jsonGenerator.writeEndObject(); // alert
            jsonGenerator.writeEndObject(); // aps
            jsonGenerator.writeEndObject();
        }
    }
}



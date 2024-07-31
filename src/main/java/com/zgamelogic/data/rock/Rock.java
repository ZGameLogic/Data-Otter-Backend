package com.zgamelogic.data.rock;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.zgamelogic.data.application.Application;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Rocks")
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private Date date;

        @ManyToOne(cascade = CascadeType.REMOVE)
        @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID")
        private Application application;

        public RockId(long appId){
            this.application = new Application(appId);
            date = new Date();
        }
    }
}

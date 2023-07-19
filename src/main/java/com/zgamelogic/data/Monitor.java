package com.zgamelogic.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Monitor {

    private int id;
    private String name;
    private boolean status;
    private String type;
    private long completedInMilliseconds;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Date taken;

}

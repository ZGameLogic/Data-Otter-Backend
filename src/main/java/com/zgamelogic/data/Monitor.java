package com.zgamelogic.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

}

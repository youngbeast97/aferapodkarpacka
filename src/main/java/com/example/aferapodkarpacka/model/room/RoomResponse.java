package com.example.aferapodkarpacka.model.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    private Long id;

    private String name;

    private boolean monitored;

    private boolean occupied;
}
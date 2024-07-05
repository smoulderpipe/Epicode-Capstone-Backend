package it.epicode.focufy.dtos;

import lombok.Data;

@Data
public class DayDTO {
    private int id;
    private String type;
    private String name;

    public DayDTO(){

    }

    public DayDTO(int id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }
}
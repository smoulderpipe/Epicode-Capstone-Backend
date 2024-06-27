package it.epicode.focufy.dtos;

import lombok.Data;

@Data
public class DayDTO {
    private int id;
    private String type;

    public DayDTO(){

    }

    public DayDTO(int id, String type) {
        this.id = id;
        this.type = type;
    }
}
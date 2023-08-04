package com.example.pj.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Schema implements Serializable {
    private String name;
    private List<Table> tables = new ArrayList<>();

    public String getViewName() {
        if (null == name)
            return "";
        return name;
    }
}

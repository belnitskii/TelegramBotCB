package com.belnitskii.telegrambotcb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Valuta {
    @JsonProperty("CharCode")
    private String charCode;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Value")
    private Double value;
}

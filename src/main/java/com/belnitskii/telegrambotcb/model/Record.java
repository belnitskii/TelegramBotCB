package com.belnitskii.telegrambotcb.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class Record {
    @JacksonXmlProperty(localName = "Date", isAttribute = true)
    private String date;

    @JacksonXmlProperty(localName = "Nominal")
    private int nominal;

    @JacksonXmlProperty(localName = "Value")
    private String rawValue;

    @JsonIgnore
    public double getValue() {
        return Double.parseDouble(rawValue.replace(",", "."));
    }
}

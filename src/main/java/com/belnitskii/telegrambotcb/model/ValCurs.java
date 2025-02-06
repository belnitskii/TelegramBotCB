package com.belnitskii.telegrambotcb.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
public class ValCurs {
    @JacksonXmlProperty(localName = "ID", isAttribute = true)
    private String id;

    @JacksonXmlProperty(localName = "DateRange1", isAttribute = true)
    private String dateRange1;

    @JacksonXmlProperty(localName = "DateRange2", isAttribute = true)
    private String dateRange2;

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;

    @JacksonXmlElementWrapper(useWrapping = false) // Чтобы избежать лишнего вложения
    @JacksonXmlProperty(localName = "Record")
    private List<Record> records;
}

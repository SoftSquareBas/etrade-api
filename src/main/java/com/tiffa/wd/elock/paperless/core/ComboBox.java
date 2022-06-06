package com.tiffa.wd.elock.paperless.core;

import java.io.Serializable;

import lombok.Data;

@Data
public class ComboBox implements Serializable {

    private static final long serialVersionUID = -6681254479746553386L;

    private String warehouseFrom;
    private String warehouseTo;

    private String query;
    private String from;
    private String to;
}

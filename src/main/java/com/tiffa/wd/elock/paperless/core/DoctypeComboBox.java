package com.tiffa.wd.elock.paperless.core;

import java.io.Serializable;

import lombok.Data;

@Data
public class DoctypeComboBox implements Serializable {

    private static final long serialVersionUID = -6681254479746553386L;

    private String receiveTypeFrom;
    private String receiveTypeTo;

    private String query;
    private String from;
    private String to;
}

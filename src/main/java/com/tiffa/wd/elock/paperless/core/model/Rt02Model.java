package com.tiffa.wd.elock.paperless.core.model;

import java.sql.Timestamp;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Rt02Model extends CommonRequest implements PageRequest {

    private String wareCode;

    private String locationiCode;

    private String locationName;

    private String active;

    // private Timestamp updDate;

    

    

}

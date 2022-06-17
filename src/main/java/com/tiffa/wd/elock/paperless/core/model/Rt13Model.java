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
public class Rt13Model extends CommonRequest implements PageRequest {

    private String mainDoc;

    private String mainDocDesc;

    private String typeFlag;
    
    private String inoutFlag;

    private Timestamp updDate;

}

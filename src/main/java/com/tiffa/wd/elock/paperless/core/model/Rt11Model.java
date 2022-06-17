package com.tiffa.wd.elock.paperless.core.model;



import java.sql.Timestamp;
import java.time.LocalDate;

// import java.sql.Timestamp;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Rt11Model extends CommonRequest implements PageRequest {

    private String ouCode;

    private LocalDate startDate;

    private LocalDate endDate;

    private String realStatus;

    private String statusFg;

    private String statusSp;

    private Integer yeAr;
    private Integer perIod;

     private Timestamp updDate;

    

    

}

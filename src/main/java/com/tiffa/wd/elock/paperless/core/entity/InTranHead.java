package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "in_tran_head")
@Table(name = "in_tran_head")
public class InTranHead implements Serializable {

    private static final long serialVersionUID = 4460155681154593524L;

    @Id
    private InTranHeadPk pk;

}

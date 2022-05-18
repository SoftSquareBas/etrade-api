package com.tiffa.wd.elock.paperless.master.devicePreparation;

import java.time.LocalDate;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class DevicePreparationModel extends CommonRequest implements PageRequest {
	
	private String tagCode;
	private LocalDate dateFrom;
	private LocalDate dateTo;
	private Integer maxRow;
}

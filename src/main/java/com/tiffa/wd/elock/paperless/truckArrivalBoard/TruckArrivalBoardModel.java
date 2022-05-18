package com.tiffa.wd.elock.paperless.truckArrivalBoard;

import java.time.LocalDate;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class TruckArrivalBoardModel extends CommonRequest implements PageRequest {

	private LocalDate dateFrom;
	private LocalDate dateTo;
	private String dest;
	
}

package com.tiffa.wd.elock.paperless.core.web;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/profile")
public class ProfileController {
	
	@PostMapping("/load")
	public Callable<Response> load() {
		return () -> {
			log.info("load");
			
			Map<String, Object> map = new HashMap<>();
			map.put("customerCode", "LM001");
			map.put("customerName", "นายยึดมั่น รักษาเกียรติ");
			map.put("position", "หัวหน้าทีมงานเคลื่อนย้ายที่ 1");
			map.put("contactInfo", "081-1234322");
			
			return Response.success(Data.of(map));
		};
	}

}

package com.wei.productivity.controller;

import com.wei.productivity.common.CommonResult;
import com.wei.productivity.common.ResultCode;
import com.wei.productivity.domain.TimeBlock;
import com.wei.productivity.dto.TimeBlockDto;
import com.wei.productivity.dto.TimeBlockParam;
import com.wei.productivity.exception.TimeBlockNotExistException;
import com.wei.productivity.service.TimeBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/productivity/u1/time_block")
public class TimeBlockController {
	@Autowired
	private TimeBlockService timeBlockService;

	@PostMapping(path = "")
	public CommonResult<TimeBlockDto> addTimeBlock(@RequestBody TimeBlockParam timeBlockParam) {
		return CommonResult.success(TimeBlockDto.parseDomain(timeBlockService.add(timeBlockParam)));
	}

	@PostMapping(path = "/u1/{blockID}")
	public CommonResult<TimeBlockDto> updateBlock(@PathVariable String blockID,
			@RequestBody TimeBlockParam timeBlockParam) {
		try {
			timeBlockService.update(blockID, timeBlockParam);
		} catch (TimeBlockNotExistException e) {
			return CommonResult.failed(e.getMessage());
		}
		return CommonResult.success();
	}

	@GetMapping(path = "")
	public CommonResult<List<TimeBlockDto>> getByDate(
			@RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
			@RequestParam(name = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
			@RequestParam(name = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
		if (date == null && (start == null || end == null)) {
			return CommonResult.failed(ResultCode.VALIDATE_FAILED, "should have start and end without date");
		}
		List<TimeBlockDto> resList = new ArrayList<>();
		List<TimeBlock> blockList = new ArrayList<>();
		if (date != null) {
			blockList = timeBlockService.getByDate(date);
		} else {
			blockList = timeBlockService.getByDateRange(start, end);
		}
		for (TimeBlock t : blockList) {
			resList.add(TimeBlockDto.parseDomain(t));
		}
		return CommonResult.success(resList);
	}
}

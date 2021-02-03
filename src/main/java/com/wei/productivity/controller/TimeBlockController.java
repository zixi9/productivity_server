package com.wei.productivity.controller;

import com.wei.productivity.common.CommonResult;
import com.wei.productivity.common.ResultCode;
import com.wei.productivity.domain.TimeBlock;
import com.wei.productivity.dto.TimeBlockDto;
import com.wei.productivity.dto.TimeBlockParam;
import com.wei.productivity.exception.TimeBlockNotExistException;
import com.wei.productivity.service.TimeBlockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Time block controller.
 */
@RestController
@RequestMapping("/api/productivity/u1/time_block")
public class TimeBlockController {
    /**
     * The Logger.
     */
    Logger logger = LoggerFactory.getLogger(TimeBlockController.class);
    @Autowired
    private TimeBlockService timeBlockService;

    /**
     * Add time block common result.
     *
     * @param timeBlockParam
     *            the time block param
     * @return the common result
     */
    @PostMapping(path = "")
    public CommonResult<TimeBlockDto> addTimeBlock(@RequestBody @Validated TimeBlockParam timeBlockParam) {
        logger.debug(timeBlockParam.toString());
        return CommonResult.success(TimeBlockDto.parseDomain(timeBlockService.add(timeBlockParam)));
    }

    /**
     * Update block common result.
     *
     * @param blockID
     *            the block id
     * @param timeBlockParam
     *            the time block param
     * @return the common result
     */
    @PostMapping(path = "/u1/{blockID}")
    public CommonResult<TimeBlockDto> updateBlock(@PathVariable @Validated @NotNull String blockID,
        @RequestBody @Validated TimeBlockParam timeBlockParam) {
        try {
            timeBlockService.update(blockID, timeBlockParam);
        } catch (TimeBlockNotExistException e) {
            return CommonResult.failed(e.getMessage());
        }
        return CommonResult.success();
    }

    /**
     * Gets by date.
     *
     * @param date
     *            the date
     * @param start
     *            the start
     * @param end
     *            the end
     * @return the by date
     */
    @GetMapping(path = "")
    public CommonResult<List<TimeBlockDto>> getByDate(
        @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        if (date == null && (start == null || end == null)) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "should have start and end without date");
        }
        List<TimeBlockDto> resList = new ArrayList<>();
        List<TimeBlock> blockList = new ArrayList<>();
        if (date != null) {
            blockList = timeBlockService.getByDate(date.atStartOfDay());
        } else {
            blockList = timeBlockService.getByDateRange(start.atStartOfDay(), end.atStartOfDay());
        }
        for (TimeBlock t : blockList) {
            logger.debug("get time block: " + t.toString());
            resList.add(TimeBlockDto.parseDomain(t));
        }
        return CommonResult.success(resList);
    }
}

package com.wei.productivity.dto;

import com.wei.productivity.domain.TimeBlock;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TimeBlockDto {
	@JsonProperty(value = "block_id")
	private String blockId;

	private String category;

	private String target;

	private String description;

	private String comment;

	@JsonProperty(value = "begin_time")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date beginTime;

	private Integer planInterval;

	@JsonProperty(value = "end_time")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date endTime;

	public static TimeBlockDto parseDomain(TimeBlock timeBlock) {
		TimeBlockDto timeBlockDto = new TimeBlockDto();

		timeBlockDto.setBlockId(timeBlock.getId());
		timeBlockDto.setCategory(timeBlock.getCategory());
		timeBlockDto.setTarget(timeBlock.getTarget());
		timeBlockDto.setDescription(timeBlock.getDescription());
		timeBlockDto.setComment(timeBlock.getComment());
		timeBlockDto.setBeginTime(timeBlock.getBeginTime());
		timeBlockDto.setPlanInterval(timeBlock.getPlanInterval());
		timeBlockDto.setEndTime(timeBlock.getEndTime());
		return timeBlockDto;
	}
}

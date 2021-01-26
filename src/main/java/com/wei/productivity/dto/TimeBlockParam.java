package com.wei.productivity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class TimeBlockParam {
	@NotNull
	@NotEmpty
	private String category;
	private String description;
	@NotNull
	@NotEmpty
	private String target;
	@NotNull
	private Date beginTime;
	private Date endTime;

	@NotNull
	@Min(0)
	private int planInterval;

	private String comment;
}

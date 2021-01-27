package com.wei.productivity.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "prod_time_block")
public class TimeBlock {

	@Id
	@Column(name = "block_id")
	private String Id;

	@Column(nullable = false)
	private String category;

	@Column(nullable = false)
	private String target;

	private String description;

	private String comment;

	@Column(name = "begin_time", nullable = false)
	private LocalDateTime beginTime;

	@Column(name = "plan_interval")
	private Integer planInterval;

	@Column(name = "end_time")
	private LocalDateTime endTime;
}

package com.wei.productivity.service;

import com.wei.productivity.domain.TimeBlock;
import com.wei.productivity.dto.TimeBlockParam;
import com.wei.productivity.exception.TimeBlockNotExistException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TimeBlockService {
	public String generate_block_id();

	public Optional<TimeBlock> get(String block_id);

	public List<TimeBlock> getByDate(LocalDateTime date);

	public List<TimeBlock> getByDateRange(LocalDateTime startDate, LocalDateTime endData);

	public TimeBlock add(TimeBlockParam timeBlockParam);

	public TimeBlock update(String blockID, TimeBlockParam timeBlockParam) throws TimeBlockNotExistException;
}

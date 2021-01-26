package com.wei.productivity.service;

import com.wei.productivity.domain.TimeBlock;
import com.wei.productivity.dto.TimeBlockParam;
import com.wei.productivity.exception.TimeBlockNotExistException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TimeBlockService {
	public String generate_block_id();

	public Optional<TimeBlock> get(String block_id);

	public List<TimeBlock> getByDate(Date date);

	public List<TimeBlock> getByDateRange(Date startDate, Date endData);

	public TimeBlock add(TimeBlockParam timeBlockParam);

	public TimeBlock update(String blockID, TimeBlockParam timeBlockParam) throws TimeBlockNotExistException;
}

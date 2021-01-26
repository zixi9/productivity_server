package com.wei.productivity.service.impl;

import com.wei.productivity.dao.TimeBlockRepository;
import com.wei.productivity.domain.TimeBlock;
import com.wei.productivity.dto.TimeBlockParam;
import com.wei.productivity.exception.TimeBlockNotExistException;
import com.wei.productivity.service.TimeBlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TimeBlockServiceImpl implements TimeBlockService {

	@Autowired
	TimeBlockRepository timeBlockRepository;

	@Override
	public String generate_block_id() {
		return UUID.randomUUID().toString();
	}

	@Override
	public Optional<TimeBlock> get(String block_id) {
		return timeBlockRepository.findById(block_id);
	}

	@Override
	public List<TimeBlock> getByDate(Date date) {
		Date endDate = new Date(date.getTime() + 24 * 3600 * 1000);
		return timeBlockRepository.findByBeginTime(date, endDate);
	}

	@Override
	public List<TimeBlock> getByDateRange(Date startDate, Date endDate) {
		return timeBlockRepository.findByBeginTime(startDate, endDate);
	}

	@Override
	public TimeBlock add(TimeBlockParam timeBlockParam) {
		var block = new TimeBlock();
		block.setId(generate_block_id());
		block.setCategory(timeBlockParam.getCategory());
		block.setTarget(timeBlockParam.getTarget());
		block.setDescription(timeBlockParam.getDescription());
		block.setBeginTime(timeBlockParam.getBeginTime());
		block.setPlanInterval(timeBlockParam.getPlanInterval());
		return timeBlockRepository.save(block);
	}

	@Override
	public TimeBlock update(String blockID, TimeBlockParam timeBlockParam) throws TimeBlockNotExistException {
		Optional<TimeBlock> block = timeBlockRepository.findById(blockID);
		if (block.isPresent()) {
			TimeBlock blockObj = block.get();
			blockObj.setCategory(timeBlockParam.getCategory());
			blockObj.setTarget(timeBlockParam.getTarget());
			blockObj.setDescription(timeBlockParam.getDescription());
			blockObj.setBeginTime(timeBlockParam.getBeginTime());
			blockObj.setEndTime(timeBlockParam.getEndTime());
			blockObj.setPlanInterval(timeBlockParam.getPlanInterval());
			blockObj.setComment(timeBlockParam.getComment());
			return timeBlockRepository.save(blockObj);
		} else {
			throw new TimeBlockNotExistException(blockID);
		}
	}
}

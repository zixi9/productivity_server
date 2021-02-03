package com.wei.productivity.service.impl;

import com.wei.productivity.dao.TimeBlockRepository;
import com.wei.productivity.domain.TimeBlock;
import com.wei.productivity.dto.TimeBlockParam;
import com.wei.productivity.exception.TimeBlockNotExistException;
import com.wei.productivity.service.TimeBlockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TimeBlockServiceImpl implements TimeBlockService {

    Logger logger = LoggerFactory.getLogger(TimeBlockServiceImpl.class);

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
    public List<TimeBlock> getByDate(LocalDateTime date) {
        LocalDateTime endDate = date.plusDays(1);
        return timeBlockRepository.findByBeginTime(date, endDate);
    }

    @Override
    public List<TimeBlock> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("timeBlockParam begin time: " + startDate.toString());
        logger.debug("timeBlock begin time: " + endDate.toString());
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
        logger.debug("timeBlockParam begin time: " + timeBlockParam.getBeginTime().toString());
        logger.debug("timeBlock begin time: " + block.getBeginTime().toString());
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

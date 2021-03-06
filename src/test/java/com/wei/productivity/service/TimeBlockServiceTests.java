package com.wei.productivity.service;

import com.wei.productivity.dao.TimeBlockRepository;
import com.wei.productivity.domain.TimeBlock;
import com.wei.productivity.dto.TimeBlockParam;
import com.wei.productivity.exception.TimeBlockNotExistException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class TimeBlockServiceTests {

    private List<String> generatedIdList;

    @Autowired
    private TimeBlockRepository timeBlockRepository;

    @Autowired
    private TimeBlockService timeBlockService;

    @BeforeEach
    public void setUp() {
        generatedIdList = new ArrayList<>();
    }

    @AfterEach
    public void tearDown() {
        for (var blockId : generatedIdList) {
            timeBlockRepository.deleteById(blockId);
        }
    }

    @Test
    public void addTimeBlock() throws ParseException {
        var timeBlockParam = new TimeBlockParam();
        timeBlockParam.setCategory("test category");
        timeBlockParam.setTarget("test target");
        timeBlockParam.setDescription("test description");
        timeBlockParam.setBeginTime(LocalDateTime.parse("2021-01-22T01:01:01"));
        timeBlockParam.setPlanInterval(25);
        var timeBlock = timeBlockService.add(timeBlockParam);

        assertThat(timeBlock).isNotNull();
        assertThat(timeBlock.getId()).isNotNull();
        generatedIdList.add(timeBlock.getId());

        var timeBlockOptional = timeBlockService.get(timeBlock.getId());
        assertThat(timeBlockOptional.isPresent()).isTrue();
        var fetchedTimeBlock = timeBlockOptional.get();
        assertThat(fetchedTimeBlock.getId()).isEqualTo(timeBlock.getId());
        assertThat(fetchedTimeBlock.getCategory()).isEqualTo(timeBlock.getCategory());
        assertThat(fetchedTimeBlock.getTarget()).isEqualTo(timeBlock.getTarget());
        assertThat(fetchedTimeBlock.getDescription()).isEqualTo(timeBlock.getDescription());
        assertThat(fetchedTimeBlock.getBeginTime()).isEqualTo(timeBlock.getBeginTime());
        assertThat(fetchedTimeBlock.getPlanInterval()).isEqualTo(timeBlock.getPlanInterval());
        assertThat(fetchedTimeBlock.getEndTime()).isNull();
        assertThat(fetchedTimeBlock.getComment()).isNull();
    }

    @Test
    public void updateNotExistBlockId() throws ParseException {
        var timeBlockParam = new TimeBlockParam();
        timeBlockParam.setCategory("test category");
        timeBlockParam.setTarget("test target");
        timeBlockParam.setDescription("test description");
        timeBlockParam.setBeginTime(LocalDateTime.parse("2021-01-22T01:01:01"));
        timeBlockParam.setPlanInterval(25);
        assertThatThrownBy(() -> {
            timeBlockService.update("Not Exist Block ID", timeBlockParam);
        }, "Update Not Exist TimeBlock").isInstanceOf(TimeBlockNotExistException.class);
    }

    @Test
    public void updateExistBlockId() throws ParseException {
        var timeBlockParam = new TimeBlockParam();
        timeBlockParam.setCategory("test category");
        timeBlockParam.setTarget("test target");
        timeBlockParam.setDescription("test description");
        timeBlockParam.setBeginTime(LocalDateTime.parse("2021-01-22T01:01:01"));
        timeBlockParam.setPlanInterval(25);
        var timeBlock = timeBlockService.add(timeBlockParam);
        generatedIdList.add(timeBlock.getId());

        var updateTimeBlockParam = new TimeBlockParam();
        updateTimeBlockParam.setCategory("updated category");
        updateTimeBlockParam.setDescription("updated description");
        updateTimeBlockParam.setTarget("updated target");
        updateTimeBlockParam.setBeginTime(LocalDateTime.parse("2021-01-22T00:00:00"));
        updateTimeBlockParam.setEndTime(LocalDateTime.parse("2021-01-23T00:00:00"));
        updateTimeBlockParam.setPlanInterval(30);

        assertThatCode(() -> timeBlockService.update(timeBlock.getId(), updateTimeBlockParam))
                .doesNotThrowAnyException();

        var optionalTimeBlock = timeBlockService.get(timeBlock.getId());
        assertThat(optionalTimeBlock.isPresent()).isTrue();
        var updatedTimeBlock = optionalTimeBlock.get();

        assertThat(updatedTimeBlock.getId()).isEqualTo(timeBlock.getId());
        assertThat(updatedTimeBlock.getCategory()).isEqualTo(updateTimeBlockParam.getCategory());
        assertThat(updatedTimeBlock.getTarget()).isEqualTo(updateTimeBlockParam.getTarget());
        assertThat(updatedTimeBlock.getDescription()).isEqualTo(updateTimeBlockParam.getDescription());
        assertThat(updatedTimeBlock.getBeginTime()).isEqualTo(updateTimeBlockParam.getBeginTime());
        assertThat(updatedTimeBlock.getPlanInterval()).isEqualTo(updateTimeBlockParam.getPlanInterval());
        assertThat(updatedTimeBlock.getEndTime()).isEqualTo(updateTimeBlockParam.getEndTime());
        assertThat(updatedTimeBlock.getComment()).isNull();
    }

    @Test
    public void getByDate() throws ParseException {
        var timeBlock1 = new TimeBlock();
        timeBlock1.setId(timeBlockService.generate_block_id());
        timeBlock1.setCategory("test category 1");
        timeBlock1.setTarget("test target 1");
        timeBlock1.setDescription("test description 1");
        timeBlock1.setBeginTime(LocalDateTime.parse("2021-01-21T10:01:01", DateTimeFormatter.ISO_DATE_TIME));
        timeBlock1.setPlanInterval(25);
        timeBlock1 = timeBlockRepository.save(timeBlock1);
        generatedIdList.add(timeBlock1.getId());

        var timeBlock2 = new TimeBlock();
        timeBlock2.setId(timeBlockService.generate_block_id());
        timeBlock2.setCategory("test category 2");
        timeBlock2.setTarget("test target 2");
        timeBlock2.setDescription("test description 2");
        timeBlock2.setBeginTime(LocalDateTime.parse("2021-01-22T11:11:11", DateTimeFormatter.ISO_DATE_TIME));
        timeBlock2.setEndTime(LocalDateTime.parse("2021-01-22T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        timeBlock2.setPlanInterval(30);
        timeBlock2 = timeBlockRepository.save(timeBlock2);
        // add无法添加结束时间等
        generatedIdList.add(timeBlock2.getId());

        var timeBlock3 = new TimeBlock();
        timeBlock3.setId(timeBlockService.generate_block_id());
        timeBlock3.setCategory("test category 3");
        timeBlock3.setTarget("test target 3");
        timeBlock3.setDescription("test description 2");
        timeBlock3.setBeginTime(LocalDateTime.parse("2021-01-21T03:00:01", DateTimeFormatter.ISO_DATE_TIME));
        timeBlock3.setEndTime(LocalDateTime.parse("2021-01-21T10:00:10", DateTimeFormatter.ISO_DATE_TIME));
        timeBlock3.setPlanInterval(20);
        timeBlock3.setComment("test common 3");
        timeBlock3 = timeBlockRepository.save(timeBlock3);
        // add无法添加结束时间等
        generatedIdList.add(timeBlock3.getId());

        var timeBlockList = timeBlockService.getByDate(LocalDateTime.parse("2021-01-22T00:00:00"));
        assertThat(timeBlockList.size()).isEqualTo(1);
        assertThat(timeBlockList.get(0).getId()).isEqualTo(timeBlock2.getId());
        assertThat(timeBlockList.get(0).getCategory()).isEqualTo(timeBlock2.getCategory());
        assertThat(timeBlockList.get(0).getTarget()).isEqualTo(timeBlock2.getTarget());
        assertThat(timeBlockList.get(0).getDescription()).isEqualTo(timeBlock2.getDescription());
        assertThat(timeBlockList.get(0).getBeginTime()).isEqualTo(timeBlock2.getBeginTime());
        assertThat(timeBlockList.get(0).getPlanInterval()).isEqualTo(timeBlock2.getPlanInterval());
        assertThat(timeBlockList.get(0).getEndTime()).isEqualTo(timeBlock2.getEndTime());
        assertThat(timeBlockList.get(0).getComment()).isNull();

        timeBlockList = timeBlockService.getByDate(LocalDateTime.parse("2021-01-21T00:00:00"));
        assertThat(timeBlockList.size()).isEqualTo(2);
        assertThat(timeBlockList.get(0).getId()).isEqualTo(timeBlock3.getId());
        assertThat(timeBlockList.get(0).getCategory()).isEqualTo(timeBlock3.getCategory());
        assertThat(timeBlockList.get(0).getTarget()).isEqualTo(timeBlock3.getTarget());
        assertThat(timeBlockList.get(0).getDescription()).isEqualTo(timeBlock3.getDescription());
        assertThat(timeBlockList.get(0).getBeginTime()).isEqualTo(timeBlock3.getBeginTime());
        assertThat(timeBlockList.get(0).getPlanInterval()).isEqualTo(timeBlock3.getPlanInterval());
        assertThat(timeBlockList.get(0).getEndTime()).isEqualTo(timeBlock3.getEndTime());
        assertThat(timeBlockList.get(0).getComment()).isEqualTo(timeBlock3.getComment());

        assertThat(timeBlockList.get(1).getId()).isEqualTo(timeBlock1.getId());
        assertThat(timeBlockList.get(1).getCategory()).isEqualTo(timeBlock1.getCategory());
        assertThat(timeBlockList.get(1).getTarget()).isEqualTo(timeBlock1.getTarget());
        assertThat(timeBlockList.get(1).getDescription()).isEqualTo(timeBlock1.getDescription());
        assertThat(timeBlockList.get(1).getBeginTime()).isEqualTo(timeBlock1.getBeginTime());
        assertThat(timeBlockList.get(1).getPlanInterval()).isEqualTo(timeBlock1.getPlanInterval());
        assertThat(timeBlockList.get(1).getEndTime()).isNull();
        assertThat(timeBlockList.get(1).getComment()).isNull();

        // Not Exist
        timeBlockList = timeBlockService.getByDate(LocalDateTime.parse("2021-01-20T00:00:00"));
        assertThat(timeBlockList.size()).isEqualTo(0);
    }

    @Test
    public void getByDateRange() throws ParseException {
        var timeBlock1 = new TimeBlock();
        timeBlock1.setId(timeBlockService.generate_block_id());
        timeBlock1.setCategory("test category 1");
        timeBlock1.setTarget("test target 1");
        timeBlock1.setDescription("test description 1");
        timeBlock1.setBeginTime(LocalDateTime.parse("2021-01-21T10:01:01", DateTimeFormatter.ISO_DATE_TIME));
        timeBlock1.setPlanInterval(25);
        timeBlock1 = timeBlockRepository.save(timeBlock1);
        generatedIdList.add(timeBlock1.getId());

        var timeBlock2 = new TimeBlock();
        timeBlock2.setId(timeBlockService.generate_block_id());
        timeBlock2.setCategory("test category 2");
        timeBlock2.setTarget("test target 2");
        timeBlock2.setDescription("test description 2");
        timeBlock2.setBeginTime(LocalDateTime.parse("2021-01-22T11:11:11", DateTimeFormatter.ISO_DATE_TIME));
        timeBlock2.setEndTime(LocalDateTime.parse("2021-01-22T12:00:00", DateTimeFormatter.ISO_DATE_TIME));
        timeBlock2.setPlanInterval(30);
        timeBlock2 = timeBlockRepository.save(timeBlock2);
        // add无法添加结束时间等
        generatedIdList.add(timeBlock2.getId());

        var timeBlock3 = new TimeBlock();
        timeBlock3.setId(timeBlockService.generate_block_id());
        timeBlock3.setCategory("test category 3");
        timeBlock3.setTarget("test target 3");
        timeBlock3.setDescription("test description 2");
        timeBlock3.setBeginTime(LocalDateTime.parse("2021-01-21T03:00:01", DateTimeFormatter.ISO_DATE_TIME));
        timeBlock3.setEndTime(LocalDateTime.parse("2021-01-21T10:00:10", DateTimeFormatter.ISO_DATE_TIME));
        timeBlock3.setPlanInterval(20);
        timeBlock3.setComment("test common 3");
        timeBlock3 = timeBlockRepository.save(timeBlock3);
        // add无法添加结束时间等
        generatedIdList.add(timeBlock3.getId());


        var timeBlockList = timeBlockService.getByDateRange(
                LocalDateTime.parse("2021-01-22T00:00:00"),
                LocalDateTime.parse("2021-01-23T00:00:00"));
        assertThat(timeBlockList.size()).isEqualTo(1);
        assertThat(timeBlockList.get(0).getId()).isEqualTo(timeBlock2.getId());
        assertThat(timeBlockList.get(0).getCategory()).isEqualTo(timeBlock2.getCategory());
        assertThat(timeBlockList.get(0).getTarget()).isEqualTo(timeBlock2.getTarget());
        assertThat(timeBlockList.get(0).getDescription()).isEqualTo(timeBlock2.getDescription());
        assertThat(timeBlockList.get(0).getBeginTime()).isEqualTo(timeBlock2.getBeginTime());
        assertThat(timeBlockList.get(0).getPlanInterval()).isEqualTo(timeBlock2.getPlanInterval());
        assertThat(timeBlockList.get(0).getEndTime()).isEqualTo(timeBlock2.getEndTime());
        assertThat(timeBlockList.get(0).getComment()).isNull();

        timeBlockList = timeBlockService.getByDateRange(
                LocalDateTime.parse("2021-01-21T00:00:00"),
                LocalDateTime.parse("2021-01-22T00:00:00"));
        assertThat(timeBlockList.size()).isEqualTo(2);
        assertThat(timeBlockList.get(0).getId()).isEqualTo(timeBlock3.getId());
        assertThat(timeBlockList.get(0).getCategory()).isEqualTo(timeBlock3.getCategory());
        assertThat(timeBlockList.get(0).getTarget()).isEqualTo(timeBlock3.getTarget());
        assertThat(timeBlockList.get(0).getDescription()).isEqualTo(timeBlock3.getDescription());
        assertThat(timeBlockList.get(0).getBeginTime()).isEqualTo(timeBlock3.getBeginTime());
        assertThat(timeBlockList.get(0).getPlanInterval()).isEqualTo(timeBlock3.getPlanInterval());
        assertThat(timeBlockList.get(0).getEndTime()).isEqualTo(timeBlock3.getEndTime());
        assertThat(timeBlockList.get(0).getComment()).isEqualTo(timeBlock3.getComment());

        assertThat(timeBlockList.get(1).getId()).isEqualTo(timeBlock1.getId());
        assertThat(timeBlockList.get(1).getCategory()).isEqualTo(timeBlock1.getCategory());
        assertThat(timeBlockList.get(1).getTarget()).isEqualTo(timeBlock1.getTarget());
        assertThat(timeBlockList.get(1).getDescription()).isEqualTo(timeBlock1.getDescription());
        assertThat(timeBlockList.get(1).getBeginTime()).isEqualTo(timeBlock1.getBeginTime());
        assertThat(timeBlockList.get(1).getPlanInterval()).isEqualTo(timeBlock1.getPlanInterval());
        assertThat(timeBlockList.get(1).getEndTime()).isNull();
        assertThat(timeBlockList.get(1).getComment()).isNull();

        timeBlockList = timeBlockService.getByDateRange(
                LocalDateTime.parse("2021-01-21T00:00:00"),
                LocalDateTime.parse("2021-01-23T00:00:00"));
        assertThat(timeBlockList.size()).isEqualTo(3);
        assertThat(timeBlockList.get(0).getId()).isEqualTo(timeBlock3.getId());
        assertThat(timeBlockList.get(1).getId()).isEqualTo(timeBlock1.getId());
        assertThat(timeBlockList.get(2).getId()).isEqualTo(timeBlock2.getId());

        // Not Exist
        timeBlockList = timeBlockService.getByDateRange(
                LocalDateTime.parse("2021-01-20T00:00:00"),
                LocalDateTime.parse("2021-01-21T00:00:00"));
        assertThat(timeBlockList.size()).isEqualTo(0);
    }

}

package com.wei.productivity.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wei.productivity.common.CommonResult;
import com.wei.productivity.common.ResultCode;
import com.wei.productivity.dao.TimeBlockRepository;
import com.wei.productivity.domain.TimeBlock;
import com.wei.productivity.dto.TimeBlockDto;
import com.wei.productivity.dto.TimeBlockParam;
import com.wei.productivity.service.TimeBlockService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class TimeBlockControllerTests {

	private List<String> generatedIdList;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TimeBlockRepository timeBlockRepository;

	@Autowired
	private TimeBlockService timeBlockService;

	@BeforeEach
	void setUp() {
		generatedIdList = new ArrayList<>();
	}

	@AfterEach
	public void tearDown() {
		for (var blockId : generatedIdList) {
			timeBlockRepository.deleteById(blockId);
		}
	}

	@Test
	public void addTimeBlock() throws Exception {
		var timeBlockParam = new TimeBlockParam();
		timeBlockParam.setCategory("test category");
		timeBlockParam.setTarget("test target");
		timeBlockParam.setDescription("test description");
		timeBlockParam.setBeginTime(new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-22"));
		timeBlockParam.setPlanInterval(25);

		ObjectMapper mapper = new ObjectMapper();

		String resultStr = this.mockMvc
				.perform(post("/api/productivity/u1/time_block").contentType(MediaType.APPLICATION_JSON)
						.content(mapper.writeValueAsString(timeBlockParam)).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		CommonResult<TimeBlockDto> commonResult = mapper.readValue(resultStr,
				new TypeReference<CommonResult<TimeBlockDto>>() {
				});
		assertThat(commonResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
		assertThat(commonResult.getData()).isNotNull();
		generatedIdList.add(commonResult.getData().getBlockId());
		assertThat(commonResult.getData().getCategory()).isEqualTo(timeBlockParam.getCategory());
		assertThat(commonResult.getData().getTarget()).isEqualTo(timeBlockParam.getTarget());
		assertThat(commonResult.getData().getDescription()).isEqualTo(timeBlockParam.getDescription());
		assertThat(commonResult.getData().getBeginTime()).isEqualTo(timeBlockParam.getBeginTime());
		assertThat(commonResult.getData().getPlanInterval()).isEqualTo(timeBlockParam.getPlanInterval());
		assertThat(commonResult.getData().getComment()).isNull();
		assertThat(commonResult.getData().getEndTime()).isNull();
	}

	@Test
	public void updateNotExistBlockId() throws Exception {
		var timeBlockParam = new TimeBlockParam();
		timeBlockParam.setCategory("test category");
		timeBlockParam.setTarget("test target");
		timeBlockParam.setDescription("test description");
		timeBlockParam.setBeginTime(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-22"));
		timeBlockParam.setPlanInterval(25);

		ObjectMapper mapper = new ObjectMapper();

		String resultStr = this.mockMvc
				.perform(post(String.format("/api/productivity/u1/time_block/u1/%s", "NotExistTimeBlock"))
						.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(timeBlockParam))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		CommonResult<TimeBlockDto> commonResult = mapper.readValue(resultStr,
				new TypeReference<CommonResult<TimeBlockDto>>() {
				});
		assertThat(commonResult.getCode()).isEqualTo(ResultCode.FAILED.getCode());
		assertThat(commonResult.getData()).isNull();
	}

	@Test
	public void updateExistBlockId() throws Exception {
		var timeBlockParam = new TimeBlockParam();
		timeBlockParam.setCategory("test category");
		timeBlockParam.setTarget("test target");
		timeBlockParam.setDescription("test description");
		timeBlockParam.setBeginTime(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-22"));
		timeBlockParam.setPlanInterval(25);
		var timeBlock = timeBlockService.add(timeBlockParam);
		generatedIdList.add(timeBlock.getId());

		var updateTimeBlockParam = new TimeBlockParam();
		updateTimeBlockParam.setCategory("updated category");
		updateTimeBlockParam.setDescription("updated description");
		updateTimeBlockParam.setTarget("updated target");
		updateTimeBlockParam.setBeginTime(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-22"));
		updateTimeBlockParam.setEndTime(new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-23"));
		updateTimeBlockParam.setPlanInterval(30);

		ObjectMapper mapper = new ObjectMapper();

		String resultStr = this.mockMvc
				.perform(post(String.format("/api/productivity/u1/time_block/u1/%s", timeBlock.getId()))
						.contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(timeBlockParam))
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		CommonResult<TimeBlockDto> commonResult = mapper.readValue(resultStr,
				new TypeReference<CommonResult<TimeBlockDto>>() {
				});

		assertThat(commonResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
		assertThat(commonResult.getData()).isNull();
	}

	@Test
	public void getByDate() throws Exception {
		var timeBlock1 = new TimeBlock();
		timeBlock1.setId(timeBlockService.generate_block_id());
		timeBlock1.setCategory("test category 1");
		timeBlock1.setTarget("test target 1");
		timeBlock1.setDescription("test description 1");
		timeBlock1.setBeginTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2021-01-21T10:01:01"));
		timeBlock1.setPlanInterval(25);
		timeBlock1 = timeBlockRepository.save(timeBlock1);
		generatedIdList.add(timeBlock1.getId());

		var timeBlock2 = new TimeBlock();
		timeBlock2.setId(timeBlockService.generate_block_id());
		timeBlock2.setCategory("test category 2");
		timeBlock2.setTarget("test target 2");
		timeBlock2.setDescription("test description 2");
		timeBlock2.setBeginTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2021-01-22T11:11:11"));
		timeBlock2.setEndTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2021-01-22T12:00:00"));
		timeBlock2.setPlanInterval(30);
		timeBlock2 = timeBlockRepository.save(timeBlock2);
		// add无法添加结束时间等
		generatedIdList.add(timeBlock2.getId());

		var timeBlock3 = new TimeBlock();
		timeBlock3.setId(timeBlockService.generate_block_id());
		timeBlock3.setCategory("test category 3");
		timeBlock3.setTarget("test target 3");
		timeBlock3.setDescription("test description 2");
		timeBlock3.setBeginTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2021-01-21T03:00:01"));
		timeBlock3.setEndTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2021-01-21T10:00:10"));
		timeBlock3.setPlanInterval(20);
		timeBlock3.setComment("test common 3");
		timeBlock3 = timeBlockRepository.save(timeBlock3);
		// add无法添加结束时间等
		generatedIdList.add(timeBlock3.getId());

		ObjectMapper mapper = new ObjectMapper();

		String resultStr = this.mockMvc
				.perform(get("/api/productivity/u1/time_block").param("date", "2021-01-22")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		CommonResult<List<TimeBlockDto>> commonResult = mapper.readValue(resultStr,
				new TypeReference<CommonResult<List<TimeBlockDto>>>() {
				});

		assertThat(commonResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
		assertThat(commonResult.getData()).isNotNull();

		var timeBlockList = commonResult.getData();
		assertThat(timeBlockList.size()).isEqualTo(1);
		assertThat(timeBlockList.get(0).getBlockId()).isEqualTo(timeBlock2.getId());
		assertThat(timeBlockList.get(0).getCategory()).isEqualTo(timeBlock2.getCategory());
		assertThat(timeBlockList.get(0).getTarget()).isEqualTo(timeBlock2.getTarget());
		assertThat(timeBlockList.get(0).getDescription()).isEqualTo(timeBlock2.getDescription());
		assertThat(timeBlockList.get(0).getBeginTime().getTime()).isEqualTo(timeBlock2.getBeginTime().getTime());
		assertThat(timeBlockList.get(0).getPlanInterval()).isEqualTo(timeBlock2.getPlanInterval());
		assertThat(timeBlockList.get(0).getEndTime().getTime()).isEqualTo(timeBlock2.getEndTime().getTime());
		assertThat(timeBlockList.get(0).getComment()).isNull();

		resultStr = this.mockMvc
				.perform(get("/api/productivity/u1/time_block").param("date", "2021-01-21")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		commonResult = mapper.readValue(resultStr, new TypeReference<CommonResult<List<TimeBlockDto>>>() {
		});

		assertThat(commonResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
		assertThat(commonResult.getData()).isNotNull();

		timeBlockList = commonResult.getData();
		assertThat(timeBlockList.size()).isEqualTo(2);
		assertThat(timeBlockList.get(0).getBlockId()).isEqualTo(timeBlock3.getId());
		assertThat(timeBlockList.get(0).getCategory()).isEqualTo(timeBlock3.getCategory());
		assertThat(timeBlockList.get(0).getTarget()).isEqualTo(timeBlock3.getTarget());
		assertThat(timeBlockList.get(0).getDescription()).isEqualTo(timeBlock3.getDescription());
		assertThat(timeBlockList.get(0).getBeginTime().getTime()).isEqualTo(timeBlock3.getBeginTime().getTime());
		assertThat(timeBlockList.get(0).getPlanInterval()).isEqualTo(timeBlock3.getPlanInterval());
		assertThat(timeBlockList.get(0).getEndTime().getTime()).isEqualTo(timeBlock3.getEndTime().getTime());
		assertThat(timeBlockList.get(0).getComment()).isEqualTo(timeBlock3.getComment());

		assertThat(timeBlockList.get(1).getBlockId()).isEqualTo(timeBlock1.getId());
		assertThat(timeBlockList.get(1).getCategory()).isEqualTo(timeBlock1.getCategory());
		assertThat(timeBlockList.get(1).getTarget()).isEqualTo(timeBlock1.getTarget());
		assertThat(timeBlockList.get(1).getDescription()).isEqualTo(timeBlock1.getDescription());
		assertThat(timeBlockList.get(1).getBeginTime().getTime()).isEqualTo(timeBlock1.getBeginTime().getTime());
		assertThat(timeBlockList.get(1).getPlanInterval()).isEqualTo(timeBlock1.getPlanInterval());
		assertThat(timeBlockList.get(1).getEndTime()).isNull();
		assertThat(timeBlockList.get(1).getComment()).isNull();

		// Not Exist
		resultStr = this.mockMvc
				.perform(get("/api/productivity/u1/time_block").param("date", "2021-01-20")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		commonResult = mapper.readValue(resultStr, new TypeReference<CommonResult<List<TimeBlockDto>>>() {
		});

		assertThat(commonResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
		assertThat(commonResult.getData()).isNotNull();
		assertThat(commonResult.getData().size()).isEqualTo(0);
	}

	@Test
	public void getByDateRange() throws Exception {
		var timeBlock1 = new TimeBlock();
		timeBlock1.setId(timeBlockService.generate_block_id());
		timeBlock1.setCategory("test category 1");
		timeBlock1.setTarget("test target 1");
		timeBlock1.setDescription("test description 1");
		timeBlock1.setBeginTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2021-01-21T10:01:01"));
		timeBlock1.setPlanInterval(25);
		timeBlock1 = timeBlockRepository.save(timeBlock1);
		generatedIdList.add(timeBlock1.getId());

		var timeBlock2 = new TimeBlock();
		timeBlock2.setId(timeBlockService.generate_block_id());
		timeBlock2.setCategory("test category 2");
		timeBlock2.setTarget("test target 2");
		timeBlock2.setDescription("test description 2");
		timeBlock2.setBeginTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2021-01-22T11:11:11"));
		timeBlock2.setEndTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2021-01-22T12:00:00"));
		timeBlock2.setPlanInterval(30);
		timeBlock2 = timeBlockRepository.save(timeBlock2);
		// add无法添加结束时间等
		generatedIdList.add(timeBlock2.getId());

		var timeBlock3 = new TimeBlock();
		timeBlock3.setId(timeBlockService.generate_block_id());
		timeBlock3.setCategory("test category 3");
		timeBlock3.setTarget("test target 3");
		timeBlock3.setDescription("test description 2");
		timeBlock3.setBeginTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2021-01-21T03:00:01"));
		timeBlock3.setEndTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2021-01-21T10:00:10"));
		timeBlock3.setPlanInterval(20);
		timeBlock3.setComment("test common 3");
		timeBlock3 = timeBlockRepository.save(timeBlock3);
		// add无法添加结束时间等
		generatedIdList.add(timeBlock3.getId());

		ObjectMapper mapper = new ObjectMapper();

		String resultStr = this.mockMvc
				.perform(get("/api/productivity/u1/time_block").param("start", "2021-01-22").param("end", "2021-01-23")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		CommonResult<List<TimeBlockDto>> commonResult = mapper.readValue(resultStr,
				new TypeReference<CommonResult<List<TimeBlockDto>>>() {
				});

		assertThat(commonResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
		assertThat(commonResult.getData()).isNotNull();

		var timeBlockList = commonResult.getData();

		assertThat(timeBlockList.size()).isEqualTo(1);
		assertThat(timeBlockList.get(0).getBlockId()).isEqualTo(timeBlock2.getId());
		assertThat(timeBlockList.get(0).getCategory()).isEqualTo(timeBlock2.getCategory());
		assertThat(timeBlockList.get(0).getTarget()).isEqualTo(timeBlock2.getTarget());
		assertThat(timeBlockList.get(0).getDescription()).isEqualTo(timeBlock2.getDescription());
		assertThat(timeBlockList.get(0).getBeginTime().getTime()).isEqualTo(timeBlock2.getBeginTime().getTime());
		assertThat(timeBlockList.get(0).getPlanInterval()).isEqualTo(timeBlock2.getPlanInterval());
		assertThat(timeBlockList.get(0).getEndTime().getTime()).isEqualTo(timeBlock2.getEndTime().getTime());
		assertThat(timeBlockList.get(0).getComment()).isNull();

		resultStr = this.mockMvc
				.perform(get("/api/productivity/u1/time_block").param("start", "2021-01-21").param("end", "2021-01-22")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		commonResult = mapper.readValue(resultStr, new TypeReference<CommonResult<List<TimeBlockDto>>>() {
		});

		assertThat(commonResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
		assertThat(commonResult.getData()).isNotNull();
		timeBlockList = commonResult.getData();

		assertThat(timeBlockList.size()).isEqualTo(2);
		assertThat(timeBlockList.get(0).getBlockId()).isEqualTo(timeBlock3.getId());
		assertThat(timeBlockList.get(0).getCategory()).isEqualTo(timeBlock3.getCategory());
		assertThat(timeBlockList.get(0).getTarget()).isEqualTo(timeBlock3.getTarget());
		assertThat(timeBlockList.get(0).getDescription()).isEqualTo(timeBlock3.getDescription());
		assertThat(timeBlockList.get(0).getBeginTime().getTime()).isEqualTo(timeBlock3.getBeginTime().getTime());
		assertThat(timeBlockList.get(0).getPlanInterval()).isEqualTo(timeBlock3.getPlanInterval());
		assertThat(timeBlockList.get(0).getEndTime().getTime()).isEqualTo(timeBlock3.getEndTime().getTime());
		assertThat(timeBlockList.get(0).getComment()).isEqualTo(timeBlock3.getComment());

		assertThat(timeBlockList.get(1).getBlockId()).isEqualTo(timeBlock1.getId());
		assertThat(timeBlockList.get(1).getCategory()).isEqualTo(timeBlock1.getCategory());
		assertThat(timeBlockList.get(1).getTarget()).isEqualTo(timeBlock1.getTarget());
		assertThat(timeBlockList.get(1).getDescription()).isEqualTo(timeBlock1.getDescription());
		assertThat(timeBlockList.get(1).getBeginTime().getTime()).isEqualTo(timeBlock1.getBeginTime().getTime());
		assertThat(timeBlockList.get(1).getPlanInterval()).isEqualTo(timeBlock1.getPlanInterval());
		assertThat(timeBlockList.get(1).getEndTime()).isNull();
		assertThat(timeBlockList.get(1).getComment()).isNull();

		resultStr = this.mockMvc
				.perform(get("/api/productivity/u1/time_block").param("start", "2021-01-21").param("end", "2021-01-23")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		commonResult = mapper.readValue(resultStr, new TypeReference<CommonResult<List<TimeBlockDto>>>() {
		});

		assertThat(commonResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
		assertThat(commonResult.getData()).isNotNull();
		timeBlockList = commonResult.getData();
		assertThat(timeBlockList.size()).isEqualTo(3);
		assertThat(timeBlockList.get(0).getBlockId()).isEqualTo(timeBlock3.getId());
		assertThat(timeBlockList.get(1).getBlockId()).isEqualTo(timeBlock1.getId());
		assertThat(timeBlockList.get(2).getBlockId()).isEqualTo(timeBlock2.getId());

		// Not Exist
		resultStr = this.mockMvc
				.perform(get("/api/productivity/u1/time_block").param("start", "2021-01-20").param("end", "2021-01-21")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		commonResult = mapper.readValue(resultStr, new TypeReference<CommonResult<List<TimeBlockDto>>>() {
		});

		assertThat(commonResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
		assertThat(commonResult.getData()).isNotNull();
		timeBlockList = commonResult.getData();
		assertThat(timeBlockList.size()).isEqualTo(0);
	}

	@Test
	public void getByDateWithoutDateAndEndParams() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		String resultStr = this.mockMvc
				.perform(get("/api/productivity/u1/time_block").param("start", "2021-01-20")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		System.out.println("resultStr: " + resultStr);
		CommonResult<List<TimeBlockDto>> commonResult = mapper.readValue(resultStr,
				new TypeReference<CommonResult<List<TimeBlockDto>>>() {
				});
		assertThat(commonResult.getCode()).isEqualTo(ResultCode.VALIDATE_FAILED.getCode());
		assertThat(commonResult.getData()).isNull();
	}
}

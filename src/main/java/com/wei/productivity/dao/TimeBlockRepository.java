package com.wei.productivity.dao;

import com.wei.productivity.domain.TimeBlock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface TimeBlockRepository extends CrudRepository<TimeBlock, String> {
	@Query(value = "SELECT tb from TimeBlock tb where beginTime >= ?1 and beginTime < ?2 order by beginTime")
	List<TimeBlock> findByBeginTime(Date firstDate, Date endDate);
}

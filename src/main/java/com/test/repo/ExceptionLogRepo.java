package com.test.repo;

import com.test.entity.ExceptionLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author anonymity
 * @create 2018-07-25 13:28
 **/
@Repository
public interface ExceptionLogRepo extends CrudRepository<ExceptionLog, Long> {
}

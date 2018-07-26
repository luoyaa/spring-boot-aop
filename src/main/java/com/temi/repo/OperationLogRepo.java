package com.temi.repo;

import com.temi.entity.OperationLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author anonymity
 * @create 2018-07-25 13:28
 **/
@Repository
public interface OperationLogRepo extends CrudRepository<OperationLog, Long> {
}

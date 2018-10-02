package com.disney.miguelmunoz.challenge.repositories;

import java.time.Instant;
import java.util.Collection;
//import java.util.Date;
import com.disney.miguelmunoz.challenge.entities.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/20/18
 * <p>Time: 5:47 PM
 *
 * @author Miguel Mu\u00f1oz
 */
@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> {
  Collection<CustomerOrder> findByOrderTimeAfterAndOrderTimeBeforeOrderByOrderTime(Instant startOfRange, Instant endOfRange);
  
  Collection<CustomerOrder> findByOrderTimeAfterAndOrderTimeBeforeAndCompleteOrderByOrderTime(
      Instant startOfRange,
      Instant endOfRange, 
      Boolean complete
  );

  Collection<CustomerOrder> findByOrderTimeAfter(Instant orderTime);
}

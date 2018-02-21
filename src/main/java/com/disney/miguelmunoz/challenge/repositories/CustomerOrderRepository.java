package com.disney.miguelmunoz.challenge.repositories;

import com.disney.miguelmunoz.challenge.entities.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 2/20/18
 * <p>Time: 5:47 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> { }

package com.cognizant.pas.policy.repository;

import java.util.Optional;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cognizant.pas.policy.models.ConsumerPolicy;

@Repository
@Transactional
@DynamicUpdate
public interface ConsumerPolicyRepository extends JpaRepository<ConsumerPolicy, Long> {

	
	 Optional<ConsumerPolicy> findById(long id);
    boolean existsByConsumerid(Long consumerid);
    
   
    ConsumerPolicy findByConsumerid(Long consumerid);

    ConsumerPolicy findByConsumeridAndBusinessid(Long consumerid, Long businessid);

}

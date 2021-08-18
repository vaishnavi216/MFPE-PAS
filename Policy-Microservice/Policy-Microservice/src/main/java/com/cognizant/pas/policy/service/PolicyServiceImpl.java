package com.cognizant.pas.policy.service;

import static com.cognizant.pas.policy.util.PolicyConstant.CONSUMER_POLICY;
import static com.cognizant.pas.policy.util.PolicyConstant.acceptDetail;
import static com.cognizant.pas.policy.util.PolicyConstant.consumerBusinessNotFound;
import static com.cognizant.pas.policy.util.PolicyConstant.coveredSum;
import static com.cognizant.pas.policy.util.PolicyConstant.duration;
import static com.cognizant.pas.policy.util.PolicyConstant.effectiveDate;
import static com.cognizant.pas.policy.util.PolicyConstant.issuedStatus;
import static com.cognizant.pas.policy.util.PolicyConstant.paymentDetail;
import static com.cognizant.pas.policy.util.PolicyConstant.policyStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.pas.policy.exception.AuthorizationException;
import com.cognizant.pas.policy.exception.ConsumerBusinessNotFoundException;
import com.cognizant.pas.policy.exception.ConsumerPolicyNotFoundException;
import com.cognizant.pas.policy.exception.PolicyNotFoundException;
import com.cognizant.pas.policy.feign.ConsumerClient;
import com.cognizant.pas.policy.feign.QuotesClient;
import com.cognizant.pas.policy.models.ConsumerPolicy;
import com.cognizant.pas.policy.models.PolicyMaster;
import com.cognizant.pas.policy.payload.request.CreatePolicyRequest;
import com.cognizant.pas.policy.payload.request.IssuePolicyRequest;
import com.cognizant.pas.policy.payload.response.ConsumerPolicyDetails;
import com.cognizant.pas.policy.payload.response.MessageResponse;
import com.cognizant.pas.policy.payload.response.PolicyDetailsResponse;
import com.cognizant.pas.policy.payload.response.QuotesDetailsResponse;
import com.cognizant.pas.policy.repository.ConsumerPolicyRepository;
import com.cognizant.pas.policy.repository.PolicyMasterRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PolicyServiceImpl implements PolicyService {

	@Autowired
	ConsumerPolicyRepository consumerPolicyRepository;

	@Autowired
	PolicyMasterRepository policyMasterRepository;

	@Autowired
	ConsumerClient consumerClient;

	@Autowired
	QuotesClient quotesclient;

	@Override
	public QuotesDetailsResponse getQuotes(String requestTokenHeader, Long businessValue, Long propertyValue,
			String propertyType) {
		/**
		 * This method is to returns Quotes details based on the business value,property
		 * value and property type.
		 */
		log.info("Start getQuotesService");
		String quote;
		try {
			quote = quotesclient.getQuotesForPolicy(requestTokenHeader, businessValue, propertyValue, propertyType);
			log.debug("quote : {}", quote);
			log.info("End getQuotesService");
			return (new QuotesDetailsResponse(quote));
		} catch (AuthorizationException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public PolicyDetailsResponse viewPolicy(Long consumerid, long id)
			throws PolicyNotFoundException, ConsumerPolicyNotFoundException {
		/**
		 * This method is to view the consumer policy details.
		 */
		log.info("Start viewPolicyService");
		Optional<ConsumerPolicy> consumerp = consumerPolicyRepository.findById(id);
		ConsumerPolicy cons = consumerp.get();
		PolicyMaster policyMaster = policyMasterRepository.findByPid(cons.getPolicyid());
		log.debug("policyMaster : {}", policyMaster);

		PolicyDetailsResponse policyDetailsResponse = new PolicyDetailsResponse(consumerid, policyMaster.getPid(),
				policyMaster.getProperty_type(), policyMaster.getConsumer_type(), policyMaster.getAssured_sum(),
				policyMaster.getTenure(), policyMaster.getBusiness_value(), policyMaster.getProperty_value(),
				policyMaster.getBase_location(), policyMaster.getType(), cons.getBusinessid(), cons.getPaymentdetails(),
				cons.getAcceptancestatus(), cons.getPolicystatus(), cons.getEffectivedate(), cons.getCovered_sum(),
				cons.getDuration(), cons.getAcceptedquote());
		log.debug("policyDetailsResponse : {}", policyDetailsResponse);
		log.info("End viewPolicyService");
		return policyDetailsResponse;
	}

	@Override
	public MessageResponse createPolicy(CreatePolicyRequest createPolicyRequest, String requestTokenHeader)
			throws ConsumerBusinessNotFoundException {

		/**
		 * This method is to respond whether the policy is created or not.
		 */
		log.info("Start createPolicyService");
		ConsumerPolicyDetails consumerPolicyDetail = getConsumerBusiness(requestTokenHeader,
				createPolicyRequest.getConsumerid());
		if (consumerPolicyDetail == null) {
			return new MessageResponse(consumerBusinessNotFound);
		}
		log.debug("consumerBusinessDetails : {}", consumerPolicyDetail);

		List<PolicyMaster> policymaster = policyMasterRepository.findAll();

		List<PolicyMaster> id = policymaster.stream()
				.filter(p -> p.getBase_location().equalsIgnoreCase(consumerPolicyDetail.getLocation()))
				.filter(p -> p.getType().equals(consumerPolicyDetail.getInsurancetype()))
				.filter(p -> p.getProperty_type().equals(consumerPolicyDetail.getPropertytype()))
				.filter(p -> p.getConsumer_type().equals(consumerPolicyDetail.getBuildingtype()))
				.collect(Collectors.toList());

		ConsumerPolicy consumerPolicy = new ConsumerPolicy(consumerPolicyDetail.getConsumerId(),
				consumerPolicyDetail.getBusinessId(), id.get(0).getPid(), acceptDetail, paymentDetail, policyStatus,
				createPolicyRequest.getAcceptedquotes(), effectiveDate, duration, coveredSum);
		log.debug(CONSUMER_POLICY, consumerPolicy);
		ConsumerPolicy consumerPolicysave = consumerPolicyRepository.save(consumerPolicy);
		log.debug("consumerPolicysave : {}", consumerPolicysave);
		log.info("End createPolicyService");
		return new MessageResponse("Policy Has been Created with Policyconsumer Id : " + consumerPolicysave.getId()
				+ " .Thank You Very Much!!");
	}

	@Override
	public MessageResponse issuePolicy(IssuePolicyRequest issuePolicyRequest)
			throws ConsumerPolicyNotFoundException, PolicyNotFoundException {
		/**
		 * This method is to issue the policy based on the agent's input and returns it.
		 */
		log.info("Start issuePolicyService");
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		// ConsumerPolicy consumerPolicy =
		// consumerPolicyRepository.findByConsumeridAndBusinessid(issuePolicyRequest.getConsumerid(),
		// issuePolicyRequest.getBusinessid());

		Optional<ConsumerPolicy> policyMaster = consumerPolicyRepository.findById(issuePolicyRequest.getId());
		ConsumerPolicy cons = policyMaster.get();
		PolicyMaster p = policyMasterRepository.findByPid(policyMaster.get().getPolicyid());
		log.debug("policyMaster : {}", policyMaster);

		cons.setPolicyid(policyMaster.get().getPolicyid());
		cons.setPaymentdetails(issuePolicyRequest.getPaymentdetails());
		cons.setAcceptancestatus(issuePolicyRequest.getAcceptancestatus());
		cons.setPolicystatus(issuedStatus);
		cons.setEffectivedate(dtf.format(now));
		cons.setDuration(p.getTenure());
		cons.setCovered_sum(p.getAssured_sum());
		ConsumerPolicy consumerPolicySave = consumerPolicyRepository.save(cons);
		log.debug("consumerPolicySave : {}", cons);
		log.info("End issuePolicyService");
		return new MessageResponse(
				"Policy has Issued to PolicyConsumer Id : " + consumerPolicySave.getId() + " .Thank You Very Much!!");
	}

	@Override
	public ConsumerPolicyDetails getConsumerBusiness(String requestTokenHeader, Long consumerid) {
		/**
		 * This method is to returns ConsumerPolicyDetails based on consumerId
		 */
		log.info("Start getConsumerBusiness");
		ConsumerPolicyDetails consumerBusinessDetails;
		try {
			consumerBusinessDetails = consumerClient.viewConsumerBusinessbypolicy(consumerid, requestTokenHeader);
			log.debug("consumerBusinessDetails : {}", consumerBusinessDetails);
			log.info("End getConsumerBusiness");
			return consumerBusinessDetails;
		} catch (AuthorizationException e) {
			e.printStackTrace();
		}
		return null;
	}

}

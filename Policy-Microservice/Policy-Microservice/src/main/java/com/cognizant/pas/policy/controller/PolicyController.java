package com.cognizant.pas.policy.controller;

import static com.cognizant.pas.policy.util.PolicyConstant.NOT_ALLOWED;
import static com.cognizant.pas.policy.util.PolicyConstant.SUCCESS;
import static com.cognizant.pas.policy.util.PolicyConstant.acceptenceStatus;
import static com.cognizant.pas.policy.util.PolicyConstant.consumerNotFound;
import static com.cognizant.pas.policy.util.PolicyConstant.paymentStatus;
import static com.cognizant.pas.policy.util.PolicyConstant.policyNotFound;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.pas.policy.exception.AuthorizationException;
import com.cognizant.pas.policy.exception.ConsumerPolicyNotFoundException;
import com.cognizant.pas.policy.feign.AuthorisingClient;
import com.cognizant.pas.policy.models.ConsumerPolicy;
import com.cognizant.pas.policy.payload.request.CreatePolicyRequest;
import com.cognizant.pas.policy.payload.request.IssuePolicyRequest;
import com.cognizant.pas.policy.payload.response.MessageResponse;
import com.cognizant.pas.policy.payload.response.PolicyDetailsResponse;
import com.cognizant.pas.policy.payload.response.QuotesDetailsResponse;
import com.cognizant.pas.policy.repository.ConsumerPolicyRepository;
import com.cognizant.pas.policy.repository.PolicyMasterRepository;
import com.cognizant.pas.policy.service.PolicyService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class PolicyController {

	@Autowired
	private AuthorisingClient authorisingClient;

	@Autowired
	PolicyService policyService;

	@Autowired
	PolicyMasterRepository policyMasterRepository;

	@Autowired
	ConsumerPolicyRepository consumerPolicyRepository;

	/***
	 * This method is to create a new policy for the consumer.
	 * 
	 * @param requestTokenHeader
	 * @param createPolicyRequest
	 * @return
	 * @throws AuthorizationException
	 */
	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/createPolicy")
	@ApiOperation(notes = "creates a new policy for the consumer", value = "creates a policy")
	public MessageResponse createPolicy(
			@RequestHeader(value = "Authorization", required = true) String requestTokenHeader,
			@ApiParam(name = "createPolicyRequest", value = "Policy request details") @Valid @RequestBody CreatePolicyRequest createPolicyRequest)
			throws AuthorizationException {
		log.info("Start createPolicy");
		if (authorisingClient.authorizeTheRequest(requestTokenHeader)) {
			MessageResponse messageResponse = policyService.createPolicy(createPolicyRequest, requestTokenHeader);
			log.debug("MessageResponse : {}", messageResponse);
			log.info("End createPolicy");
			return (messageResponse);
		} else {
			throw new AuthorizationException(NOT_ALLOWED);
		}
	}

	/***
	 * This method is to issue's policy to the consumer business if their business
	 * is eligible for one of the policies.
	 * 
	 * @param requestTokenHeader
	 * @param issuePolicyRequest
	 * @return
	 * @throws AuthorizationException
	 */
	@CrossOrigin(origins = "http://localhost:4200")
	@PostMapping("/issuePolicy")
	@ApiOperation(notes = "issues the consumer's policy if it is accepted", value = "issues policy")
	public MessageResponse issuePolicy(
			@RequestHeader(value = "Authorization", required = true) String requestTokenHeader,
			@ApiParam(name = "issuePolicyRequest", value = "Policy issue details") @Valid @RequestBody IssuePolicyRequest issuePolicyRequest)
			throws AuthorizationException {
		log.info("Start issuePolicy");
		if (authorisingClient.authorizeTheRequest(requestTokenHeader)) {
			if (!consumerPolicyRepository.existsByConsumerid(issuePolicyRequest.getConsumerid())) {
				return (new MessageResponse(consumerNotFound));
			}

			Optional<ConsumerPolicy> policyMaster = consumerPolicyRepository.findById(issuePolicyRequest.getId());
			if (policyMaster.get().getId() == null) {
				return (new MessageResponse(policyNotFound));
			}
			if (!(SUCCESS.equalsIgnoreCase(issuePolicyRequest.getPaymentdetails()))) {
				return (new MessageResponse(paymentStatus));
			}
			if (!(issuePolicyRequest.getAcceptancestatus().equals("Accepted"))) {
				return (new MessageResponse(acceptenceStatus));
			}
			MessageResponse messageResponse = policyService.issuePolicy(issuePolicyRequest);
			log.debug("MessageResponse : {}", messageResponse);
			log.info("End issuePolicy");
			return (messageResponse);
		} else {
			throw new AuthorizationException(NOT_ALLOWED);
		}
	}

	/***
	 * This method is to view the policy of a consumer using their consumerid and
	 * policyid.
	 * 
	 * @param requestTokenHeader
	 * @param consumerid
	 * @param policyid
	 * @return
	 * @throws AuthorizationException
	 */
	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping("/viewPolicy/{consumerid}/{id}")
	@ApiOperation(notes = "returns a particular consumer's policy", value = "displays the policy")
	public ResponseEntity<PolicyDetailsResponse> viewPolicy(
			@RequestHeader(value = "Authorization", required = true) String requestTokenHeader,
			@ApiParam(name = "consumerid", value = "Id of the Consumer") @Valid @PathVariable Long consumerid,
			@ApiParam(name = "policyid", value = "Id of the Policy") @PathVariable long id)
			throws AuthorizationException {
		log.info("Start viewPolicy");
		if (authorisingClient.authorizeTheRequest(requestTokenHeader)) {
			log.info("Auth Successful");
			Optional<ConsumerPolicy> policyMaster = consumerPolicyRepository.findById(id);
			if (policyMaster.get().getId() == null) {
				new ConsumerPolicyNotFoundException(policyNotFound);
			}
			if (!consumerPolicyRepository.existsByConsumerid(consumerid)) {
				log.info("Policy in master table");
				throw new ConsumerPolicyNotFoundException(consumerNotFound);
			}
			log.info("Consumer found");
			PolicyDetailsResponse policyDetailsResponse = policyService.viewPolicy(consumerid, id);
			log.debug("PolicyDetailsResponse: {}", policyDetailsResponse);
			log.info("End viewPolicy");
			return ResponseEntity.ok(policyDetailsResponse);
		} else {
			throw new AuthorizationException(NOT_ALLOWED);
		}
	}

	/***
	 * This method is to get the quotes for the given business value and property
	 * value.
	 * 
	 * @param requestTokenHeader
	 * @param businessValue
	 * @param propertyValue
	 * @param propertyType
	 * @return
	 * @throws AuthorizationException
	 */
	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping("/getQuotes/{businessValue}/{propertyValue}/{propertyType}")
	@ApiOperation(notes = "returns the quotes for the given business value and property value", value = "displays the quotes")
	public ResponseEntity<QuotesDetailsResponse> getQuotes(
			@RequestHeader(value = "Authorization", required = true) String requestTokenHeader,
			@ApiParam(name = "businessValue", value = "Value of the Business") @Valid @PathVariable Long businessValue,
			@ApiParam(name = "propertyValue", value = "Value of the Property") @PathVariable Long propertyValue,
			@ApiParam(name = "propertyType", value = "Type of the Property") @PathVariable String propertyType)
			throws AuthorizationException {
		log.info("Start getQuotes");
		if (authorisingClient.authorizeTheRequest(requestTokenHeader)) {
			QuotesDetailsResponse quotesDetailsResponse = policyService.getQuotes(requestTokenHeader, businessValue,
					propertyValue, propertyType);
			log.debug("QuotesMaster: {}", quotesDetailsResponse);
			log.info("End getQuotes");
			return ResponseEntity.ok(quotesDetailsResponse);
		} else {
			throw new AuthorizationException(NOT_ALLOWED);
		}
	}

}

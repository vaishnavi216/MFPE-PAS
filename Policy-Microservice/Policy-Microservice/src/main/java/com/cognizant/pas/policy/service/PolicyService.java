package com.cognizant.pas.policy.service;

import org.springframework.stereotype.Service;

import com.cognizant.pas.policy.payload.request.CreatePolicyRequest;
import com.cognizant.pas.policy.payload.request.IssuePolicyRequest;
import com.cognizant.pas.policy.payload.response.ConsumerPolicyDetails;
import com.cognizant.pas.policy.payload.response.MessageResponse;
import com.cognizant.pas.policy.payload.response.PolicyDetailsResponse;
import com.cognizant.pas.policy.payload.response.QuotesDetailsResponse;

@Service
public interface PolicyService {
    
    /***
     * 
     * @param requestTokenHeader
     * @param businessValue
     * @param propertyValue
     * @param propertyType
     * @return
     */
    QuotesDetailsResponse getQuotes(String requestTokenHeader, Long businessValue, Long propertyValue, String propertyType);
    
    /***
     * 
     * @param consumerid
     * @param policyid
     * @return
     */
    PolicyDetailsResponse viewPolicy(Long consumerid, long id);
    
    /***
     * 
     * @param issuePolicyRequest
     * @return
     */
    MessageResponse issuePolicy(IssuePolicyRequest issuePolicyRequest);
    
    /***
     * 
     * @param createPolicyRequest
     * @param requestTokenHeader
     * @return
     */
    MessageResponse createPolicy(CreatePolicyRequest createPolicyRequest, String requestTokenHeader);
    
    /***
     * 
     * @param requestTokenHeader
     * @param consumerid
     * @return
     */
    ConsumerPolicyDetails getConsumerBusiness(String requestTokenHeader, Long consumerid);

}

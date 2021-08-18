package com.cognizant.pas.consumer.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.pas.consumer.exception.BusinessPropertyNotFoundException;
import com.cognizant.pas.consumer.exception.ConsumerBusinessNotFoundException;
import com.cognizant.pas.consumer.model.Business;
import com.cognizant.pas.consumer.model.BusinessMaster;
import com.cognizant.pas.consumer.model.Consumer;
import com.cognizant.pas.consumer.model.Property;
import com.cognizant.pas.consumer.model.PropertyMaster;
import com.cognizant.pas.consumer.payload.request.BusinessPropertyRequest;
import com.cognizant.pas.consumer.payload.request.ConsumerBusinessRequest;
import com.cognizant.pas.consumer.payload.response.BusinessPropertyDetails;
import com.cognizant.pas.consumer.payload.response.ConsumerBusinessDetails;
import com.cognizant.pas.consumer.payload.response.ConsumerPolicyDetails;
import com.cognizant.pas.consumer.payload.response.MessageResponse;
import com.cognizant.pas.consumer.repository.BusinessMasterRepository;
import com.cognizant.pas.consumer.repository.BusinessRepository;
import com.cognizant.pas.consumer.repository.ConsumerRepository;
import com.cognizant.pas.consumer.repository.PropertyMasterRepository;
import com.cognizant.pas.consumer.repository.PropertyRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

	@Autowired
	BusinessRepository businessRepository;

	@Autowired
	ConsumerRepository consumerRepository;

	@Autowired
	PropertyRepository propertyRepository;

	@Autowired
	BusinessMasterRepository businessMasterRepository;

	@Autowired
	PropertyMasterRepository propertyMasterRepository;

	@Override
	public MessageResponse createConsumerBusiness(ConsumerBusinessRequest consumerBusinessRequest) {
		/**
		 * This method is to respond whether the consumer buisness is created or not.
		 */
		log.info("Start createConsumerBusiness Service");
		Consumer consumer = new Consumer(consumerBusinessRequest.getFirstname(), consumerBusinessRequest.getLastname(),
				consumerBusinessRequest.getDob(), consumerBusinessRequest.getLocation(),
				consumerBusinessRequest.getBusinessname(), consumerBusinessRequest.getPandetails(),
				consumerBusinessRequest.getEmail(), consumerBusinessRequest.getPhone(),
				consumerBusinessRequest.getWebsite(), consumerBusinessRequest.getBusinessoverview(),
				consumerBusinessRequest.getValidity(), consumerBusinessRequest.getAgentname(),
				consumerBusinessRequest.getAgentid());
		log.debug("Consumer: {}", consumer);
		Consumer consumersave = consumerRepository.save(consumer);
		log.debug("Consumer Save: {}", consumersave);
		Long businessvalue = calculateBusinessValue(consumerBusinessRequest.getBusinessturnover(),
				consumerBusinessRequest.getCapitalinvested());
		log.debug("BusinessValue: {}", businessvalue);
		Business business = new Business(consumersave.getId(), consumerBusinessRequest.getBusinesscategory(),
				consumerBusinessRequest.getBusinesstype(), consumerBusinessRequest.getBusinessturnover(),
				consumerBusinessRequest.getCapitalinvested(), consumerBusinessRequest.getTotalemployees(),
				businessvalue, consumerBusinessRequest.getBusinessage());
		log.debug("Business: {}", business);
		Business businesssave = businessRepository.save(business);
		log.debug("Business Save: {}", businesssave);
		log.info("End createConsumerBusinessService");
		return new MessageResponse("SuccessFully Created Consumer with Consumer ID: " + consumersave.getId()
				+ " and Business ID: " + businesssave.getId() + " with Business Value : "
				+ businesssave.getBusinessvalue() + ".Thank you!!");
	}

	@Override
	public Long calculateBusinessValue(Long businessturnover, Long capitalinvested) {
		/**
		 * This method is to calculate the buisness value for the given consumer's
		 * buisness.
		 */
		log.info("Start CalculateBusinessValue");
		if (businessturnover == 0 || capitalinvested == 0 || (businessturnover.equals(capitalinvested))) {
			throw new NullPointerException("NullPointerException in CalculateBusinessValue");
		}

		Double x_ratio = (double) (businessturnover / capitalinvested);
		log.debug("X_ratio: {}", x_ratio);
		Double Range_min = 0D;
		Double Range_max = 10D;
		Double x_max = (double) businessturnover;
		Double x_min = (double) capitalinvested;
		Double range_diff = (double) (Range_max - Range_min);
		log.debug("range_diff : {}", range_diff);
		Double sat = ((x_ratio - x_min) / (x_max - x_min));
		log.debug("(x_ratio - x_min) / (x_max - x_min): {}", sat);
		Double businessvalue = (range_diff * sat);
		log.debug("BusinessValue: {}", businessvalue);
		log.info("End CalBusinessValue");
		return (long) Math.abs(Math.round(businessvalue));
	}

	@Override
	public MessageResponse updateConsumerBusiness(ConsumerBusinessDetails consumerBusinessDetails)
			throws BusinessPropertyNotFoundException {
		/**
		 * This method is to update the details of the consumer business.
		 */
		log.info("Start updateConsumerBusinessService");
		Optional<Consumer> consumer = consumerRepository.findById(consumerBusinessDetails.getConsumerId());
		log.debug("Consumer Set: {}", consumer);
		Consumer consumers = consumer.get();
		log.debug("Consumer: {}", consumers);

		Business business = businessRepository.findByConsumerId(consumerBusinessDetails.getConsumerId());
		log.debug("Business: {}", business);
		consumers.setFirstname(consumerBusinessDetails.getFirstname());
		consumers.setLastname(consumerBusinessDetails.getLastname());
		consumers.setDob(consumerBusinessDetails.getDob());
		consumers.setLocation(consumerBusinessDetails.getLocation());
		consumers.setBusinessname(consumerBusinessDetails.getBusinessname());
		consumers.setPandetails(consumerBusinessDetails.getPandetails());
		consumers.setEmail(consumerBusinessDetails.getEmail());
		consumers.setPhone(consumerBusinessDetails.getPhone());
		consumers.setWebsite(consumerBusinessDetails.getWebsite());
		consumers.setBusinessname(consumerBusinessDetails.getBusinessname());
		consumers.setBusinessoverview(consumerBusinessDetails.getBusinessoverview());
		consumers.setValidity(consumerBusinessDetails.getValidity());
		consumers.setAgentname(consumerBusinessDetails.getAgentname());
		consumers.setAgentid(consumerBusinessDetails.getAgentid());

		Consumer consumersave = consumerRepository.save(consumers);
		log.debug("Consumer Save: {}", consumersave);
		business.setBusinesscategory(consumerBusinessDetails.getBusinesscategory());
		business.setBusinesstype(consumerBusinessDetails.getBusinesscategory());
		business.setBusinessturnover(consumerBusinessDetails.getBusinessturnover());
		business.setCapitalinvested(consumerBusinessDetails.getCapitalinvested());
		business.setTotalemployees(consumerBusinessDetails.getTotalemployees());
		business.setBusinessvalue(
				calculateBusinessValue(business.getBusinessturnover(), business.getCapitalinvested()));
		business.setBusinessage(consumerBusinessDetails.getBusinessage());

		Business businesssave = businessRepository.save(business);
		log.debug("Business Save: {}", businesssave);
		log.info("End updateConsumerBusinessService");
		return new MessageResponse("Successfully Updated Consumer with Consumer ID: " + consumersave.getId()
				+ " and Business ID: " + businesssave.getId() + ". Thank you!");
	}

	@Override
	public ConsumerBusinessDetails viewConsumerBusiness(Long consumerid) throws ConsumerBusinessNotFoundException {
		/**
		 * This method is to view the consumer business details.
		 */
		log.info("Start viewConsumerBusinessService");
		Optional<Consumer> consumer = Optional.ofNullable(consumerRepository.findById(consumerid)
				.orElseThrow(() -> new ConsumerBusinessNotFoundException("Consumer not found")));
		log.debug("Consumer List : {}", consumer);
		Consumer consumers = consumer.get();
		log.debug("Consumer : {}", consumers);
		Business business = businessRepository.findByConsumerId(consumerid);
		log.debug("Business : {}", business);
		ConsumerBusinessDetails consumerBusinessDetails = new ConsumerBusinessDetails(consumers.getFirstname(),
				consumers.getLastname(), consumers.getDob(), consumers.getLocation(), consumers.getBusinessname(),
				consumers.getPandetails(), consumers.getEmail(), consumers.getPhone(), consumers.getWebsite(),
				consumers.getBusinessoverview(), consumers.getValidity(), consumers.getAgentname(),
				consumers.getAgentid(), business.getId(), business.getConsumerId(), business.getBusinesscategory(),
				business.getBusinesstype(), business.getBusinessturnover(), business.getCapitalinvested(),
				business.getTotalemployees(), business.getBusinessvalue(), business.getBusinessage()

		);
		log.debug("ConsumerBusinessDetails : {}", consumerBusinessDetails);
		log.info("End viewConsumerBusinessService");
		return consumerBusinessDetails;
	}

	@Override
	public MessageResponse createBusinessProperty(BusinessPropertyRequest businessPropertyRequest) {
		/**
		 * This method is to create business property and to return the property id.
		 */
		log.info("Start createConsumerBusinessService");
		Long propertyValue = calculatePropertyValue(businessPropertyRequest.getCostoftheasset(),
				businessPropertyRequest.getSalvagevalue(), businessPropertyRequest.getUsefullifeoftheAsset());
		log.debug("propertyValue : {}", propertyValue);
		Property property = new Property(businessPropertyRequest.getBusinessId(),
				businessPropertyRequest.getConsumerId(), businessPropertyRequest.getInsurancetype(),
				businessPropertyRequest.getPropertytype(), businessPropertyRequest.getBuildingsqft(),
				businessPropertyRequest.getBuildingtype(), businessPropertyRequest.getBuildingstoreys(),
				businessPropertyRequest.getBuildingage(), propertyValue, businessPropertyRequest.getCostoftheasset(),
				businessPropertyRequest.getSalvagevalue(), businessPropertyRequest.getUsefullifeoftheAsset());
		log.debug("property : {}", property);
		Property propertysave = propertyRepository.save(property);
		log.debug("propertysave : {}", propertysave);
		log.info("End createConsumerBusinessService");
		return new MessageResponse("SuccessFully Created Business Property with Property Id: " + propertysave.getId()
				+ " and Property Value :" + property.getPropertyvalue() + ". Thank you!!");
	}

	@Override
	public Long calculatePropertyValue(Long costoftheasset, Long salvagevalue, Long usefullifeoftheAsset) {
		/**
		 * This method is to calculate the property value and returns it.
		 */
		log.info("Start calculatePropertyValue");
		if (usefullifeoftheAsset == 0 || salvagevalue == 0 || costoftheasset == 0
				|| (costoftheasset.equals(salvagevalue))) {
			throw new NullPointerException("NullPointerException in calculatePropertyValue");
		}
		Double propertyvalue = (double) ((costoftheasset - salvagevalue) / usefullifeoftheAsset);
		if (propertyvalue <= 0) {
			propertyvalue = 1.0;
		}
		return (long) Math.abs(Math.round(propertyvalue));
	}

	@Override
	public MessageResponse updateBusinessProperty(BusinessPropertyDetails businessPropertyDetails) {
		/**
		 * This method is to update the buisness property of the consumer's business.
		 */
		log.info("Start updateBusinessPropertyService");
		Property property = propertyRepository.findByConsumerId(businessPropertyDetails.getConsumerId());
		log.debug("property  : {}", property);
		Long propertyValue = calculatePropertyValue(businessPropertyDetails.getCostoftheasset(),
				businessPropertyDetails.getSalvagevalue(), businessPropertyDetails.getUsefullifeoftheAsset());
		log.debug("propertyValue  : {}", propertyValue);
		property.setInsurancetype(businessPropertyDetails.getInsurancetype());
		property.setPropertytype(businessPropertyDetails.getPropertytype());
		property.setBuildingsqft(businessPropertyDetails.getBuildingsqft());
		property.setBuildingtype(businessPropertyDetails.getBuildingtype());
		property.setBuildingstoreys(businessPropertyDetails.getBuildingstoreys());
		property.setBuildingage(businessPropertyDetails.getBuildingage());
		property.setPropertyvalue(propertyValue);
		property.setCostoftheasset(businessPropertyDetails.getCostoftheasset());
		property.setSalvagevalue(businessPropertyDetails.getSalvagevalue());
		property.setUsefullifeoftheAsset(businessPropertyDetails.getUsefullifeoftheAsset());

		Property propertysave = propertyRepository.save(property);
		log.debug("propertysave  : {}", propertysave);
		log.info("End updateBusinessPropertyService");
		return new MessageResponse(
				"SuccessFully Updated Business Property with Property Id :" + propertysave.getId() + " . Thank you!");
	}

	@Override
	public Property viewConsumerProperty(Long consumerid, Long propertyid) {
		/**
		 * This method is to view the consumer property by using consumer and property
		 * id.
		 */
		log.info("Start viewConsumerPropertyService");
		Property property = propertyRepository.findByConsumerId(consumerid);
		log.debug("property  : {}", property);
		log.info("End viewConsumerPropertyService");
		return property;
	}

	@Override
	public boolean checkBusinessEligibility(ConsumerBusinessRequest consumerBusinessRequest) throws Exception {
		/**
		 * This method is to check whether the buisness is eligible for the insurance.
		 */
		boolean check = false;
		log.info("Start checkBusinessEligibility");

		BusinessMaster businessMaster = businessMasterRepository.findByBusinesscategoryAndBusinesstype(
				consumerBusinessRequest.getBusinesscategory(), consumerBusinessRequest.getBusinesstype());
		if (businessMaster == null) {
			return check;
		}
		log.debug("Business Master {}", businessMaster);
		if (businessMaster.getTotalemployees() <= consumerBusinessRequest.getTotalemployees()
				|| businessMaster.getBusinessage() <= consumerBusinessRequest.getBusinessage()) {
			check = true;
		}
		log.debug("Vaule of Check: {}", check);
		log.info("End checkBusinessEligibility");
		return check;
	}

	@Override
	public boolean checkPropertyEligibility(String propertytype, String insurancetype, String buildingtype,
			Long buildingage) throws Exception {
		/**
		 * This method is to check whether the property of the consumer is eligible for
		 * insurance or not.
		 */
		log.info("Start checkPropertyEligibility");
		boolean check = false;
		PropertyMaster propertyMaster = propertyMasterRepository
				.findByPropertytypeAndInsurancetypeAndBuildingtype(propertytype, insurancetype, buildingtype);
		if (propertyMaster == null) {
			return check;
		}
		log.debug("propertyMaster {}", propertyMaster);
		if (propertyMaster.getBuildingage() <= buildingage) {
			check = true;
			log.debug("Vaule of Check {}", check);
		}
		log.debug("Vaule of Check {}", check);
		log.info("End checkPropertyEligibility");
		return check;
	}

	@Override
	public ConsumerPolicyDetails viewConsumerPolicy(Long consumerid) throws ConsumerBusinessNotFoundException {
		/**
		 * This method is to view the consumer buisness details.
		 */
		log.info("Start viewConsumerBusinessService");
		// Optional<Consumer>
		Optional<Consumer> consumer = Optional.ofNullable(consumerRepository.findById(consumerid)
				.orElseThrow(() -> new ConsumerBusinessNotFoundException("Consumer not found")));
		log.debug("Consumer List : {}", consumer);
		Consumer consumers = consumer.get();
		log.debug("Consumer : {}", consumers);
		Business business = businessRepository.findByConsumerId(consumerid);
		log.debug("Business : {}", business);

		Property property = propertyRepository.findByConsumerId(consumerid);
		log.debug("property: {}", property);
		ConsumerPolicyDetails consumerPolicyDetails = new ConsumerPolicyDetails(property.getId(), business.getId(),
				business.getConsumerId(), consumers.getLocation(), property.getInsurancetype(),
				property.getPropertytype(), property.getPropertyvalue(), business.getBusinesscategory(),
				business.getBusinesstype(), business.getBusinessvalue(), property.getBuildingtype());

		log.debug("ConsumerPolicyDetails : {}", consumerPolicyDetails);
		log.info("End viewConsumerBusinessService");
		return consumerPolicyDetails;
	}

}
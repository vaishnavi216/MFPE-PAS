package com.cognizant.pas.policy.util;

public final class PolicyConstant {
	PolicyConstant() throws IllegalAccessException {
		throw new IllegalAccessException("Constant class");
	}

	public static final String SUCCESS = "Success";

	public static final String NOT_ALLOWED = "Not allowed";

	public static final String CONSUMER_POLICY = "consumerPolicy : {}";

	public static final String policyNotFound = "Sorry!!, No Policy Found!!";

	public static final String consumerNotFound = "Sorry!!, No Consumer Found!!";

	public static final String paymentStatus = "Sorry!!, Payment Failed!! Try Again";

	public static final String acceptenceStatus = "Sorry!!, Accepted Failed !! Try Again";

	public static final String consumerBusinessNotFound = "No Consumer Business Found !!";

	public static final String acceptDetail = "Pending";

	public static final String paymentDetail = "Not Paid..";

	public static final String policyStatus = "Initiated";

	public static final String effectiveDate = "Not Started";

	public static final String duration = "Not Started";

	public static final String coveredSum = "Pending";

	public static final String issuedStatus = "Issued";
}
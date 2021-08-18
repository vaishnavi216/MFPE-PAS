package com.cognizant.pas.policy.payload.response;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 @Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerPolicyDetails {

	@NotNull
    private Long propertyId;
    
    @NotNull
    private Long businessId;
    
   @NotNull
    private Long consumerId;
    
    @NotBlank
    @Size(max = 30)
    private String location;
    
    
    @NotBlank
    @Size(max = 30)
    private String insurancetype;
    
    @NotBlank
    @Size(max = 30)
    private String propertytype;
    	
    @NotNull
    private Long propertyvalue;
 
	    @NotBlank
	    @Size(max = 40)
	    private String businesscategory;
	    @NotBlank
	    @Size(max = 40)
	    private String businesstype;
	    
	    @NotNull
	    private Long businessvalue;
	    @NotNull
	    private String buildingtype;
	    
	   
}

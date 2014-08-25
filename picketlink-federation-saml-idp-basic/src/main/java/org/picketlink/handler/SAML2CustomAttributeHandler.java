package org.picketlink.handler;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.jacc.PolicyContext;
import javax.servlet.http.HttpServletRequest;

import org.picketlink.identity.federation.core.interfaces.AttributeManager;

public class SAML2CustomAttributeHandler implements AttributeManager{

	@Override
	public Map<String, Object> getAttributes(Principal userPrincipal, List<String> attributeKeys) {
    	Map<String, Object> m = new HashMap<String, Object>();
    	
	    try{
	    	HttpServletRequest request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
			m.put("sessionID", request.getSession().getId());
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
		return m;
	}

}

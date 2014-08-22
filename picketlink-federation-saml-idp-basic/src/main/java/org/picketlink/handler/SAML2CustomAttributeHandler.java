package org.picketlink.handler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.picketlink.identity.federation.core.interfaces.AttributeManager;

public class SAML2CustomAttributeHandler implements AttributeManager{

	@Override
	public Map<String, Object> getAttributes(Principal userPrincipal,
			List<String> attributeKeys) {
		System.out.println("OI");
		// TODO Auto-generated method stub
		return null;
	}

}

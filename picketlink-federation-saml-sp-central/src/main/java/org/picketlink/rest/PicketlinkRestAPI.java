package org.picketlink.rest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.picketlink.utils.ElementNotFound;
import org.picketlink.utils.IOUtils;

/**
 * @author Rodrigo Ramalho da Silva
 * 		   hodrigohamalho@gmail.com
 */
@Path("/")
public class PicketlinkRestAPI {

	private static final Logger log = Logger.getLogger(PicketlinkRestAPI.class.getName());
	private Header[] headers;
	private final String COOKIE = "cookie";
	private final String SAMLRequest = "SAMLRequest";
	private final String SAMLResponse = "SAMLResponse";
	private final String JSESSIONID_PREFIX = "JSESSIONID=";
	private final String SET_COOKIE = "set-cookie";

	@GET
	@Path("/auth/{context}")
	@Produces({ "text/html" })
	public String auth(@Context HttpServletRequest request, @PathParam("context") String context) {
		HttpSession session = ((HttpServletRequest) request).getSession();
		String IDPSessionID = getIDPSessionID(session);
		String html = "";
		
		try{
			 // Request to (SP) (context parameter)
			 String SPUrl = getUrl(request) + addDash(context);
			 Request req = Request.Get(SPUrl);
			 
			 html = req.execute().handleResponse(new ResponseHandler<String>() {
				@Override            
				public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
					headers = httpResponse.getAllHeaders();
					return IOUtils.getStringFromInputStream(httpResponse.getEntity().getContent());
				}
			});

			// Check if auth is need and was processed by picketlink 
			if (html.contains("HTTP Post Binding (Request)")){
				String setCookie = extractCookieFromHeader(getHeader(SET_COOKIE));
				String SAMLRequestValue = getAttributeValueFromHtml(html, SAMLRequest);
				String idpURL = getAttributeActionFromHtml(html);

				// Request to IDP with SAMLRequest attribute using IDP Session ID
				html = Request.Post(idpURL).addHeader(COOKIE, JSESSIONID_PREFIX+IDPSessionID).
						bodyForm(Form.form().add(SAMLRequest, SAMLRequestValue).build()).
						execute().returnContent().toString();
				
				String SAMLResponseValue = getAttributeValueFromHtml(html, SAMLResponse);
				
				// Request to SP with SAMLResponse parameter using JSESSIONID retrieved by the first request to SP
				// This POST return a 302 HTTP STATUS
				Request.Post(SPUrl).addHeader(COOKIE, JSESSIONID_PREFIX+setCookie).
						bodyForm(Form.form().add(SAMLResponse, SAMLResponseValue).build()).
						execute().discardContent();
				
				// Finally GET the SP content
				html = Request.Get(SPUrl).addHeader(COOKIE, JSESSIONID_PREFIX+setCookie).execute().returnContent().toString();
				if (html.contains("HTTP Post Binding (Request)")){
					log.info("Alguma coisa falhou na autenticação!: "+idpURL);
				}
			}else{
				log.info("Content requested without auth process!");
			}
		}catch(IOException io){
			io.printStackTrace();
		}

		return html;
	}
	
	private String addDash(String context) {
		if (context != null && !context.endsWith("/")){
			context += "/";
		}
		
		return context;
	}
}

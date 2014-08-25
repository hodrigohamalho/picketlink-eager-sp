package org.picketlink.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class PicketlinkUtil implements Filter {

	private Header[] headers;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession();
		String IDPSessionID = getIDPSessionID(session);

		// Request pro destino (SP)
		String html = Request.Get("http://localhost:8080/sales-post/").addHeader("cookie", "JSESSIONID="+IDPSessionID).execute().handleResponse(new ResponseHandler<String>() {
			@Override            
			public String handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
				headers = httpResponse.getAllHeaders();

				return IOUtils.getStringFromInputStream(httpResponse.getEntity().getContent());
			}
		});

		if (html.contains("HTTP Post Binding (Request)")){
			String SAMLRequest = getAttributeValueFromHtml(html, "SAMLRequest");
			String idpURL = getAttributeActionFromHtml(html);

			// Request pro IDP com o SAMLRequest
			Request req = Request.Post(idpURL).bodyForm(Form.form().add("SAMLRequest", SAMLRequest).build());
			req.addHeader("cookie", "JSESSIONID="+IDPSessionID);

			html = req.execute().handleResponse(new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					headers = response.getAllHeaders();

					return IOUtils.getStringFromInputStream(response.getEntity().getContent());
				}

			});

			String SAMLResponse = getAttributeValueFromHtml(html, "SAMLResponse");
			String spURL = getAttributeActionFromHtml(html);

			// Request pro SP com o SAMLResponse
			Request reqSP = Request.Post(spURL).bodyForm(Form.form().add("SAMLResponse", SAMLResponse).build());
			req.addHeader("cookie", "JSESSIONID="+IDPSessionID);

			html = reqSP.execute().handleResponse(new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					headers = response.getAllHeaders();

					return IOUtils.getStringFromInputStream(response.getEntity().getContent());
				}

			});
		}
		
		System.out.println("HTML: "+html);
		filterChain.doFilter(request, response);
	}


	@SuppressWarnings("unchecked")
	private String getIDPSessionID(HttpSession session){
		Map<String, Object> map = (Map<String, Object>) session.getAttribute("SESSION_ATTRIBUTE_MAP");
		return (String) ((List<Object>) map.get("sessionID")).get(0);
	}
	//    private String httpPost(String url, List <NameValuePair> postParams) throws IOException {
	//        String html;
	//
	//        CloseableHttpClient httpclient = HttpClients.createDefault();
	//        HttpPost httpPost = new HttpPost(url);
	//
	//        httpPost.setEntity(new UrlEncodedFormEntity(postParams));
	//        CloseableHttpResponse response2 = httpclient.execute(httpPost);
	//
	//        try {
	//            HttpEntity entity2 = response2.getEntity();
	//            html = IOUtils.getStringFromInputStream(entity2.getContent());
	//            EntityUtils.consume(entity2);
	//        } finally {
	//            response2.close();
	//        }
	//
	//        return html;
	//    }
	//
	//    private String httpGet(String url) throws IOException {
	//        String html;
	//
	//        CloseableHttpClient httpclient = HttpClients.createDefault();
	//        HttpGet httpGet = new HttpGet(url);
	//        CloseableHttpResponse response1 = httpclient.execute(httpGet);
	//
	//        try {
	//            HttpEntity entity1 = response1.getEntity();
	//            html = IOUtils.getStringFromInputStream(entity1.getContent());
	//            EntityUtils.consume(entity1);
	//        } finally {
	//            response1.close();
	//        }
	//
	//        return html;
	//    }
	//
	private String getAttributeValueFromHtml(String htmlContent, String attribute){
		String value = "";

		Document doc = Jsoup.parse(htmlContent);
		// Get SAMLRequest value
		for (Element el : doc.select("input")){
			if (el.attr("name").equals(attribute)){
				value = el.attr("value");
			}
		}

		return value;
	}

	private String getAttributeActionFromHtml(String htmlContent){
		Document doc = Jsoup.parse(htmlContent);
		return doc.select("form").attr("action");
	}

	private String getUrl(ServletRequest request){
		return request.getScheme() +"://"+ request.getServerName() + ":"+ request.getServerPort() + "/";
	}

	@Override
	public void destroy() {}
}

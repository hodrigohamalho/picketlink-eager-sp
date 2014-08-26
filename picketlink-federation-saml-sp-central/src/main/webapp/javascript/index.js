/**
 * Created by hodrigohamalho@gmail.com on 24/07/14.
 */

$( document ).ready(function() {
	
	$("#button-java").click(function(e){
		e.preventDefault();
		var url = $(this).parent().attr("href");
	
		$.ajax({
	        url: url,
	        type: "GET"
	    }).done(function(result) {
	    	$("#another-sp-content").html(result);
	    })
	});
	
	$("#button-ajax").click(function(){
	    // SEND GET Request to SP
	    $.ajax({
	        url: "/sales-post/",
	        type: "GET"
	    }).done(function(result) {
	    	
	    	if (result.indexOf("SAMLRequest") == -1){
	    		$("#another-sp-content").html(result);
	    		return;
	    	}
	    	
	        var form = $(result)[1];
	        var action = $(form).attr("action");
	        var samlRequestName = $(form).find("input").attr("name");
	        var samlRequestValue = $(form).find("input").attr("value");
	        var samlJson = {};
	        samlJson[samlRequestName] = samlRequestValue;
	        
	        // SEND POST request to IDP with SAMLRequest param
	        $.ajax({
	            url: action,
	            type: "POST",
	            data: samlJson
	        }).done(function(result) {
	            var form = $(result)[1];
	            var action = $(form).attr("action");
	
	            var samlResponseName = $(form).find("input").attr("name");
	            var samlResponseValue = $(form).find("input").attr("value");
	            var jsonData = {};
	            jsonData[samlResponseName] = samlResponseValue;
	
	            // SEND POST request to sales-post with SAMLResponse param
	            $.ajax({
	                url: action,
	                type: "POST",
	                data: jsonData
	            }).done(function(result) {
	                $("#another-sp-content").html(result);
	            });
	        });
	    });
	});
	
});
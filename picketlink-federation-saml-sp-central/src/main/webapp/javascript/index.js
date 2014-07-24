/**
 * Created by hodrigohamalho@gmail.com on 24/07/14.
 */
$( document ).ready(function() {
    // SEND GET Request to SP
    $.ajax({
        url: "/sales-post",
        type: "GET"
    }).done(function(result) {
        var form = $(result)[1];
        var action = $(form).attr("action");
        var paramName = $(form).find("input").attr("name");
        var paramValue = $(form).find("input").attr("value");
        var jsonData = {};
        jsonData[paramName] = paramValue;

        // SEND POST request to IDP with SAMLRequest param
        $.ajax({
            url: action,
            type: "POST",
            data: jsonData
        }).done(function(result) {
            var form = $(result)[1];
            var action = $(form).attr("action");
            var paramName = $(form).find("input").attr("name");
            var paramValue = $(form).find("input").attr("value");
            var jsonData = {};
            jsonData[paramName] = paramValue;

            // SEND POST request to sales-post with SAMLResponse param
            $.ajax({
                url: action,
                type: "POST",
                data: jsonData
            }).done(function(result) {
                var form = $(result)[1];
                $("#another-sp-content").text(result).html();
            });
        });
    });
});
Picketlink SP Communication
==========================

## Problem
When one SP make a GET to a secured content on another SP, it receives the following html:
```
```

## Objective
This project aims to explain possible solutions to make requests between Service Providers using IDP basic and SP Post from [picketlink quickstarts](https://github.com/jboss-developer/jboss-picketlink-quickstarts).  
On this project are showed to ways to do that:
* Client Side (trought ajax calls)
* Service Side

## Why?  
Some applications have pages that works like portal. One page, rendering others apps on the same domain via AJAX.

## Explain
When one SP makes a GET to a secured resource on other SP, picketlink proccess this request and send this request to auth proccess.  
To solve it is necessary make a background authentication. So on IDP is necessary to put the handler SAML2AttributeHandler with a custom AttributeManager to set a idp's session id on session that can be retrieved by the SP. With the IDP session id, the SP can make a manual authentication.





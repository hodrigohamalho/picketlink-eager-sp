<html>
<head>
    <script src="<%=request.getContextPath()%>/javascript/jquery-1.11.1.js"></script>
    <script src="<%=request.getContextPath()%>/javascript/index.js"></script>
</head>
<body>
<div align="center">
    <h1>Central</h1>
    <hr/>
    <br/>
    
    <button id="button-ajax" value="Get sales-post AJAX">Get sales-post AJAX</button>
    <a href="<%=request.getContextPath()%>/rest/auth/sales-post">
    	<button id="button-java" value="Get sales-post Java">Get sales-post Java</button>
    </a>
    <div id="another-sp-content"></div>
</div>
</body>
</html>

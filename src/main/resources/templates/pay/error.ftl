<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<meta http-equiv="Cache-Control" content="no-cache" />
		<script src="${rc.contextPath}/static/plugins/jquery/jquery-2.1.1.min.js" ></script>
	</head>
	<body>
	</body>
</html>
<style>
</style>
<script type="text/javascript">
   	var errorCode = '${retData.code!''}';
	var errorMsg = '${retData.msg!''}';
	if(errorCode == 404){
		var div = $("<div></div>");
		div.css("text-align","center");
		div.append("<img src='${rc.contextPath}/static/img/404.png'/>");
		$("body").append(div);
	}else{
		var h1 = $("<h3></h3>");
		var div = $("<div></div>");
		h1.css("text-align","center");
		h1.css("margin-top","10em");
		div.css("text-align","center");
		h1.text("错误码："+errorCode);
		div.text("错误信息："+errorMsg);
		$("body").append(h1);
		$("body").append(div);
	}
	
</script>


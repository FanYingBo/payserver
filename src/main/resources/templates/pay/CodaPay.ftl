<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<meta http-equiv="Cache-Control" content="no-cache" />
		<script src="${rc.contextPath}/static/plugins/jquery/jquery-2.1.1.min.js" ></script>
	</head>
	<body>
		<#if retData.data??>
			<input type="hidden" name="txnId" value="${retData.data.txnId!""}"/>
			<input type="hidden" name="url" value="${retData.data.url!""}"/>
			<input type="hidden" name="type" value="${retData.data.type!""}"/>
			<input type="hidden" name="browser_type" value="${retData.data.browser_type!""}"/>
		</#if>
	</body>
</html>
<script>
	$(function(){
		var txnId = $("[name=txnId]").val();
		var url = $("[name=url]").val();
		if(txnId == null || txnId == ''){
			var errorCode = '${retData.code!''}';
			var errorMsg = '${retData.msg!''}';
			var h1 = $("<h3></h3>");
			var div = $("<div></div>");
			h1.css("text-align","center");
			h1.css("margin-top","10em");
			div.css("text-align","center");
			h1.text("错误码："+errorCode);
			div.text("错误信息："+errorMsg);
			$("body").append(h1);
			$("body").append(div);
			return;
		}
		var type = $("[name=type]").val();
		var browser_type = $("[name=browser_type]").val();
		url+=("?type="+type+"&txn_id="+txnId+"&browser_type="+browser_type);
		location.href=url;
	});
</script>
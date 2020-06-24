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
   	function closeWebView(){
       	window.webview.jsCmd(6,0,"");
   	}
	$(window).load(function(){
		closeWebView();
	});
</script>


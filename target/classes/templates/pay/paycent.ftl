<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Web Page Payment</title> 

		<!-- Bootstrap/JQuery/Paycent meta/css/js -->
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta name="viewport" content="width=device-width, initial-scale=1">

		<!-- Load related JS/CSS files， "PaycentServer" need to be replaced by the paycent server URL -->
		<link href="${retData.data.resourcePath!''}statics/css/bootstrap-3.3.7.min.css" rel="stylesheet">
		<script type="text/javascript" src="${retData.data.resourcePath!''}statics/js/jquery-1.12.4.min.js"></script>
		<script type="text/javascript" src="${retData.data.resourcePath!''}statics/js/bootstrap-3.3.7.min.js"></script>
		<script type="text/javascript" src="${retData.data.resourcePath!''}statics/js/jquery.i18n.properties.min.js"></script>
		<link href="${retData.data.resourcePath!''}statics/css/paycent.css" type="text/css" rel="stylesheet"/>
		<script type="text/javascript" src="${retData.data.resourcePath!''}statics/js/paycent.min.js"></script>
	</head>
	<body>
		<div>
			<!-- Parameters for the JS SDK -->
			<input type='hidden' name='merchantNo' id='merchantNo' value='${retData.data.merchantNo!''}'>   <!-- Need to be replaced by the merchant number -->
			<input type='hidden' name='orderNo' id='orderNo' value='${retData.data.orderNo!''}'>            <!-- Need to be replaced by the order number -->
			<input type='hidden' name='amount' id='amount' value='${retData.data.amount?c!}'>
			<input type='hidden' name='cur' id='cur' value=''>
			<input type='hidden' name='country' id='country' value='${retData.data.country!''}'>
			<input type='hidden' name='productName' id='productName' value='${retData.data.productName!''}'>
			<!-- 商户自定义参数，由支付通知结果带回。商户可以用它区分不同的App等 -->
			<input type='hidden' name='mp' id='mp' value=''> 
			<input type='hidden' name='returnUrl' id='returnUrl' value='${retData.data.returnUrl!''}'>    <!-- Need to be replaced by the merchant return url -->
			<input type='hidden' name='notifyUrl' id='notifyUrl' value='${retData.data.notifyUrl!''}'>    <!-- Need to be replaced by the merchant notify url -->
			<input type='hidden' name='frpCode' id='frpCode' value='${retData.data.frpCode!''}'>
			<input type='hidden' name='orderPeriod' id='orderPeriod'  value=''>
			<input type='hidden' name='payerLoginName' id='payerLoginName' value=''>
			<input type='hidden' name='lang' id='lang' value='${retData.data.lang!''}'>
			<input type='hidden' name='initPaymentHmac' id='initPaymentHmac' value='${retData.data.initPaymentHmac!''}'>            <!-- Need to be replaced by the initPaymentHmac -->
			<input type='hidden' name='executePaymentHmac' id='executePaymentHmac' value='${retData.data.executePaymentHmac!''}'>          <!-- Need to be replaced by the executePaymentHmac -->
			<input type='hidden' name='inqueryPaymentHmac' id='inqueryPaymentHmac' value='${retData.data.inqueryPaymentHmac!''}'>    <!-- Need to be replaced by the inqueryPaymentHmac -->
			<input type='hidden' name='resourcePath' id='resourcePath' value='${retData.data.resourcePath!''}'>                                    <!-- "PaycentServer" need to be replaced by the paycent server URL -->
			<input type='hidden' name='initPayUrl' id='initPayUrl' value='${retData.data.initPayUrl!''}'>               <!-- "PaycentServer" need to be replaced by the paycent server URL -->
			<input type='hidden' name='executePayUrl' id='executePayUrl' value='${retData.data.executePayUrl!''}'>      <!-- "PaycentServer" need to be replaced by the paycent server URL -->
			<input type='hidden' name='orderQueryUrl' id='orderQueryUrl' value='${retData.data.orderQueryUrl!''}'>   <!-- "PaycentServer" need to be replaced by the paycent server URL -->
		</div>
	</body>
</html>
<script>
	$(document).ready(function(){
		initPayment();
	});
</script>

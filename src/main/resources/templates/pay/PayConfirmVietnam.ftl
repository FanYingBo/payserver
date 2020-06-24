<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<meta http-equiv="Cache-Control" content="no-cache" />
		<title>测试页面</title>
		<link rel="stylesheet" href="${rc.contextPath}/static/plugins/bootstrap/css/bootstrap.min.css">
		<script src="${rc.contextPath}/static/plugins/jquery/jquery-2.1.1.min.js" ></script>
		<script src="${rc.contextPath}/static/plugins/bootstrap/js/bootstrap.min.js"></script>
	</head>
	<body>
		<div class="form-div">
			<div class="form-title">
				<h5>支付测试</h5>
			</div>
			<form id="data-form" class="form-data" action="">
				<input type="hidden" name="platform" value="1"/>
				<input type="hidden" name="channel" value="1"/>
				<table>
					<tbody>
						<tr>
							<td>
								玩家ID：
							</td>
							<td>
								<input class="form-control" name="userId" type="text" value="1249"/>
							</td>
						</tr>
						<tr>
							<td>
								支付类型：
							</td>
							<td>
								<select name="payType" class="form-control">
									<option value="1">IOS支付</option>
									<option value="2">google支付</option>
									<option value="3">越南点卡</option>
									<option value="4">CodaPay-缅甸点卡</option>
									<option value="5">CodaPay-缅甸短信支付</option>
									<option value="6">CodaPay-缅甸电子钱包</option>
									<option value="7">华为支付-国际</option>
									<option value="8">Paycent</option>
									<option value="9">华为支付-国内</option>
									<option value="10">Upay越南网银</option>
									<option value="11">CodaPay-柬埔寨短信</option>
									<option value="12">CodaPay-柬埔寨电子钱包</option>
									<option value="13">CodaPay-泰国短信</option>
									<option value="14">CodaPay-泰国TrueMoneyCashCard</option>
									<option value="15">CodaPay-泰国TrueMoneyWallet</option>
									<option value="16">CodaPay-泰国7-ElevenTH</option>
									<option value="17">CodaPay-泰国零售</option>
									<option value="18">CodaPay-泰国RabbitLine</option>
								</select>
							</td>
						</tr>
						<tr id="ios-receipt">
							<td>
								receipt:
							</td>
							<td>
								<textarea name="receipt" class="form-control">MIITtAYJKoZIhvcNAQcCoIITpTCCE6ECAQExCzAJBgUrDgMCGgUAMIIDVQYJKoZIhvcNAQcBoIIDRgSCA0IxggM+MAoCAQgCAQEEAhYAMAoCARQCAQEEAgwAMAsCAQECAQEEAwIBADALAgELAgEBBAMCAQAwCwIBDwIBAQQDAgEAMAsCARACAQEEAwIBADALAgEZAgEBBAMCAQMwDAIBCgIBAQQEFgI0KzAMAgEOAgEBBAQCAgCOMA0CAQ0CAQEEBQIDAdZRMA0CARMCAQEEBQwDMS4wMA4CAQkCAQEEBgIEUDI1MzAPAgEDAgEBBAcMBTIuMC4zMBgCAQQCAQIEENr0yIKnWEzJ1ORztBjf5pQwGwIBAAIBAQQTDBFQcm9kdWN0aW9uU2FuZGJveDAbAgECAgEBBBMMEWNvbS55eS5samJ5Mi50aGFpMBwCAQUCAQEEFJ742a20l8e\/8\/zlbVRZTFvlAtFHMB4CAQwCAQEEFhYUMjAxOS0wOS0yNlQwNTo1NTo1M1owHgIBEgIBAQQWFhQyMDEzLTA4LTAxVDA3OjAwOjAwWjAzAgEGAgEBBCu3YChND2QYTWTQZhILDwHRG4e6z77VqsD8oHWupLYJg\/wSWnUTe58CIb4TMEICAQcCAQEEOsQRBZ9p7ahZpEVbGm2mdSY1nlcojnej2DhAP\/3F6iXAOdO452G2xJ+iDvxx8+rrwXPgAnc85aF\/jFkwggFbAgERAgEBBIIBUTGCAU0wCwICBqwCAQEEAhYAMAsCAgatAgEBBAIMADALAgIGsAIBAQQCFgAwCwICBrICAQEEAgwAMAsCAgazAgEBBAIMADALAgIGtAIBAQQCDAAwCwICBrUCAQEEAgwAMAsCAga2AgEBBAIMADAMAgIGpQIBAQQDAgEBMAwCAgarAgEBBAMCAQEwDAICBq4CAQEEAwIBADAMAgIGrwIBAQQDAgEAMAwCAgaxAgEBBAMCAQAwGwICBqcCAQEEEgwQMTAwMDAwMDU3MjU5MDY3NTAbAgIGqQIBAQQSDBAxMDAwMDAwNTcyNTkwNjc1MB8CAgaoAgEBBBYWFDIwMTktMDktMjZUMDU6NTU6NTNaMB8CAgaqAgEBBBYWFDIwMTktMDktMjZUMDU6NTU6NTNaMCECAgamAgEBBBgMFmNvbS55dWV5b3UueGJ5Lm5ld2dpZnSggg5lMIIFfDCCBGSgAwIBAgIIDutXh+eeCY0wDQYJKoZIhvcNAQEFBQAwgZYxCzAJBgNVBAYTAlVTMRMwEQYDVQQKDApBcHBsZSBJbmMuMSwwKgYDVQQLDCNBcHBsZSBXb3JsZHdpZGUgRGV2ZWxvcGVyIFJlbGF0aW9uczFEMEIGA1UEAww7QXBwbGUgV29ybGR3aWRlIERldmVsb3BlciBSZWxhdGlvbnMgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkwHhcNMTUxMTEzMDIxNTA5WhcNMjMwMjA3MjE0ODQ3WjCBiTE3MDUGA1UEAwwuTWFjIEFwcCBTdG9yZSBhbmQgaVR1bmVzIFN0b3JlIFJlY2VpcHQgU2lnbmluZzEsMCoGA1UECwwjQXBwbGUgV29ybGR3aWRlIERldmVsb3BlciBSZWxhdGlvbnMxEzARBgNVBAoMCkFwcGxlIEluYy4xCzAJBgNVBAYTAlVTMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApc+B\/SWigVvWh+0j2jMcjuIjwKXEJss9xp\/sSg1Vhv+kAteXyjlUbX1\/slQYncQsUnGOZHuCzom6SdYI5bSIcc8\/W0YuxsQduAOpWKIEPiF41du30I4SjYNMWypoN5PC8r0exNKhDEpYUqsS4+3dH5gVkDUtwswSyo1IgfdYeFRr6IwxNh9KBgxHVPM3kLiykol9X6SFSuHAnOC6pLuCl2P0K5PB\/T5vysH1PKmPUhrAJQp2Dt7+mf7\/wmv1W16sc1FJCFaJzEOQzI6BAtCgl7ZcsaFpaYeQEGgmJjm4HRBzsApdxXPQ33Y72C3ZiB7j7AfP4o7Q0\/omVYHv4gNJIwIDAQABo4IB1zCCAdMwPwYIKwYBBQUHAQEEMzAxMC8GCCsGAQUFBzABhiNodHRwOi8vb2NzcC5hcHBsZS5jb20vb2NzcDAzLXd3ZHIwNDAdBgNVHQ4EFgQUkaSc\/MR2t5+givRN9Y82Xe0rBIUwDAYDVR0TAQH\/BAIwADAfBgNVHSMEGDAWgBSIJxcJqbYYYIvs67r2R1nFUlSjtzCCAR4GA1UdIASCARUwggERMIIBDQYKKoZIhvdjZAUGATCB\/jCBwwYIKwYBBQUHAgIwgbYMgbNSZWxpYW5jZSBvbiB0aGlzIGNlcnRpZmljYXRlIGJ5IGFueSBwYXJ0eSBhc3N1bWVzIGFjY2VwdGFuY2Ugb2YgdGhlIHRoZW4gYXBwbGljYWJsZSBzdGFuZGFyZCB0ZXJtcyBhbmQgY29uZGl0aW9ucyBvZiB1c2UsIGNlcnRpZmljYXRlIHBvbGljeSBhbmQgY2VydGlmaWNhdGlvbiBwcmFjdGljZSBzdGF0ZW1lbnRzLjA2BggrBgEFBQcCARYqaHR0cDovL3d3dy5hcHBsZS5jb20vY2VydGlmaWNhdGVhdXRob3JpdHkvMA4GA1UdDwEB\/wQEAwIHgDAQBgoqhkiG92NkBgsBBAIFADANBgkqhkiG9w0BAQUFAAOCAQEADaYb0y4941srB25ClmzT6IxDMIJf4FzRjb69D70a\/CWS24yFw4BZ3+Pi1y4FFKwN27a4\/vw1LnzLrRdrjn8f5He5sWeVtBNephmGdvhaIJXnY4wPc\/zo7cYfrpn4ZUhcoOAoOsAQNy25oAQ5H3O5yAX98t5\/GioqbisB\/KAgXNnrfSemM\/j1mOC+RNuxTGf8bgpPyeIGqNKX86eOa1GiWoR1ZdEWBGLjwV\/1CKnPaNmSAMnBjLP4jQBkulhgwHyvj3XKablbKtYdaG6YQvVMpzcZm8w7HHoZQ\/Ojbb9IYAYMNpIr7N4YtRHaLSPQjvygaZwXG56AezlHRTBhL8cTqDCCBCIwggMKoAMCAQICCAHevMQ5baAQMA0GCSqGSIb3DQEBBQUAMGIxCzAJBgNVBAYTAlVTMRMwEQYDVQQKEwpBcHBsZSBJbmMuMSYwJAYDVQQLEx1BcHBsZSBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTEWMBQGA1UEAxMNQXBwbGUgUm9vdCBDQTAeFw0xMzAyMDcyMTQ4NDdaFw0yMzAyMDcyMTQ4NDdaMIGWMQswCQYDVQQGEwJVUzETMBEGA1UECgwKQXBwbGUgSW5jLjEsMCoGA1UECwwjQXBwbGUgV29ybGR3aWRlIERldmVsb3BlciBSZWxhdGlvbnMxRDBCBgNVBAMMO0FwcGxlIFdvcmxkd2lkZSBEZXZlbG9wZXIgUmVsYXRpb25zIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyjhUpstWqsgkOUjpjO7sX7h\/JpG8NFN6znxjgGF3ZF6lByO2Of5QLRVWWHAtfsRuwUqFPi\/w3oQaoVfJr3sY\/2r6FRJJFQgZrKrbKjLtlmNoUhU9jIrsv2sYleADrAF9lwVnzg6FlTdq7Qm2rmfNUWSfxlzRvFduZzWAdjakh4FuOI\/YKxVOeyXYWr9Og8GN0pPVGnG1YJydM05V+RJYDIa4Fg3B5XdFjVBIuist5JSF4ejEncZopbCj\/Gd+cLoCWUt3QpE5ufXN4UzvwDtIjKblIV39amq7pxY1YNLmrfNGKcnow4vpecBqYWcVsvD95Wi8Yl9uz5nd7xtj\/pJlqwIDAQABo4GmMIGjMB0GA1UdDgQWBBSIJxcJqbYYYIvs67r2R1nFUlSjtzAPBgNVHRMBAf8EBTADAQH\/MB8GA1UdIwQYMBaAFCvQaUeUdgn+9GuNLkCm90dNfwheMC4GA1UdHwQnMCUwI6AhoB+GHWh0dHA6Ly9jcmwuYXBwbGUuY29tL3Jvb3QuY3JsMA4GA1UdDwEB\/wQEAwIBhjAQBgoqhkiG92NkBgIBBAIFADANBgkqhkiG9w0BAQUFAAOCAQEAT8\/vWb4s9bJsL4\/uE4cy6AU1qG6LfclpDLnZF7x3LNRn4v2abTpZXN+DAb2yriphcrGvzcNFMI+jgw3OHUe08ZOKo3SbpMOYcoc7Pq9FC5JUuTK7kBhTawpOELbZHVBsIYAKiU5XjGtbPD2m\/d73DSMdC0omhz+6kZJMpBkSGW1X9XpYh3toiuSGjErr4kkUqqXdVQCprrtLMK7hoLG8KYDmCXflvjSiAcp\/3OIK5ju4u+y6YpXzBWNBgs0POx1MlaTbq\/nJlelP5E3nJpmB6bz5tCnSAXpm4S6M9iGKxfh44YGuv9OQnamt86\/9OBqWZzAcUaVc7HGKgrRsDwwVHzCCBLswggOjoAMCAQICAQIwDQYJKoZIhvcNAQEFBQAwYjELMAkGA1UEBhMCVVMxEzARBgNVBAoTCkFwcGxlIEluYy4xJjAkBgNVBAsTHUFwcGxlIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRYwFAYDVQQDEw1BcHBsZSBSb290IENBMB4XDTA2MDQyNTIxNDAzNloXDTM1MDIwOTIxNDAzNlowYjELMAkGA1UEBhMCVVMxEzARBgNVBAoTCkFwcGxlIEluYy4xJjAkBgNVBAsTHUFwcGxlIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MRYwFAYDVQQDEw1BcHBsZSBSb290IENBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5JGpCR+R2x5HUOsF7V55hC3rNqJXTFXsixmJ3vlLbPUHqyIwAugYPvhQCdN\/QaiY+dHKZpwkaxHQo7vkGyrDH5WeegykR4tb1BY3M8vED03OFGnRyRly9V0O1X9fm\/IlA7pVj01dDfFkNSMVSxVZHbOU9\/acns9QusFYUGePCLQg98usLCBvcLY\/ATCMt0PPD5098ytJKBrI\/s61uQ7ZXhzWyz21Oq30Dw4AkguxIRYudNU8DdtiFqujcZJHU1XBry9Bs\/j743DN5qNMRX4fTGtQlkGJxHRiCxCDQYczioGxMFjsWgQyjGizjx3eZXP\/Z15lvEnYdp8zFGWhd5TJLQIDAQABo4IBejCCAXYwDgYDVR0PAQH\/BAQDAgEGMA8GA1UdEwEB\/wQFMAMBAf8wHQYDVR0OBBYEFCvQaUeUdgn+9GuNLkCm90dNfwheMB8GA1UdIwQYMBaAFCvQaUeUdgn+9GuNLkCm90dNfwheMIIBEQYDVR0gBIIBCDCCAQQwggEABgkqhkiG92NkBQEwgfIwKgYIKwYBBQUHAgEWHmh0dHBzOi8vd3d3LmFwcGxlLmNvbS9hcHBsZWNhLzCBwwYIKwYBBQUHAgIwgbYagbNSZWxpYW5jZSBvbiB0aGlzIGNlcnRpZmljYXRlIGJ5IGFueSBwYXJ0eSBhc3N1bWVzIGFjY2VwdGFuY2Ugb2YgdGhlIHRoZW4gYXBwbGljYWJsZSBzdGFuZGFyZCB0ZXJtcyBhbmQgY29uZGl0aW9ucyBvZiB1c2UsIGNlcnRpZmljYXRlIHBvbGljeSBhbmQgY2VydGlmaWNhdGlvbiBwcmFjdGljZSBzdGF0ZW1lbnRzLjANBgkqhkiG9w0BAQUFAAOCAQEAXDaZTC14t+2Mm9zzd5vydtJ3ME\/BH4WDhRuZPUc38qmbQI4s1LGQEti+9HOb7tJkD8t5TzTYoj75eP9ryAfsfTmDi1Mg0zjEsb+aTwpr\/yv8WacFCXwXQFYRHnTTt4sjO0ej1W8k4uvRt3DfD0XhJ8rxbXjt57UXF6jcfiI1yiXV2Q\/Wa9SiJCMR96Gsj3OBYMYbWwkvkrL4REjwYDieFfU9JmcgijNq9w2Cz97roy\/5U2pbZMBjM3f3OgcsVuvaDyEO2rpzGU+12TZ\/wYdV2aeZuTJC+9jVcZ5+oVK3G72TQiQSKscPHbZNnF5jyEuAF1CqitXa5PzQCQc3sHV1ITGCAcswggHHAgEBMIGjMIGWMQswCQYDVQQGEwJVUzETMBEGA1UECgwKQXBwbGUgSW5jLjEsMCoGA1UECwwjQXBwbGUgV29ybGR3aWRlIERldmVsb3BlciBSZWxhdGlvbnMxRDBCBgNVBAMMO0FwcGxlIFdvcmxkd2lkZSBEZXZlbG9wZXIgUmVsYXRpb25zIENlcnRpZmljYXRpb24gQXV0aG9yaXR5AggO61eH554JjTAJBgUrDgMCGgUAMA0GCSqGSIb3DQEBAQUABIIBADf4ddZPM0tDp53FM7ub8QAVRnXRunVvc4zPT3M+LkYtDKXu\/rNOt8J+EbkB1kj7\/5BduVtWlpbdz5QVGUS5zMzmOuRcesWVF5wR0h7TmHH8D8hvE9X5jm7O6HVcrHEeejHANQ9Nlx3rPhPOl3U4KHYImpUPkXv\/LUkNCRwgUJfAwvdErfxwsVVYvfF2cGDgPB5XnTSgkWxfvyNuLC0kGgN9aWtRD7nP\/QEDWZBzmz6UslFd8jFJfTXFP2Yn13E1QAEKlyMI61zFdZzi9R\/LPeM16KwqwOZc0O\/8SO52FzQQk7bKWnLb+oISL3xmST05\/tgYlKclF52ZnUS5qkD9I+Q=</textarea>
							</td>
						</tr>
						<tr id="vietnam-money">
							<td>金额:</td>
							<td>
								<select name="money" class="form-control">
									<option value="0">0</option>
									<option value="10000">10000</option>
									<option value="20000">20000</option>
									<option value="30000">30000</option>
									<option value="50000">50000</option>
									<option value="100000">100000</option>
									<option value="200000">200000</option>
									<option value="300000">300000</option>
									<option value="300000">300000</option>
									<option value="500000">500000</option>
									<option value="1000000">1000000</option>
								</select>
							</td>
						</tr>
						<tr id="vietnam-pointNames">
							<td>
								付费点：
							</td>
							<td>
								<select name="pointName" class="form-control">
									<#list payPoints as payPoint>
											<option>${payPoint.name!''}</option>
									</#list>
								</select>
							</td>
						</tr>
						<tr id="vietnam-cardNum">
							<td>卡   号：</td>
							<td>
								<input class="form-control" name="cardNum" type="text"/>
							</td>
						</tr>
						<tr id="vietnam-cardPin">
							<td>卡   密：</td>
							<td><input class="form-control" name="cardPin" type="text"/></td>
						</tr>
						<tr id="vietnam-provider">
							<td>提供商：</td>
							<td><input class="form-control" name="providerCode" type="text"/></td>
						</tr>
						<tr id="google-orderNum">
							<td>orderNum：</td>
							<td><input class="form-control" name="googleOrderNum" value="" type="text"/></td>
						</tr>
						<tr id="google-productId">
							<td>productId：</td>
							<td>
								<select name="productId" class="form-control">
									<#list payPoints as payPoint>
										<option>${payPoint.name!''}</option>
									</#list>
								</select>
							</td>
						</tr>
						<tr id="google-pakcageName">
							<td>pakcageName：</td>
							<td><input class="form-control" name="packageName" value="com.last.topfish" type="text"/></td>
						</tr>
						<tr id="google-orderId">
							<td>orderId：</td>
							<td><input class="form-control" name="orderId" value="GPA.3388-6491-6871-60276" type="text"/></td>
						</tr>
						<tr id="google-token">
							<td>purchaseToken：</td>
							<td><input class="form-control" name="token" type="text" value='knkeimmnnocjpgbmgdfnmnlg.AO-J1OxlNLKxFPuHksgeqolyLG6Fq_EfiQ6tcle2ISfxi1V-CPqEG_HptZpDnDKDGIodgkqRlNq1jT29qy5Gzyouxq3ItC-DYQ7IH_fGQF7sv96-AU87OKTJ_Np1CmzkMS0ZAji-RM9-'/></td>
						</tr>
						<tr id="coda-voucher-cardNum">
							<td>cardNum：</td>
							<td><input class="form-control" name="voucher-cardNum" value="" type="text"/></td>
						</tr>
						<tr id="coda-voucher-cardPin">
							<td>cardPin：</td>
							<td><input class="form-control" name="voucher-cardPin" value="" type="text"/></td>
						</tr>
						<tr id="coda-voucher-money">
							<td>金额:</td>
							<td>
								<select name="coda-money" class="form-control">
									<option value="500">500</option>
									<option value="1000">1000</option>
									<option value="2000">2000</option>
									<option value="3000">3000</option>
									<option value="5000">5000</option>
									<option value="10000">10000</option>
									<option value="50000">50000</option>
									<option value="100000">100000</option>
								</select>
							</td>
						</tr>
						<tr id="coda-sms-mno">
							<td>缅甸电信运营商:</td>
							<td>
								<select name="mnoId" class="form-control">
									<option value="101">Telenor Myanmar</option>
									<option value="102">Ooredoo</option>
									<option value="103">MPT</option>
									<option value="104">Mytel</option>
								</select>
							</td>
						</tr>
						<tr id="huawei-productId">
							<td>productId:</td>
							<td>
								<input class="form-control" name="huawei-productId" value="com.last.xby.1gold" type="text"/>
							</td>
						</tr>
						<tr id="huawei-purchaseToken">
							<td>purchaseTokey:</td>
							<td>
								<input class="form-control" name="huawei-purchaseToken" value="0000016f170aeaa437ee3fbadb4a195807144da10f5594db9aa17d33acb7ea4261f11db75c6cbb21.1.101408493" type="text"/>
							</td>
						</tr>
						<tr id="coda-sms-mno-cambodia">
							<td>柬埔寨电信运营商:</td>
							<td>
								<select name="mnoId-cambodia" class="form-control">
									<option value="121">Smart</option>
									<option value="122">Cellcard</option>
									<option value="123">Metfone</option>
								</select>
							</td>
						</tr>
						<tr id="coda-sms-mno-thailand">
							<td>泰国电信运营商:</td>
							<td>
								<select name="mnoId-thailand" class="form-control">
									<option value="70">DTAC</option>
									<option value="71">AIS</option>
									<option value="72">CAT</option>
									<option value="73">Truemove TH</option>
								</select>
							</td>
						</tr>
					</tbody>
				</table>
				<nobr>
					<input class="btn btn-success query" id="other" type="button" value="其他" />
					<input class="btn btn-success confirm" id="confirm" type="button" value="支付" />
					<input class="btn btn-success confirm" id="page-init" type="button" value="网页支付" />
				</nobr>
				</div>
			</form>
		</div>
		<!-- 遮罩层模态框（Modal） -->
		<div class="modal fade"  id="loading" tabindex="-1" role="dialog"  aria-hidden="true">	
		    	<div id="canvas" style="text-align:center;margin-top:20%">
		    		<img src="${rc.contextPath}/static/img/timg.gif"/>
				</div>
		</div>
		<style>
			body {
				font-size: 45.556% !important;
			}
			.form-div {
				width:100%;
			}
			.form-div .form-title{
				text-align:center;
				margin:0 30%;
				width:30%;
				height:5%;
			}
			.form-div .form-data{
				margin:0 20%;
				width:60%;
			}
			.form-data table{
				margin:0 10%;
				width:60%;
				border-collapse:separate; 
				border-spacing:10px 20px; 
			}
			.form-data table tbody tr{
				width:100%;
				border-collapse:separate; 
				border-spacing:10px 20px; 
			}
			.form-data table tbody div tr{
				width:100%;
				border-collapse:separate; 
				border-spacing:10px 20px; 
			}
			.form-data input{
				width:100%;
			}
			.form-data select{
				width:100%;
			}
			.form-data select option{
				width:100%;
			}
			.form-data .confirm {
				margin:0 0 0 10%;
				width:auto;
				font-size: 45.556% !important;
			}
			.form-data .query {
				margin:0 0 0 30%;
				width:auto;
				font-size: 45.556% !important;
			}
		</style>
		<script>
			function allHideExceptIOS(){
				$("#vietnam-pointNames").hide();
				$("#vietnam-cardNum").hide();
				$("#vietnam-cardPin").hide();
				$("#google-productId").hide();
				$("#google-pakcageName").hide();
				$("#google-token").hide();
				$("#vietnam-money").hide();
				$("#google-orderId").hide();
				$("#coda-voucher-cardNum").hide();
				$("#coda-voucher-cardPin").hide();
				$("#page-init").hide();
				$("#coda-sms-mno").hide();
				$("#vietnam-provider").hide();
				$("#coda-voucher-money").hide();
				$("#google-orderNum").hide();
				$("#huawei-productId").hide();
				$("#huawei-purchaseToken").hide();
				$("#coda-sms-mno-cambodia").hide();
				$("#coda-sms-mno-thailand").hide();
			}
			
			function iosShow(){
				$("#ios-receipt").show();
				$("#page-init").hide();
			}
			function iosHide(){
				$("#ios-receipt").hide();
			}
			
			function googleShow(){
				$("#google-productId").show();
				$("#google-pakcageName").show();
				$("#google-token").show();
				$("#google-orderId").show();
				$("#vietnam-pointNames").show();
				$("#page-init").hide();
			}
			function googleHide(){
				$("#google-productId").hide();
				$("#google-pakcageName").hide();
				$("#google-token").hide();
				$("#google-orderId").hide();
			}
			
			function vietnamShow(){
				$("#vietnam-pointNames").show();
				$("#vietnam-cardNum").show();
				$("#vietnam-cardPin").show();
				$("#vietnam-provider").show();
				$("#vietnam-money").show();
				$("#page-init").hide();
			}
			function vietnamHide(){
				$("#vietnam-provider").hide();
				$("#vietnam-cardNum").hide();
				$("#vietnam-cardPin").hide();
				$("#vietnam-money").hide();
			}
			function codaPayEasyPointsShow(){
				$("#coda-voucher-cardNum").show();
				$("#coda-voucher-cardPin").show();
				$("#coda-voucher-money").show();
				$("#vietnam-pointNames").show();
			}
			
			function codaPayEasyPointsHide(){
				$("#coda-voucher-cardNum").hide();
				$("#coda-voucher-cardPin").hide();
				$("#coda-voucher-money").hide();
			}
			function codaPaySMSShow(){
				$("#page-init").show();
				$("#coda-sms-mno").show();
				$("#vietnam-pointNames").show();
			}
			
			function codaPaySMSHide(){
				$("#coda-sms-mno").hide();
			}
			
			function codaPayCambodiaSMSShow(){
				$("#page-init").show();
				$("#coda-sms-mno-cambodia").show();
				$("#vietnam-pointNames").show();
			}
			
			function codaPayCambodiaSMSHide(){
				$("#coda-sms-mno-cambodia").hide();
			}
			function codaPayEWalletShow(){
				$("#vietnam-pointNames").show();
				$("#page-init").show();
				$("#vietnam-pointNames").show();
			}
			function codaPayEWalletHide(){
				
			}
			function codaPayWingShow(){
				$("#vietnam-pointNames").show();
				$("#page-init").show();
				$("#vietnam-pointNames").show();
			}
			function codaPayWingHide(){
				
			}
			function huaweiShow(){
				$("#huawei-productId").show();
				$("#huawei-purchaseToken").show();
			}
			function huaweiHide(){
				$("#huawei-productId").hide();
				$("#huawei-purchaseToken").hide();
			}
			
			function paycentShow(){
				$("#vietnam-pointNames").show();
				$("#coda-sms-mno").show();
				$("#page-init").show();
			}
			function paycentHide(){
				
			}
			function pointNamesShow(){
				$("#vietnam-pointNames").show();
			}
			function pointNamesHide(){
				$("#vietnam-pointNames").hide();
			}
			function pageInitHide(){
				$("#page-init").hide();
			}
			function vietnamEbankShow(){
				$("#vietnam-pointNames").show();
			}
			
			function pagePointShow(){
				$("#page-init").show();
				$("#vietnam-pointNames").show();
			}
		</script>
		<script>
		
		allHideExceptIOS();
		
		$("[name=payType]").change(function(){
			var payType = $(this).val();
			if(payType == 1){
				iosShow();
				googleHide();
				vietnamHide();
				codaPayEasyPointsHide();
				codaPaySMSHide();
				codaPayEWalletHide();
				huaweiHide();
				paycentHide();
				pointNamesHide();
				pageInitHide();
				codaPayCambodiaSMSHide();
			}else if(payType == 2){
				iosHide();
				googleShow();
				vietnamHide();
				codaPayEasyPointsHide();
				codaPaySMSHide();
				codaPayEWalletHide();
				huaweiHide();
				paycentHide();
				pageInitHide();
				codaPayCambodiaSMSHide();
			}else if(payType == 3){
				iosHide();
				googleHide();
				vietnamShow();
				codaPayEasyPointsHide();
				codaPaySMSHide();
				codaPayEWalletHide();
				huaweiHide();
				paycentHide();
				pageInitHide();
				codaPayCambodiaSMSHide();
			}else if(payType == 4){
				iosHide();
				googleHide();
				vietnamHide();
				codaPayEasyPointsShow();
				codaPaySMSHide();
				codaPayEWalletHide();
				huaweiHide();
				paycentHide();
				pageInitHide();
				codaPayCambodiaSMSHide();
			}else if(payType == 5){
				iosHide();
				googleHide();
				vietnamHide();
				codaPayEasyPointsHide();
				codaPaySMSShow();
				codaPayEWalletHide();
				huaweiHide();
				paycentHide();
				codaPayCambodiaSMSHide();
			}else if(payType == 6){
				iosHide();
				googleHide();
				vietnamHide();
				codaPayEasyPointsHide();
				codaPaySMSHide();
				codaPayEWalletShow();
				huaweiHide();
				paycentHide();
				codaPayCambodiaSMSHide();
			}else if(payType == 7 || payType == 9){
				iosHide();
				googleHide();
				vietnamHide();
				codaPayEasyPointsHide();
				codaPaySMSHide();
				codaPayEWalletHide();
				huaweiShow();
				paycentHide();
				pageInitHide();
				pointNamesShow();
				codaPayCambodiaSMSHide();
			}else if(payType == 8){
				iosHide();
				googleHide();
				vietnamHide();
				codaPayEasyPointsHide();
				codaPaySMSHide();
				codaPayEWalletHide();
				huaweiHide();
				paycentShow();
				codaPayCambodiaSMSHide();
			}else if(payType == 10){
				vietnamEbankShow();
				iosHide();
				googleHide();
				vietnamHide();
				codaPayEasyPointsHide();
				codaPaySMSHide();
				codaPayEWalletHide();
				huaweiHide();
				paycentHide();
				codaPayCambodiaSMSHide();
			}else if(payType == 11){
				iosHide();
				googleHide();
				vietnamHide();
				codaPayEasyPointsHide();
				codaPaySMSHide();
				codaPayEWalletHide();
				huaweiHide();
				paycentHide();
				codaPayCambodiaSMSShow();
			}else if(payType == 12){
				iosHide();
				googleHide();
				vietnamHide();
				codaPayEasyPointsHide();
				codaPaySMSHide();
				codaPayEWalletHide();
				huaweiHide();
				paycentHide();
				codaPayCambodiaSMSHide();
				codaPayWingShow();
			}else if(payType == 13 || payType == 14 ||payType == 15||payType == 16 ||payType == 17||payType == 18){
				iosHide();
				googleHide();
				vietnamHide();
				codaPayEasyPointsHide();
				codaPaySMSHide();
				codaPayEWalletHide();
				huaweiHide();
				paycentHide();
				codaPayCambodiaSMSHide();
				codaPayWingHide();
				pagePointShow();
			}
			
		});
			$("#confirm").click(function(){
				var data = {};
				var payType = $("[name=payType]").val();
				if(payType == 1){
					data.userId = $("[name=userId]").val();
					data.platform = $("[name=platform]").val();
					data.channel = $("[name=channel]").val();
					data.payType = payType;
					data.ip = "192.168.1.132";
					data.pointName = $("[name=productId]").val();
					data.purchaseInfo = $("[name=receipt]").val();
				}else if(payType == 2){
					data.userId = $("[name=userId]").val();
					data.platform = $("[name=platform]").val();
					data.channel = $("[name=channel]").val();
					data.payType = payType;
					data.pointName = $("[name=pointName]").val();
					data.ip = "192.168.1.132";
					var purchaseInfo = {};
					purchaseInfo.productId = $("[name=productId]").val();
					purchaseInfo.packageName = $("[name=packageName]").val();
					purchaseInfo.purchaseToken = $("[name=token]").val();
					purchaseInfo.orderId = $("[name=orderId]").val();
					data.purchaseInfo = JSON.stringify(purchaseInfo);				
				}else if(payType == 3 || payType == 10){
					data = $('#data-form').serializeArray();
				}else if(payType == 4){
					data.userId = $("[name=userId]").val();
					data.platform = $("[name=platform]").val();
					data.channel = $("[name=channel]").val();
					data.payType = payType;
					data.pointName = $("[name=pointName]").val();
					data.cardNum = $("[name=voucher-cardNum]").val();
					data.cardPin = $("[name=voucher-cardPin]").val();
					data.money = $("[name=coda-money]").val();
				}else if(payType == 7 || payType == 9){
					data.userId = $("[name=userId]").val();
					data.platform = $("[name=platform]").val();
					data.channel = $("[name=channel]").val();
					data.payType = payType;
					data.pointName = $("[name=pointName]").val();
					var purchaseInfo = {};
					purchaseInfo.productId = $("[name=huawei-productId]").val();
					purchaseInfo.purchaseToken = $("[name=huawei-purchaseToken]").val();
					data.purchaseInfo = JSON.stringify(purchaseInfo);
				}else{
					return;
				}
				var url ='${rc.contextPath}/PayManager/placeAnOrder';
				$.ajax({
					type:'POST',
					url:url,
					data:data,
					beforeSend:function(){
						$('#loading').modal('show');
					},
					complete:function(){
						$('#loading').modal('hide');
					},
					dataType:'json',
					async:false,
					success:function(result){
						alert("code: "+result.code+" msg: "+result.msg+" data: "+result.data);
						if((result.code == 0 || result.code == 9000) && payType == 10){
							location.href = result.data;
						}
					}				
				});
			});
			$("#other").click(function(){
				var payType = $("[name=payType]").val();
				if(payType != 2 && payType != 1 && payType != 7 && payType != 9){
					return;
				}
				var data = {};
				var url = "${rc.contextPath}/PayManager/redisPay";
				if(payType == 2){
					data.userId = $("[name=userId]").val();
					data.platform = $("[name=platform]").val();
					data.channel = $("[name=channel]").val();
					data.orderNum = $("[name=googleOrderNum]").val();
					data.payType = payType;
					data.pointName = $("[name=pointName]").val();
					data.ip = "192.168.1.132";
					var purchaseInfo = {};
					purchaseInfo.productId = $("[name=productId]").val();
					purchaseInfo.packageName = $("[name=packageName]").val();
					purchaseInfo.purchaseToken = $("[name=token]").val();
					purchaseInfo.orderId = $("[name=orderId]").val();
					data.purchaseInfo = JSON.stringify(purchaseInfo);				
				}else if(payType == 1){
					data.userId = $("[name=userId]").val();
					data.platform = $("[name=platform]").val();
					data.channel = $("[name=channel]").val();
					data.payType = payType;
					data.pointName = $("[name=productId]").val();
					data.ip = "192.168.1.132";
					data.purchaseInfo = $("[name=receipt]").val();
				}else if(payType == 7 || payType == 9){
					data.userId = $("[name=userId]").val();
					data.platform = $("[name=platform]").val();
					data.channel = $("[name=channel]").val();
					data.payType = payType;
					data.pointName = $("[name=pointName]").val();
					data.ip = "192.168.1.132";
					var purchaseInfo = {};
					purchaseInfo.productId = $("[name=huawei-productId]").val();
					purchaseInfo.purchaseToken = $("[name=huawei-purchaseToken]").val();
					data.purchaseInfo = JSON.stringify(purchaseInfo);
				}
				$.ajax({
					type:'POST',
					url:url,
					data:data,
					beforeSend:function(){
						$('#loading').modal('show');
					},
					complete:function(){
						$('#loading').modal('hide');
					},
					dataType:'json',
					async:false,
					success:function(result){
						alert("code: "+result.code+" msg: "+result.msg+" data: "+result.data);
					}				
				});
			});
			$("#query").click(function(){
				$("#loading").modal('show');
				$.ajax({
					type:'POST',
					url:'${rc.contextPath}/PayManager/queryRequestInfo',
					data:$('#data-form').serializeArray(),
					dataType:'json',
					async:false,
					success:function(result){
						// alert("code: "+result.code+" msg: "+result.msg+" data: "+result.data);
						if(result.code == '00'){
							$('input[name="cardPrintAmount"]').val(result.data.cardPrintAmount);
							$('input[name="providerCode"]').val(result.data.providerCode);
						}else{
							alert("code: "+result.code+" msg: "+result.msg+" data: "+result.data);
						}
					}				
				});
				$("#loading").modal('hide');
			});
			$("#page-init").click(function(){
				var payType = $("[name=payType]").val();
				if(payType != 5 
					&& payType != 6 
					&& payType != 8 
					&& payType != 10 
					&& payType != 11
					&& payType != 13
					&& payType != 14
					&& payType != 15
					&& payType != 16
					&& payType != 17
					&& payType != 18){
					alert("请选择短信支付或电子钱包支付！！");
					return;
				}
				var mnoId = $("[name=mnoId]").val();
				var pointName = $("[name=pointName]").val();
				var url = "${rc.contextPath}/PayManager/codaPayPage?userId="+$("[name=userId]").val()+"&currency=BUK&payType="+payType+"&pointName="+pointName+"&money=0.99&platform="+$("[name=platform]").val()+"&channel="+$("[name=channel]").val();
				if(payType == 5 || payType == 8){
					url += ("&providerCode="+mnoId);
				}
				var mnoIdCambodia = $("[name=mnoId-cambodia]").val();
				if(payType == 11){
					url += ("&providerCode="+mnoIdCambodia);
				}
				
				location.href = url;
			});
		</script>
	</body>
</html>
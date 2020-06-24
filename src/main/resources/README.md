# 说明

SpringBoot Web 项目，用于完成游戏中不同类型的支付

## 项目结构

|---com.coda.airtime															------CodaPay

|---com.last.pay																  	------支付项目根目录

|---com.last.pay.base.datasource										------数据原配置

|---com.last.pay.core.controller											------控制层

|---com.last.pay.core.service												------业务逻辑层

## 支持的支付类型

1. 支付类型

   com.last.pay.base.common.constants.Constants.PayTypeConstants

```java
// IOS
public static final int IOS = 1;
// GOOGLE
public static final int GOOGLE = 2;
// 越南点卡
public static final int Vietnam_CARD = 3;
// 缅甸点卡EasyPoints
public static final int Myanmar_CODAPAY_Vouchers = 4;
// 缅甸短信
public static final int Myanmar_CODAPAY_SMS = 5;
// 缅甸电子钱包WaveMoney
public static final int Myanmar_CODAPAY_WAVE = 6;
// 华为国际
public static final int HUAWEI_INTERNATIONAL = 7;
// PayCent
public static final int PAYCENT = 8;
// 华为国内
public static final int HUAWEI_INLAND = 9;
// Upay电子钱包
public static final int VIETNAM_UPAY_EBANK = 10;
// 柬埔寨短信支付
public static final int Cambodia_CODAPAY_SMS = 11;
// 柬埔寨电子钱包
public static final int Cambodia_CODAPAY_Wing = 12;
```

2. 充值渠道（越南点卡子类）

   com.last.pay.base.common.Constants.PayChannelConstants

```java
public static final String pay_channel_param = "pay_channel";
public static final String WEB_CHANNEL = "web";
public static final String INAPP_CHANNEL = "inapp";
public static final int OLD_CHANNEL = 1; // 越南点卡旧渠道
public static final int UPAY_CHANNEL = 2;// uPay
public static final int KING_CARD_CHANNEL = 3; // KingCard
public static final int BANG_LANG_CHANNEL = 4; // BangLang
```

3. 充值平台（不同平台对应不同的点卡充值子类）

   com.last.pay.base.common.Constants.PayPlatformConstants

```java
// 游戏旧渠道
public static final int IN_GAME = 1;
// web旧渠道
public static final int WEB_OLD_CHANNEL = 4;
// 游戏Upay
public static final int IN_GAEM_UPAY = 5;
// Web Upay
public static final int WEB_UPAY = 6;
// 游戏KingCard
public static final int IN_GAME_KING_CARD = 7;
// Web KingCard
public static final int WEB_KING_CARD = 8;
// 游戏BangLang
public static final int IN_GAME_BANG_LANG = 9;
// Web BangLang
public static final int WEB_BANG_LANG = 10;
```



## Rest接口（Controller层）

1. com.last.pay.core.controller.PayController （支付接口）

   * 测试用页面

   ```java
   @RequestMapping("/PayManager/payOrder")
   public String payHomePage(Model model,String userId,String pointName,String currency){}
   ```

   * 用于游戏端调用

   ```java
   @RequestMapping("/PayManager/placeAnOrder")
   @ResponseBody
   public CodeMsg<?> placeAnOrder(HttpServletRequest request,PayOrder payOrder,String payType,String pointCount) {}
   ```

   * 游戏中的CodaPay支付页面

   ```java
   @RequestMapping("/PayManager/codaPayPage")
   public String codaPayPage(PayOrder payOrder,String payType,HttpServletRequest request,Model model) {}
   ```

   * 测试Redis中的订单（测试页面调用）

   ```java
   @RequestMapping("/PayManager/redisPay")
   @ResponseBody
   public CodeMsg<?> redisPay(PayCallbackCommand command){}
   ```

   

2. com.last.pay.core.controller.CallBackController（回调接口）

   * CodaPay后端回调

   ```java
   @RequestMapping("/backend")
   @ResponseBody
   public String backEndCallBack(HttpServletRequest servletRequest){}
   ```

   * CodaPay页面回调

   ```java
   @RequestMapping("/successpage")
   public String callBackPage(String TxnId,String OrderId,Model model){}
   ```

   * Upay后端回调

   ```java
   @RequestMapping("/upaybackend")
   @ResponseBody
   public String uPaycallBack(HttpServletRequest servletRequest) {}
   ```

   * PayCent页面回调

   ```java
   @RequestMapping("/paycentnotify")
   @ResponseBody
   public String paycentCallBack(HttpServletRequest servletRequest) {}
   ```

   * PayCent页面回调

   ```java
   @RequestMapping("/paycentreturnpage")
   public String paycentReturnPage() {}
   ```

   * 越南点卡支付回调（旧）

   ```java
   @RequestMapping("/vietnampayret")
   @ResponseBody
   public String vietnamPayCallBack(HttpServletRequest request) {}
   ```

   * 越南KingCard回调

   ```java
   @RequestMapping("/vietnamkcret")
   @ResponseBody
   public String vitnamKingCardCallBack(HttpServletRequest request,@RequestBody KingCardCallBackParam kingCardCallBackParam) {}
   ```

   * 越南BangLang回调

   ```java
   @RequestMapping("/vietnamblret")
   @ResponseBody
   public String vitnamBangLangCardCallback(@RequestParam Map<String, Object> paramMap) {}
   ```

   

3. com.last.pay.core.controller.HeartBeatController

   * 心跳检测

   ```java
   @RequestMapping("/heartbeat")
   @ResponseBody
   public Integer heartBeat() {}
   ```




## 业务处理（Service 第三方处理）

1. 不同的支付类型通过继承 *com.last.pay.core.handler.GeneralPayRequestHandler* 实现

```java
	/**
	 * 如果订单未成功，将执行该程序
	 * @param payOrder
	 * @param request
	 * @return
	 */
	public abstract CodeMsg<?> doProcessRequest(PayOrder payOrder,HttpServletRequest request);
	/**
	 * 检查第三方订单是否存在已经成功的订单
	 * @param payOrder
	 * @return
	 */
	public abstract boolean confirmIfExistPayOrder(PayOrder payOrder);
	/**
	 * 	检查第三方订单是否存在已经成功的订单（其他非订单号条件:例如卡号）
	 * @param payOrder
	 * @param httpServletRequest
	 * @return
	 */
	public abstract boolean confirmIfExistSuccessPayOrder(PayOrder payOrder, HttpServletRequest httpServletRequest);
	/**
	 * 检查订单中的补单号是否存在或者已经成功 （验证是否需要补单）
	 * @param replacementOrder
	 * @return
	 */
	public abstract boolean isSuccessPayOrder(ReplacementOrder replacementOrder);
```

2. 不同的越南点卡支付渠道通过继承*com.last.pay.core.handler.channel.vietnam.PayChannelHandler* 实现

```java
	/**
	 * 点卡渠道类型
	 * {@link PayChannelConstants}
	 * @return
	 */
	public int getPayChannel();
	/**
	 * 接口调用
	 * @param payOrder 订单
	 * @param cardNum 卡号
	 * @param cardPin 卡密
	 * @param providerCode 提供商
	 * @return
	 */
	public CodeMsg<?> doProcessRequest(PayOrder payOrder,String cardNum, String cardPin, String providerCode);
```

## 错误码&错误信息

请参考 *com.last.pay.base.CodeMsgType*

```java
	SUCCESS(0,"充值成功"), // 越南：00
	ERR_PAYTYPE(900,"訂單支付類型錯誤"),
	ERR_USERID(901,"用戶ID不合法"), //  
	ERR_DB(902,"数据库操作失败"),
	ERR_REDIS(903,"REDIS操作失败"),
	ERR_ORDER_NUM(904,"訂單編號錯誤"),
	ERR_ORDER_MONEY(905,"訂單金額錯誤"),
	ERR_ORDER_CURRENCY(906,"訂單货币标识錯誤"),
	ERR_ORDER_POINTNAME(907,"付費點錯誤"),
	ERR_SYS_CONFIG(908,"支付系统配置錯誤"),
	ERR_SYS_PARAMS(909,"参数处理失败"),
	ERR_SYS_PARAMS_CURRENCY(910,"货币标识不正确"),
	ERR_SYS_PARAMS_POINTNAME_HAVE_PURCHASE(911,"付费点不能重复够买"),
	ERR_SYS_PARAMS_CURRENCY_NONSUPPORT(921,"当前地区不支持该支付选项"),
	ERR_SYS_REQEUST_ADDR(922,"请求类型与地址不匹配"),
	ERR_CODAPAY_PARAM_PAYTYPE(952,"CodaPay的支付類型不合法"),
	ERR_CODAPAY_MNO_LIMIT(953,"电信运营商超出支付限额"),
	ERR_PARAM_MONEY(923,"输入金额为空"),
	ERR_PAYORDER_NUM(925,"未查找到订单信息"),
	ERR_PAYORDER_HAD_HANDLED(925,"订单已经完成"),
	
	
	ERR_SYS(300,"系統調用異常"),// 越南：-1
	ERR_PARTNERID(301,"合夥人ID不合法"),// 越南：01
	ERR_PROVIDER(302,"未找到供應商"),// 越南：02
	ERR_CORE_SYS(303,"系統内部異常"),// 越南：03
	ERR_REQUEST_PARAMS(304,"解析請求參數失敗"),// 越南：04
	ERR_REQUEST_ID(305,"請求ID不合法"),// 越南：05
	ERR_CARD_NUM(306,"卡號錯誤"),// 越南：06
	ERR_CARD_PIN(307,"卡PIN錯誤"),// 越南：07
	ERR_PROVIDER_LOCKED(308,"供應商暫時被鎖定"),// 越南：08
	ERR_CARD(324,"卡號或卡PIN錯誤，或已被使用"),// 越南：24
	ERR_REQUEST(325,"請檢查請求數據"),// 越南：25
	ERR_CARD_AMOUNT(326,"請求面額不合法"),// 越南：26
	ERR_TRANSACTION(336,"交易失敗，請五分鐘后重試"),// 越南：36
	ERR_TRANSACTION_PENDING(399,"交易請求正在執行，請檢查交易狀態"),// 越南：99
	ERR_PAY_FAILED(309,"交易失敗"),// 越南：09
	/***接口文档没有****/
	ERR_CARD_PRINT_AMOUNT(330,"卡面额必须大于0"),// 越南：30
	/***越南查询交易信息时，返回的卡面金额为0**/
	ERR_QUERY_CARD_AMOUNT_IS_ZERO(350,"未查找到卡面金额"),
	ERR_CARD_REPEAT_INPUT(380,"不能重复输入,请稍后再试"),
	ERR_TIMEOUT(381,"订单超时"),
	ERR_AUTH(382,"未授权"),
	ERR_OBJECT_NOT_FOUND(383,"未找到请求对象"),
	ERR_REQUEST_DENIED(438,"请求被拒绝"),
	
	ERR_GOOGLE_ACCESS_TOKEY(201,"生成访问Google APIs的access_token失败"),
	ERR_GOOGLE_PAY_FAILURE(202,"Google支付失败，订单失败"),
	ERR_GOOGLE_PAY_PARAM_PURCHASE(203,"购买信息的参数不合法"),
	ERR_GOOGLE_PAY_API(204,"调用Google API接口失败"),
	
	ERR_CALL_CODA_PAY(401,"调用CodaPay支付接口失败"),
	ERR_CALL_CODA_INIT(410,"初始化CodaPay事务请求失败"),
	ERR_CODA_PAY_PARAM_MNO(451,"CodaPay电信运营商编号错误"),
	ERR_CODA_PAY_CANCLE(402,"交易不合法被取消"),
	ERR_CODA_PAY_PARAM(403,"请求消息中的参数不正确"),
	ERR_CODA_PAY_VOUCHERS_CARDNUM(413,"卡号不正确"),
	ERR_CODA_PAY_VOUCHERS_CARDPIN(423,"卡PIN不正确"),
	ERR_CODA_PAY_SYS(422,"CodaPay系统出错"),
	ERR_CODA_PAY_API_KEY(404,"未知的API_KEY"),
	ERR_CODA_PAY_BIZ(405,"CodaPay的商家出错了"),
	ERR_CODA_PAY_REQUEST_INVALID(414,"请求值无效"),
	ERR_CODA_PAY_TRANS_STATUS(415,"交易已经启动但尚未完成"),
	ERR_CODA_PAY_CHANNEL_ACTIVITY(425,"支付通道未激活"),
	ERR_CODA_PAY_CHANNEL_INVALID(461,"支付通道不支持订阅"),
	ERR_CODA_PAY_VOUCHER_CARD_INVALID(443,"卡号已被使用或已失效"),
	ERR_CODA_PAY_BIZ_TRANS(407,"CodaPay的商家交易失败"),
	ERR_CODA_PAY_OPTS(408,"CodaPay未知操作错误"),
	ERR_CODA_PAY_VOUCHER_CODE(411,"VouchersCode错误"),
	ERR_CODA_PAY_VOUCHER_PIN(412,"VouchersPIN错误"),
	ERR_CODA_PAY_VOUCHER_CARD_TYPE(416,"CardType错误"),
	ERR_CODA_PAY_VOUCHER_PARAMS_BLACKLIST(409,"部分请求参数被列为黑名单"),
	ERR_CALL_CODA_PAY_ERROR(450,"初始化CodaPay交易事务失败"),
	ERR_CODACALL_BACK_PARAM(499,"回调参数验证失败，参数不合法"),
	
	ERR_IOS_DATA_NOT_READ(100,"App Store无法读取你提供的JSON数据"), // IOS 21000 
	ERR_IOS_RECEIPT_DATA_NOT_VALID(102,"收据信息不合法"), // IOS 21002 
	ERR_IOS_RECEIPT_DATA_NOT_AUTH(103,"收据信息未被验证"), // IOS 21003 
	ERR_IOS_SCRET_KEY_COINCIDENT(104,"收据的密钥与注册密钥不一致"), // IOS 21004 
	ERR_IOS_SCRET_SYSTEM_ERR(105,"收据信息未被验证"), // IOS 21005 
	ERR_IOS_RECEIPT_SEND_SANDBOX(107,"测试环境的凭据发送到生产环境中"),
	ERR_IOS_RECEIPT_SEND_PRODUCT(108,"生产环境的凭据发送到测试环境中"),
	ERR_IOS_RECEIPT_TRADE_NOT_MAKE(110,"购买凭据未生效"),
	ERR_IOS_INTERFACE_QUERY(120,"IOS查询凭据信息失败"),
	ERR_IOS_IN_APP_DATA(121,"IOS应用内购买信息为空"),
	ERR_IOS_RECEIPT_EMPTY(122,"IOS凭据信息为空"),
	ERR_IOS_SYSTEM_IN(199,"IOS支付内部故障"),
	
	ERR_HUAWEI_PURCHASE_CANCEL(601,"支付已经取消"),
	ERR_HUAWEI_PURCHASE_REFUND(602,"支付已经退款"),
	ERR_HUAWEI_PRODUCTID(603,"产品ID为空"),
	ERR_HUAWEI_PURCHASETOKEN(604,"购买信息为空"),
	ERR_HUAWEI_RESPONSE(605,"获取响应信息失败"),
	ERR_HUAWEI_SIGNATURE(606,"订单信息签名验证失败"),
	ERR_HUAWEI_GETAUTH(607,"获取access_token失败"),
	ERR_HUAWEI_WORD_CANCEL(611,"用户取消了订单"),
	ERR_HUAWEI_NETWORK(612,"网络连接异常"),
	ERR_HUAWEI_REQUEST_NOT_SUPPORT(613,"支付API版本不支持该请求类型"),
	ERR_HUAWEI_PRODUCT_NOT_SUPPORT(614,"产品不支持购买"),
	ERR_HUAWEI_PARAM(615,"请求参数无效"),
	ERR_HUAWEI_API_ERROR(616,"请求处理出错"),
	ERR_HUAWEI_PURCHASE_HAS_PAY(617,"产品已经购买"),
	ERR_HUAWEI_PURCHASE_NOT_HAS(618,"产品未购买，消耗失败"),
	ERR_HUAWEI_PRODUCT_HAS_SPEND(619,"产品未购买，消耗失败"),
	
	ERR_PAYCENT_FAIL(801,"支付失败"),
	ERR_PAYCENT_WAIT_CONFIRM(802,"支付失败"),
	ERR_PAYCENT_CANCLE(803,"支付取消"),
	ERR_PAYCENT_SECRET(804,"支付验证密钥错误"),
	ERR_PAYCENT_RETURN_URL(805,"返回地址错误"),
	ERR_PAYCENT_NOTIFY_URL(806,"回调地址错误"),
	ERR_PAYCENT_MERCHANT(807,"商户参数错误"),
	ERR_PAYCENT_PARAM(808,"参数错误"),
	ERR_PAYCENT_PAYCENT_SERVER(809,"服务地址错误"),
	ERR_PAYCENT_PAYCENT_MNO(810,"运营商编号错误"),
	ERR_PAYCENT_EXPIRE_CODE(811,"交易已过期"),
	
	WARN_TRANS_IS_PROCESSED(9000,"支付正在处理中，请稍后"),
```


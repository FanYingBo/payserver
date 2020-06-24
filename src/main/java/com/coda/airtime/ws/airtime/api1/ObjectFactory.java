
package com.coda.airtime.ws.airtime.api1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.coda.airtime.ws.airtime.api1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _InitResult_QNAME = new QName("http://ws.airtime.coda.com/airtime/api1.0", "initResult");
    private final static QName _InquiryPaymentResultResponse_QNAME = new QName("http://ws.airtime.coda.com/airtime/api1.0", "inquiryPaymentResultResponse");
    private final static QName _InquiryPaymentRequest_QNAME = new QName("http://ws.airtime.coda.com/airtime/api1.0", "inquiryPaymentRequest");
    private final static QName _InitResponse_QNAME = new QName("http://ws.airtime.coda.com/airtime/api1.0", "initResponse");
    private final static QName _InquiryPaymentResult_QNAME = new QName("http://ws.airtime.coda.com/airtime/api1.0", "inquiryPaymentResult");
    private final static QName _ItemInfo_QNAME = new QName("http://ws.airtime.coda.com/airtime/api1.0", "itemInfo");
    private final static QName _Init_QNAME = new QName("http://ws.airtime.coda.com/airtime/api1.0", "init");
    private final static QName _InitRequest_QNAME = new QName("http://ws.airtime.coda.com/airtime/api1.0", "initRequest");
    private final static QName _PaymentResult_QNAME = new QName("http://ws.airtime.coda.com/airtime/api1.0", "paymentResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.coda.airtime.ws.airtime.api1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InitRequest }
     * 
     */
    public InitRequest createInitRequest() {
        return new InitRequest();
    }

    /**
     * Create an instance of {@link InitRequest.Profile }
     * 
     */
    public InitRequest.Profile createInitRequestProfile() {
        return new InitRequest.Profile();
    }

    /**
     * Create an instance of {@link PaymentResult }
     * 
     */
    public PaymentResult createPaymentResult() {
        return new PaymentResult();
    }

    /**
     * Create an instance of {@link PaymentResult.Profile }
     * 
     */
    public PaymentResult.Profile createPaymentResultProfile() {
        return new PaymentResult.Profile();
    }

    /**
     * Create an instance of {@link InitResult }
     * 
     */
    public InitResult createInitResult() {
        return new InitResult();
    }

    /**
     * Create an instance of {@link InitResult.Profile }
     * 
     */
    public InitResult.Profile createInitResultProfile() {
        return new InitResult.Profile();
    }

    /**
     * Create an instance of {@link InquiryPaymentResultResponse }
     * 
     */
    public InquiryPaymentResultResponse createInquiryPaymentResultResponse() {
        return new InquiryPaymentResultResponse();
    }

    /**
     * Create an instance of {@link InquiryPaymentResult }
     * 
     */
    public InquiryPaymentResult createInquiryPaymentResult() {
        return new InquiryPaymentResult();
    }

    /**
     * Create an instance of {@link Init }
     * 
     */
    public Init createInit() {
        return new Init();
    }

    /**
     * Create an instance of {@link ItemInfo }
     * 
     */
    public ItemInfo createItemInfo() {
        return new ItemInfo();
    }

    /**
     * Create an instance of {@link InquiryPaymentRequest }
     * 
     */
    public InquiryPaymentRequest createInquiryPaymentRequest() {
        return new InquiryPaymentRequest();
    }

    /**
     * Create an instance of {@link InitResponse }
     * 
     */
    public InitResponse createInitResponse() {
        return new InitResponse();
    }

    /**
     * Create an instance of {@link ArrayList }
     * 
     */
    public ArrayList createArrayList() {
        return new ArrayList();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link InitRequest.Profile.Entry }
     * 
     */
    public InitRequest.Profile.Entry createInitRequestProfileEntry() {
        return new InitRequest.Profile.Entry();
    }

    /**
     * Create an instance of {@link PaymentResult.Profile.Entry }
     * 
     */
    public PaymentResult.Profile.Entry createPaymentResultProfileEntry() {
        return new PaymentResult.Profile.Entry();
    }

    /**
     * Create an instance of {@link InitResult.Profile.Entry }
     * 
     */
    public InitResult.Profile.Entry createInitResultProfileEntry() {
        return new InitResult.Profile.Entry();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InitResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.airtime.coda.com/airtime/api1.0", name = "initResult")
    public JAXBElement<InitResult> createInitResult(InitResult value) {
        return new JAXBElement<InitResult>(_InitResult_QNAME, InitResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InquiryPaymentResultResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.airtime.coda.com/airtime/api1.0", name = "inquiryPaymentResultResponse")
    public JAXBElement<InquiryPaymentResultResponse> createInquiryPaymentResultResponse(InquiryPaymentResultResponse value) {
        return new JAXBElement<InquiryPaymentResultResponse>(_InquiryPaymentResultResponse_QNAME, InquiryPaymentResultResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InquiryPaymentRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.airtime.coda.com/airtime/api1.0", name = "inquiryPaymentRequest")
    public JAXBElement<InquiryPaymentRequest> createInquiryPaymentRequest(InquiryPaymentRequest value) {
        return new JAXBElement<InquiryPaymentRequest>(_InquiryPaymentRequest_QNAME, InquiryPaymentRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InitResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.airtime.coda.com/airtime/api1.0", name = "initResponse")
    public JAXBElement<InitResponse> createInitResponse(InitResponse value) {
        return new JAXBElement<InitResponse>(_InitResponse_QNAME, InitResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InquiryPaymentResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.airtime.coda.com/airtime/api1.0", name = "inquiryPaymentResult")
    public JAXBElement<InquiryPaymentResult> createInquiryPaymentResult(InquiryPaymentResult value) {
        return new JAXBElement<InquiryPaymentResult>(_InquiryPaymentResult_QNAME, InquiryPaymentResult.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ItemInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.airtime.coda.com/airtime/api1.0", name = "itemInfo")
    public JAXBElement<ItemInfo> createItemInfo(ItemInfo value) {
        return new JAXBElement<ItemInfo>(_ItemInfo_QNAME, ItemInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Init }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.airtime.coda.com/airtime/api1.0", name = "init")
    public JAXBElement<Init> createInit(Init value) {
        return new JAXBElement<Init>(_Init_QNAME, Init.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InitRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.airtime.coda.com/airtime/api1.0", name = "initRequest")
    public JAXBElement<InitRequest> createInitRequest(InitRequest value) {
        return new JAXBElement<InitRequest>(_InitRequest_QNAME, InitRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PaymentResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.airtime.coda.com/airtime/api1.0", name = "paymentResult")
    public JAXBElement<PaymentResult> createPaymentResult(PaymentResult value) {
        return new JAXBElement<PaymentResult>(_PaymentResult_QNAME, PaymentResult.class, null, value);
    }

}

package uni.aznu;


import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uni.aznu.exceptions.EquipmentException;
import uni.aznu.exceptions.VisitException;
import uni.aznu.model.*;
import uni.aznu.state.ProcessingEvent;
import uni.aznu.state.ProcessingState;
import uni.aznu.state.StateService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.camel.model.rest.RestParamType.body;

@Component
public class BookingService extends RouteBuilder {
    @Autowired
    BookingIdentifierService bookingIdentifierService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    StateService equipmentStateService;
    @Autowired
    StateService visitStateService;

    @Value("${kafka.server}")
    private String kafkaServer;
    @Value("${booking.service.type}")
    private String bookingServiceType;

    @Override
    public void configure() throws Exception {

        if (bookingServiceType.equals("all") || bookingServiceType.equals("visit"))
            bookVisitExceptionHandlers();
        if (bookingServiceType.equals("all") || bookingServiceType.equals("equipment"))
            bookEquipmentExceptionHandlers();
        if (bookingServiceType.equals("all") || bookingServiceType.equals("gateway"))
            gateway();
        if (bookingServiceType.equals("all") || bookingServiceType.equals("equipment"))
            equipment();
        if (bookingServiceType.equals("all") || bookingServiceType.equals("visit"))
            visit();
        if (bookingServiceType.equals("all") || bookingServiceType.equals("payment"))
            payment();
    }

    private void bookVisitExceptionHandlers() {
        onException(VisitException.class)
                .process(BookingService::exceptionLogic)
                .marshal().json()
                .to("stream:out")
                .setHeader("serviceType", constant("visit"))
                .to("kafka:BookingFailTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType )
                .handled(true);
    }

    private static void exceptionLogic(Exchange exchange) {
        ExceptionResponse er = new ExceptionResponse();
        er.setTimestamp(OffsetDateTime.now());
        Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        er.setMessage(cause.getMessage());

        // Extract bookingId from the message header
        String bookingId = exchange.getMessage().getHeader("bookingId", String.class);
        er.setBookingId(bookingId);
        exchange.getMessage().setBody(er);
    }


    private void bookEquipmentExceptionHandlers() {
        onException(EquipmentException.class)
                .process(BookingService::exceptionLogic)
                .marshal().json()
                .to("stream:out")
                .setHeader("serviceType", constant("equipment"))
                .to("kafka:BookingFailTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType )
                .handled(true);
    }


    private void payment() {
        AtomicBoolean isCanceled = new AtomicBoolean(false);
        from("kafka:BookingInfoTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType ).routeId("paymentBookingInfo")
                .log("fired paymentBookingInfo")
                .unmarshal().json(JsonLibrary.Jackson, BookingInfo.class)
                .process(
                        (exchange) -> paymentLogic(exchange, isCanceled))
                .choice()
                .when(header("isReady").isEqualTo(true)).to("direct:finalizePayment")
                .otherwise().to("direct:cancelPayment")
                .endChoice();

        from("kafka:ProcessReqTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType ).routeId("paymentProcessReq")
                .log("fired paymentProcessReq")
                .unmarshal().json(JsonLibrary.Jackson, BookProcessRequest.class)
                .process(
                        (exchange) -> {
                            String bookingId = exchange.getMessage()
                                    .getHeader("bookingId", String.class);
                            boolean isReady= paymentService.addBookProcessRequest(
                                    bookingId,
                                    exchange.getMessage().getBody(BookProcessRequest.class));
                            exchange.getMessage().setHeader("isReady", isReady);
                        })
                .choice()
                .when(header("previousState").isEqualTo(ProcessingState.CANCELLED))
                .to("direct:paymentCompensationAction")
                .otherwise()
                .choice()
                .when(header("isReady").isEqualTo(true)).to("direct:finalizePayment")
                .endChoice().endChoice();

        from("direct:paymentCompensationAction").routeId("paymentCompensationAction")
                .log("fired paymentCompensationAction")
                .to("stream:out");

        from("direct:finalizePayment").routeId("finalizePayment")
                .log("fired finalizePayment")
                .process(this::finalizePaymentLogic)
                .to("kafka:BookingResultTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType )
                .to("direct:notification");

        from("direct:notification").routeId("notification")
                .log("fired notification")
                .process(this::notify)
                .to("stream:out");
        from("direct:cancelPayment").log("fired notification").to("stream:out");
    }

    private void notify(Exchange exchange) {
        String bookingId = exchange.getMessage().getHeader("bookingId",
                String.class);
        if(visitStateService.getState(bookingId).equals(ProcessingState.CANCELLED) ||
                visitStateService.getState(bookingId).equals(ProcessingState.FINISHED)) {
            visitStateService.sendEvent(bookingId, ProcessingEvent.COMPLETE);
            visitStateService.removeState(bookingId);
        }
        if(equipmentStateService.getState(bookingId).equals(ProcessingState.CANCELLED) ||
                equipmentStateService.getState(bookingId).equals(ProcessingState.FINISHED)) {
            equipmentStateService.sendEvent(bookingId, ProcessingEvent.COMPLETE);
            equipmentStateService.removeState(bookingId);
        }
    }

    private void finalizePaymentLogic(Exchange exchange) {
        String bookingId = exchange.getMessage().
                getHeader("bookingId", String.class);
        PaymentService.PaymentData paymentData =
                paymentService.getPaymentData(bookingId);
        BigDecimal equipmentCost=paymentData.equipmentBookingInfo.getCost();
        if(equipmentCost == null){
            equipmentCost = new BigDecimal(0);
        }
        BigDecimal visitCost=paymentData.visitBookingInfo.getCost();
        if(visitCost == null){
            visitCost = new BigDecimal(0);
        }
        BigDecimal totalCost=equipmentCost.add(visitCost);
        ResultModel resultModel = new ResultModel();
        resultModel.setId(bookingId);
        resultModel.setMessage("Cost: " + totalCost);
        exchange.getMessage().setBody(resultModel);
    }

    private void paymentLogic(Exchange exchange, AtomicBoolean isCanceled) {
        String bookingId =
                exchange.getMessage().getHeader("bookingId", String.class);
        isCanceled.set(visitStateService.getState(bookingId).equals(ProcessingState.CANCELLED) ||
                equipmentStateService.getState(bookingId).equals(ProcessingState.CANCELLED));
        if(!isCanceled.get()) {
            boolean isReady= paymentService.addBookingInfo(
                    bookingId,
                    exchange.getMessage().getBody(BookingInfo.class),
                    exchange.getMessage().getHeader("serviceType", String.class));
            exchange.getMessage().setHeader("isReady", isReady);
        }else{
            exchange.getMessage().setHeader("isReady", false);
        }
    }

    private void visit() {

        from("cxf://http://localhost:8090/soap/BookingService"
                + "?serviceClass=uni.aznu.soap.BookingServicePortType"
                + "&wsdlURL=/visit/BookingService.wsdl")
                .routeId("soapBookingService")
                .log("SOAP request received: ${body}")
                .process(this::handleSoapRequest)
                .to("direct:processSoapResponse");

        from("direct:processSoapResponse")
                .process(exchange -> {
                    // Construct SOAP response logic here
                    String bookingId = exchange.getIn().getHeader("bookingId", String.class);
                    exchange.getMessage().setBody("Booking response for ID: " + bookingId);
                });

        from("direct:invokeSoapService")
                .setHeader(CxfConstants.OPERATION_NAME, constant("BookEquipment"))
                .setHeader(CxfConstants.OPERATION_NAMESPACE, constant("http://aznu.uni/"))
                .to("cxf://http://external-soap-service-endpoint"
                        + "?serviceClass=uni.aznu.soap.BookingServicePortType"
                        + "&wsdlURL=/path-to/BookingService.wsdl")
                .log("SOAP response received: ${body}");

//        from("kafka:ProcessReqTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType )
//                .routeId("bookVisit")
//                .log("fired bookVisit")
//                .unmarshal().json(JsonLibrary.Jackson, BookProcessRequest.class)
//                .process(this::visitLogic)
//                .marshal().json()
//                .to("stream:out")
//                .choice()
//                .when(header("previousState").isEqualTo(ProcessingState.CANCELLED))
//                .to("direct:bookVisitCompensationAction")
//                .otherwise()
//                .setHeader("serviceType", constant("visit"))
//                .to("kafka:BookingInfoTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType )
//                .endChoice();
//
//        from("kafka:BookingFailTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType ).routeId("bookVisitCompensation")
//                        .log("fired bookVisitCompensation")
//                        .unmarshal().json(JsonLibrary.Jackson, ExceptionResponse.class)
//                        .choice().when(header("serviceType").isNotEqualTo("visit"))
//                            .process((exchange) -> compensationLogic(exchange, visitStateService))
//                            .choice().when(header("previousState").isEqualTo(ProcessingState.FINISHED))
//                                .to("direct:bookVisitCompensationAction")
//                            .endChoice()
//                        .endChoice();
//
//        from("direct:bookVisitCompensationAction").routeId("bookVisitCompensationAction")
//                .log("fired bookVisitCompensationAction")
//                .to("stream:out");

    }

    private void equipment() {
        from("kafka:ProcessReqTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType )
                .routeId("bookEquipment")
                .log("fired bookEquipment").
                unmarshal().json(JsonLibrary.Jackson, BookProcessRequest.class)
                .process(this::equipmentLogic)
                .marshal().json()
                .to("stream:out")
                .choice()
                    .when(header("previousState").isEqualTo(ProcessingState.CANCELLED))
                    .to("direct:bookEquipmentCompensationAction")
                .otherwise()
                    .setHeader("serviceType", constant("equipment"))
                    .to("kafka:BookingInfoTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType )
                .endChoice();

        from("kafka:BookingFailTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType ).routeId("bookEquipmentCompensation")
                .log("fired bookEquipmentCompensation")
                .unmarshal().json(JsonLibrary.Jackson, ExceptionResponse.class)
                .choice().when(header("serviceType").isNotEqualTo("equipment"))
                    .process((exchange) -> compensationLogic(exchange, equipmentStateService))
                    .choice().when(header("previousState").isEqualTo(ProcessingState.FINISHED))
                        .to("direct:bookEquipmentCompensationAction")
                    .endChoice()
                .endChoice();

        from("direct:bookEquipmentCompensationAction").routeId("bookEquipmentCompensationAction")
                .log("fired bookEquipmentCompensationAction")
                .to("stream:out");
    }

    private void equipmentLogic(Exchange exchange) throws EquipmentException {
        String bookingId =
                exchange.getMessage().getHeader("bookingId", String.class);
        ProcessingState previousState =
                equipmentStateService.sendEvent(bookingId, ProcessingEvent.START);
        if (previousState!=ProcessingState.CANCELLED){
            BookingInfo bookingInfo = new BookingInfo();
            bookingInfo.setId(bookingId);
            BookProcessRequest request = exchange.getMessage().getBody(BookProcessRequest.class);
            if (request != null && request.getEquipment() != null
                    && request.getEquipment().getEType() != null) {
                String eType = request.getEquipment().getEType();
                if (eType.equals("Tent")) {
                    bookingInfo.setCost(new BigDecimal(100));
                }
                else if(eType.equals("Harness")){
                    throw new EquipmentException("Not available type of equipment: " + eType);
                }
                else {
                    bookingInfo.setCost(new BigDecimal(50));
                }
            }
            exchange.getMessage().setBody(bookingInfo);
            previousState = equipmentStateService.sendEvent(bookingId,
                    ProcessingEvent.FINISH);
        }
        exchange.getMessage().setHeader("previousState", previousState);
    }

    private void compensationLogic(Exchange exchange, StateService stateService) {
        String bookingId = exchange.getMessage().getHeader("bookingId", String.class);
        ProcessingState previousState = stateService.sendEvent(bookingId,
                ProcessingEvent.CANCEL);
        exchange.getMessage().setHeader("previousState", previousState);
    }

    private void gatewayRestConfig(){
        restConfiguration()
                .component("servlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .enableCORS(true)
                .contextPath("/api")
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Micro Process booking API")
                .apiProperty("api.version", "1.0.0");

        rest("/process")
                .description("Micro Process booking REST service")
                .consumes("application/json")
                .produces("application/json")
                .post("/booking").description("Book an equipment")
                .type(BookProcessRequest.class).outType(ResultModel.class)
                .param().name("body").type(body).description("The equipment to book").endParam()
                .responseMessage().code(200).message("Equipment successfully booked").endResponseMessage()
                .to("direct:bookProcess");

        rest("/process").description("Equipment booking result")
                .produces("application/json")
                .get("/result").description("Get equipment booking result").outType(ResultModel.class)
                .responseMessage().code(200).message("Equipment booking result").endResponseMessage()
                .to("direct:BookingResult");

        rest("/bookingResult").get("/{id}")
                .route().routeId("RenderHtml")
                .process(exchange -> {
                    String bookingId = exchange.getIn().getHeader("id", String.class);
                    ResultModel result = InMemoryStorage.getResult(bookingId);
                    String message;
                    boolean refresh = false;
                    if (result == null) {
                        message = "Result not yet available. Please refresh.";
                        refresh = true;
                    }
                    else{
                        message = result.getMessage();
                    }
                    String htmlResponse = generateHtmlResponse(bookingId, message, refresh);
                    exchange.getMessage().setBody(htmlResponse);
                    exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "text/html");
                });

        rest("/error")
                .get()
                .produces("text/html")
                .route().routeId("error")
                .process(exchange -> {
                    String htmlResponse = generateHtmlError("random");
                    exchange.getMessage().setBody(htmlResponse);
                    exchange.getMessage().setHeader("Content-Type", "text/html");
                });
    }

    private void gateway() {
        gatewayRestConfig();

        from("direct:bookProcess")
                .routeId("bookProcess")
                .log("bookProcess fired")
                .process((exchange) ->
                        exchange.getMessage().setHeader("bookingId", bookingIdentifierService.getBookingIdentifier()))
                .to("direct:ProcessBookRequest")
                .to("direct:bookRequester");

        from("direct:bookRequester")
                .routeId("bookRequester")
                .log("bookRequester fired")
                .process((exchange) ->
                        exchange.getMessage().setBody(Utils.prepareBookingInfo(
                                exchange.getMessage().getHeader("bookingId", String.class),
                                null
                        ))
                );

        from("direct:ProcessBookRequest")
                .routeId("ProcessBookRequest")
                .log("brokerTopic fired")
                .marshal().json()
                .to("kafka:ProcessReqTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType );

        from("kafka:BookingResultTopic?brokers=" + kafkaServer + "&groupId=" + bookingServiceType)
                .routeId("BookingResultKafka")
                .log("Booking result request for id: ${header.bookingId}")
                .to("direct:Storage");

        from("kafka:BookingFailTopic?brokers=" + kafkaServer)
                .routeId("BookingFailKafkaGateway")
                .log("Error on booking")
                .process(exchange -> {
                    String rawMessage = exchange.getIn().getBody(String.class);
                    rawMessage = rawMessage.substring(1, rawMessage.length()-1);
                    String[] fields = rawMessage.split("\"");
                    String result = "{"+fields[3]+"|"+fields[7]+ " " + fields[11] + "}";
                    exchange.getMessage().setBody(result);
                })
                .to("direct:Storage");

        from("direct:Storage")
                .routeId("store")
                .process(exchange -> {
                    String rawMessage = exchange.getIn().getBody(String.class);
                    rawMessage = rawMessage.substring(1, rawMessage.length()-1);
                    ResultModel result = new ResultModel();
                    String[] fields = rawMessage.split("[|]");
                    result.setId(fields[0]);
                    result.setMessage(fields[1]);
                    InMemoryStorage.addResult(result);
                });
    }

    private String generateHtmlResponse(String id, String message, boolean refresh) {
        String html;
        html = "<html>" +
                "<head><title>Booking Result</title></head>" +
                "<body>" +
                "<h1>Booking Result</h1>" +
                "<p><strong>ID:</strong> " + id + "</p>" +
                "<p><strong>Message:</strong> " + message + "</p>";
        if(refresh){
            html += "<a href=\"/api/bookingResult/"+id+"\">Refresh</a>";
        }
        else{
            html += "<a href=\"/booking\">Book again</a>";
        }
        html += "</body>" +
                "</html>";
        return html;

    }

    private static String generateHtmlError(String message){
        String html;
        html = "<html>" +
                "<head><title>Booking Error</title></head>" +
                "<body>" +
                "<h1>Booking Error</h1>" +
                "<p><strong>Message:</strong> " + message + "</p>"+
                "<a href=\"/booking\">Book again</a>";
        html += "</body>" +
                "</html>";
        return html;
    }

}

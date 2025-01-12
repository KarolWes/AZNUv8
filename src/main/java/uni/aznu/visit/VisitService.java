package uni.aznu.visit;


import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;

@WebService(targetNamespace = "http://example.com/visit", name = "VisitBookingPortType")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public class VisitService {

    @WebMethod(operationName = "BookVisit")
    public BookVisitResponse bookVisit(
            @WebParam(name = "BookVisitRequest") BookVisitRequest request) {

        BookVisitResponse response = new BookVisitResponse();
        String bookingId = request.getBookingId();

        ProcessingState previousState = visitStateService.sendEvent(bookingId, ProcessingEvent.START);
        if (previousState != ProcessingState.CANCELLED) {
            // Prepare response
            BookingInfo bookingInfo = new BookingInfo();
            bookingInfo.setId(bookingId);
            // Perform logic based on request data

            // Update state
            previousState = visitStateService.sendEvent(bookingId, ProcessingEvent.FINISH);
            response.setStatus("SUCCESS");
            response.setMessage("Visit booked successfully");
        } else {
            response.setStatus("FAILED");
            response.setMessage("Visit booking cancelled");
        }

        return response;
    }

    // Example state service (mock implementation for demonstration)
    private final VisitStateService visitStateService = new VisitStateService();

    // Supporting classes and enums
    public static class BookVisitRequest {
        private String bookingId;
        private String requestData;

        public String getBookingId() {
            return bookingId;
        }

        public void setBookingId(String bookingId) {
            this.bookingId = bookingId;
        }

        public String getRequestData() {
            return requestData;
        }

        public void setRequestData(String requestData) {
            this.requestData = requestData;
        }

    }

    public static class BookVisitResponse {
        private String status;
        private String message;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    public static class VisitStateService {
        public ProcessingState sendEvent(String bookingId, ProcessingEvent event) {
            // Simulate state transition logic
            return ProcessingState.FINISHED;
        }
    }

    public enum ProcessingEvent {
        START,
        FINISH
    }

    public enum ProcessingState {
        CANCELLED,
        FINISHED
    }

    public static class BookingInfo {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }
}

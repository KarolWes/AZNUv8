package uni.aznu.visit;

import org.springframework.stereotype.Service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@Service
@WebService(targetNamespace = "http://example.com/visit", name = "VisitBookingPortType")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public class VisitService {

    @WebMethod(operationName = "BookVisit")
    public uni.aznu.visit.BookVisitResponse bookVisit(
            @WebParam(name = "BookVisitRequest") uni.aznu.visit.BookVisitRequest request) {

        uni.aznu.visit.BookVisitResponse response = new uni.aznu.visit.BookVisitResponse();
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

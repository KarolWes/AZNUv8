package uni.aznu.soapserver;

import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

@Service
@WebService
public class VisitService {
    public BookVisitResponse process(BookVisitRequest request) {
        BookVisitResponse response = new BookVisitResponse();
        response.setStatus("success");
        return response;
    }
}

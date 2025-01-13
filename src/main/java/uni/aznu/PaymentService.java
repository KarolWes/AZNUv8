package uni.aznu;


import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import uni.aznu.model.BookProcessRequest;
import uni.aznu.model.BookingInfo;

@Service
public class PaymentService {
    private HashMap<String, PaymentData> payments;

    @PostConstruct
    void init() {
        payments = new HashMap<>();
    }

    public static class PaymentData {
        BookProcessRequest bookProcessRequest;
        BookingInfo equipmentBookingInfo;
        BookingInfo visitBookingInfo;

        public boolean isReady() {
            return bookProcessRequest != null && equipmentBookingInfo != null && visitBookingInfo != null;
        }
    }

    public synchronized boolean addBookProcessRequest(String bookProcessId, BookProcessRequest bookProcessRequest) {
        PaymentData paymentData = getPaymentData(bookProcessId);
        paymentData.bookProcessRequest = bookProcessRequest;
        return paymentData.isReady();
    }


    public synchronized boolean addBookingInfo(String bookProcessId, BookingInfo bookingInfo, String serviceType) {
        PaymentData paymentData = getPaymentData(bookProcessId);
        if (serviceType.equals("visit"))
            paymentData.visitBookingInfo = bookingInfo;
        else
            paymentData.equipmentBookingInfo = bookingInfo;
        return paymentData.isReady();
    }


    public synchronized PaymentData getPaymentData(String bookProcessId) {
        PaymentData paymentData = payments.get(bookProcessId);
        if (paymentData == null) {
            paymentData = new PaymentData();
            payments.put(bookProcessId, paymentData);
        }
        return paymentData;
    }


}
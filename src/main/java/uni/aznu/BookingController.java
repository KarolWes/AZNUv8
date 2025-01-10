package uni.aznu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import uni.aznu.model.BookProcessRequest;

@Controller
public class BookingController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/booking")
    public String bookingForm(Model model){
        BookProcessRequest request = new BookProcessRequest();
        model.addAttribute("request", request);
        return "index";
    }

    @PostMapping("/booking")
    public String returnSite(BookProcessRequest request, Model model){
        String id = restTemplate.postForObject("http://gateway:8090/api/process/booking", request, String.class);
        model.addAttribute("bookingId", id);
        return "result";
    }
}

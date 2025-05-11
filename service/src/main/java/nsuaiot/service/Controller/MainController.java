package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.Service.MainService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/main")
public class MainController {

    private final MainService mainService;

    //메인 페이지
    @Transactional(readOnly = true)
    @GetMapping("/graph")
    public ResponseEntity<?> showMain(@RequestAttribute("userId") String userId){
        return mainService.main(userId);
    }

}

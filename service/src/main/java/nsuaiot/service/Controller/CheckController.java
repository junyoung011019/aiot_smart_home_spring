package nsuaiot.service.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.PlugActionRequest;
import nsuaiot.service.Security.JwtTokenValid;
import nsuaiot.service.Service.CheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/check")
public class CheckController {

    private final CheckService checkService;
    private final JwtTokenValid jwtTokenValid;

    @GetMapping("/plugList")
    public ResponseEntity<String> checkPlugList(){
        return checkService.checkPlugList();
    }

    @GetMapping("/plugState/{plugId}")
    public ResponseEntity<String> checkPlug(@PathVariable String plugId){
        return checkService.checkPlug(plugId);
    }

    @GetMapping("/token")
    public ResponseEntity<String> testToken(HttpServletRequest request){
        ResponseEntity<String> response = jwtTokenValid.validateToken(request,true);
        if(response!=null) return response;
        String userId = (String) request.getAttribute("userId");
        System.out.println("userId" + userId);
        return ResponseEntity.ok(" ");
    }

}

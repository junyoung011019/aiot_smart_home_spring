package nsuaiot.service.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.Service.UsageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/usage")
public class UsageController {

    private final UsageService usageService;

    @GetMapping("/week/plug/{plugId}")
    public ResponseEntity<String> weekUsagePlug(@PathVariable String plugId) throws JsonProcessingException {
        return usageService.weekUsagePlug(plugId);
    }

    @GetMapping("/week/groupPlug")
    public ResponseEntity<String> weekUsageGroupPlug() throws JsonProcessingException {
        return usageService.weekUsageGroupPlug();
    }

    @GetMapping("/month")
    public ResponseEntity<String> monthUsage() throws JsonProcessingException {
        return usageService.monthUsage();
    }

}


package nsuaiot.service.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.PlugActionRequest;
import nsuaiot.service.Service.ControlService;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/control")
public class ControlController {

    private final ControlService controlService;

    @Transactional(readOnly = true)
    @PostMapping("/device/{plugId}")
    public ResponseEntity<?> controlDevice(
            @RequestAttribute("userId") String userId,
            @PathVariable String plugId,
            @RequestBody PlugActionRequest plugActionRequest) {
        return controlService.controlDevice(plugId, plugActionRequest.getAction(), userId);
    }
}


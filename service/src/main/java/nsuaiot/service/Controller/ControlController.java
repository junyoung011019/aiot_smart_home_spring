package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.PlugActionRequest;
import nsuaiot.service.Repository.PlugRepository;
import nsuaiot.service.Service.ControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/control")
public class ControlController {

    private final ControlService controlService;

    @PostMapping("/device/{plugId}")
    public ResponseEntity<Map<String, Object>> controlDevice(
            @PathVariable String plugId,
            @RequestBody PlugActionRequest plugActionRequest) {
        return controlService.controlDevice(plugId, plugActionRequest.getAction());
    }
}


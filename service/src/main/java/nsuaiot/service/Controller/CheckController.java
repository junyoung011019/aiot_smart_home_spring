package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.Entity.Plug;
import nsuaiot.service.Repository.PlugRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CheckController {
    private final PlugRepository plugRepository;

    @GetMapping("/check/plug")
    public ResponseEntity<List<Plug>> plug(){
        List<Plug> result = plugRepository.findAll();
        return ResponseEntity.ok(result);
    }

}

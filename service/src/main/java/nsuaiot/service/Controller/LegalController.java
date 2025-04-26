package nsuaiot.service.Controller;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.Service.LegalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/legal")
public class LegalController {

    private final LegalService legalService;

    @GetMapping("/terms")
    public ResponseEntity<Resource> legalTerms() throws FileNotFoundException, MalformedURLException {
        return legalService.viewTerms();
    }

    @GetMapping("/privacy")
    public ResponseEntity<Resource> viewPrivacy() throws FileNotFoundException, MalformedURLException {
        return legalService.viewPrivacy();
    }

}

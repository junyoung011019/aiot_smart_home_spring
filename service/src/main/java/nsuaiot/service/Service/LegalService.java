package nsuaiot.service.Service;

import lombok.RequiredArgsConstructor;
import nsuaiot.service.DTO.ApiResponse;
import nsuaiot.service.Entity.Plug;
import nsuaiot.service.Repository.PlugRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LegalService {

    @Value("${pdf.terms}")
    private String PDF_TERMS;

    @Value("${pdf.privacy}")
    private String PDF_PRIVACY;

    public ResponseEntity<Resource> viewTerms() throws FileNotFoundException, MalformedURLException {
        Path path = Paths.get(PDF_TERMS);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("PDF 파일을 찾을 수 없습니다.");
        }

        String encodedFileName = URLEncoder.encode("서비스이용약관(자사 제품, 서비스 공급용).pdf", StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"); // 공백 처리

        String contentDisposition = "inline; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    public ResponseEntity<Resource> viewPrivacy() throws FileNotFoundException, MalformedURLException {
        Path path = Paths.get(PDF_PRIVACY);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("PDF 파일을 찾을 수 없습니다.");
        }

        String encodedFileName = URLEncoder.encode("개인정보처리방침.pdf", StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"); // 공백 처리

        String contentDisposition = "inline; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);

    }
}


package com.sdde.neurodub.service;

import com.sdde.neurodub.config.AppProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class TtsService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final AppProperties appProperties;

    public TtsService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public byte[] preview(String text, String voice) {
        String url = appProperties.getTools().getTtsServiceUrl() + "/tts/preview";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(Map.of("text", text, "voice", voice), headers);
        ResponseEntity<ByteArrayResource> response = restTemplate.exchange(url, HttpMethod.POST, request, ByteArrayResource.class);
        return response.getBody() != null ? response.getBody().getByteArray() : new byte[0];
    }
}

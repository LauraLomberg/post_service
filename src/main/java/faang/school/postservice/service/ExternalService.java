package faang.school.postservice.service;

import faang.school.postservice.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class ExternalService {
    private final RestTemplate restTemplate;

    public boolean userExists(Long userId) {
        String url = "http://localhost/user/" + userId;
        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            throw new ExternalServiceException("Error checking if user exists", e);
        }
    }

    public boolean projectExists(Long projectId) {
        String url = "http://localhost/project/" + projectId;
        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            throw new ExternalServiceException("Error checking if project exists", e);
        }
    }
}

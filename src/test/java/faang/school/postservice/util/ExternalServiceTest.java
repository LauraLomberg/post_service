package faang.school.postservice.util;

import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.service.ExternalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExternalServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExternalService externalService;

    @Test
    public void userExists_True() {
        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        boolean result = externalService.userExists(1L);

        assertTrue(result);
    }


    @Test
    public void userExists_False() {
        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        boolean result = externalService.userExists(1L);

        assertFalse(result);
    }

    @Test
    public void userExists_ThrowsExternalServiceException() {
        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenThrow(new RuntimeException("Connection error"));

        assertThrows(ExternalServiceException.class, () -> externalService.userExists(1L));
    }

    @Test
    public void projectExists_True() {
        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        boolean result = externalService.projectExists(1L);

        assertTrue(result);
    }

    @Test
    public void projectExists_False() {
        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        boolean result = externalService.projectExists(1L);

        assertFalse(result);
    }

    @Test
    public void projectExists_ThrowsExternalServiceException() {
        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenThrow(new RuntimeException("Connection error"));

        assertThrows(ExternalServiceException.class, () -> externalService.projectExists(1L));
    }
}

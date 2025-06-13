package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<PostDto>> getFeed(@PathVariable Long userId) {
        List<PostDto> feed = feedService.getFeedForUser(userId);
        return ResponseEntity.ok(feed);
    }
}
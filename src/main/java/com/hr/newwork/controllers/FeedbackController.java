package com.hr.newwork.controllers;

import com.hr.newwork.data.dto.FeedbackDto;
import com.hr.newwork.data.dto.FeedbackRequestDto;
import com.hr.newwork.services.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @Operation(summary = "Create feedback", description = "Creates feedback for a user. If 'polish' is true, content is sent to HuggingFace for polishing.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Feedback created"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/users/{id}/feedback")
    public ResponseEntity<FeedbackDto> createFeedback(@PathVariable String id, @RequestBody FeedbackRequestDto feedbackRequest) {
        FeedbackDto created = feedbackService.createFeedback(id, feedbackRequest.getContent(), feedbackRequest.isPolish());
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "List feedback", description = "Lists feedback for a user, respecting visibility.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Feedback list returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/users/{id}/feedback")
    public ResponseEntity<List<FeedbackDto>> listFeedback(@PathVariable String id) {
        List<FeedbackDto> feedbackList = feedbackService.listFeedback(id);
        return ResponseEntity.ok(feedbackList);
    }

    @Operation(summary = "Edit feedback", description = "Edit feedback. Allowed for author, manager, or admin.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Feedback updated"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Feedback not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{feedbackId}")
    public ResponseEntity<FeedbackDto> editFeedback(@PathVariable String feedbackId, @RequestBody FeedbackRequestDto editRequest) {
        FeedbackDto updated = feedbackService.editFeedback(feedbackId, editRequest.getContent(), editRequest.isPolish());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Get feedback", description = "Fetch a single feedback by ID, including status and polished content.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Feedback returned"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Feedback not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{feedbackId}")
    public ResponseEntity<FeedbackDto> getFeedback(@PathVariable String feedbackId) {
        FeedbackDto feedback = feedbackService.getFeedback(feedbackId);
        return ResponseEntity.ok(feedback);
    }
}

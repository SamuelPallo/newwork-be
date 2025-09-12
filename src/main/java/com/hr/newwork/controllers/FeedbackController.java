package com.hr.newwork.controllers;

import com.hr.newwork.services.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping()
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
    public void createFeedback(@PathVariable UUID id, @RequestBody Object feedbackRequest) {
        feedbackService.createFeedback(id, null, false);
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
    public void listFeedback(@PathVariable UUID id) {
        feedbackService.listFeedback(id);
    }

    @Operation(summary = "Edit feedback", description = "Edit feedback. Allowed for author, manager, or admin.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Feedback updated"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Feedback not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/feedback/{feedbackId}")
    public void editFeedback(@PathVariable UUID feedbackId, @RequestBody Object editRequest) {
        feedbackService.editFeedback(feedbackId);
    }
}

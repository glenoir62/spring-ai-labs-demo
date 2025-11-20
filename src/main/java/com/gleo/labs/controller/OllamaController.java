package com.gleo.labs.controller;

import com.gleo.labs.service.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OllamaController {

  private final OllamaService ollamaService;

  @Autowired
  public OllamaController(OllamaService ollamaService) {
    this.ollamaService = ollamaService;
  }

  @GetMapping("/ai/call")
  public ResponseEntity<String> call(@RequestParam(value = "message") String message) {
    return ResponseEntity.ok(ollamaService.call(message));
  }

  @GetMapping("/ai/callWithContext")
  public ResponseEntity<String> callWithContext(@RequestParam(value = "message") String message,
                                                @RequestParam(value = "contextId", defaultValue = "none")
                                                String contextId) {
    return ResponseEntity.ok(ollamaService.callWithContext(message, contextId));
  }
}

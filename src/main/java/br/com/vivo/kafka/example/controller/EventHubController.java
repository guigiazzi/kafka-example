package br.com.vivo.kafka.example.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.vivo.kafka.example.dto.RequestMessageDTO;
import br.com.vivo.kafka.example.producer.EventHubProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/event-hub")
public class EventHubController {

    private final EventHubProducer producer;

    @PostMapping(value = "/message")
    public void sendMessage(@RequestBody RequestMessageDTO request) throws JsonProcessingException {
        log.info("Request received {}", new ObjectMapper().writeValueAsString(request));
        producer.publish(request.getMessage());
    }

}
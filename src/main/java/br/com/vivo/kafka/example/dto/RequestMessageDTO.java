package br.com.vivo.kafka.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestMessageDTO {

    private String message;

    public RequestMessageDTO() {

    }

}

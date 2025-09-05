package com.interviewer.dto.AI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestBody {
    private String model;
    private double temperature;
    private List<Message> messages;
}


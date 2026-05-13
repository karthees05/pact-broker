package com.example.contract;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostRequest(Integer id, Integer userId, String title, String body) {
}

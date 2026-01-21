package com.codetoelevate.SecurityApp.SecurityApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostDTO {

    private Long id;
    private String title;
    private String description;
}

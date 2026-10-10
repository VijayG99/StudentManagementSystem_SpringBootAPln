package org.example.studentmanagementsystem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PaginationRequestDto {

    @Min(0)
    private int page = 0;

    @Min(1) @Max(100)
    private int size = 10;

    @Pattern(regexp = "id|firstName|lastName|email|createdAt", message = "Invalid sort field")  // use your real fields
    private String sortBy = "firstName";

    @Pattern(regexp = "(?i)asc|desc", message = "Direction must be asc or desc")
    private String direction = "asc";
}
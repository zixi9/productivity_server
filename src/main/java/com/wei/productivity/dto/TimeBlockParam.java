package com.wei.productivity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class TimeBlockParam {
    @NotNull
    @NotEmpty
    private String category;
    private String description;
    @NotNull
    @NotEmpty
    private String target;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty(value = "begin_time")
    private LocalDateTime beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty(value = "end_time")
    private LocalDateTime endTime;

    @NotNull
    @Min(0)
    @JsonProperty(value = "plan_interval")
    private int planInterval;

    private String comment;
}

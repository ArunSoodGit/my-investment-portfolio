package com.sood.historical.data.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@Serdeable
public class Value {

    private final String datetime;
    private final String open;
    private final String high;
    private final String low;
    private final String close;
    private final String volume;
}

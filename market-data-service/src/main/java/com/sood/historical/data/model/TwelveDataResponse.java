package com.sood.historical.data.model;


import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Builder
@Getter
@Setter
@Serdeable
public class TwelveDataResponse {

    private final MetaData meta;
    private final List<Value> values;
    private final String status;
}

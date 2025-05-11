package nsuaiot.service.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsageRequest {
    @JsonProperty("t")
    private String date;

    @JsonProperty("value")
    private Double usage;
}

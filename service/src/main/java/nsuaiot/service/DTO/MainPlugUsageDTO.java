package nsuaiot.service.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class MainPlugUsageDTO {

    private String plugId;
    private String plugName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MainPlugUsageDTO that = (MainPlugUsageDTO) o;
        return Objects.equals(plugId, that.plugId) && Objects.equals(plugName, that.plugName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plugId, plugName);
    }

    public MainPlugUsageDTO(String plugId, String plugName) {
        this.plugId = plugId;
        this.plugName = plugName;
    }
}

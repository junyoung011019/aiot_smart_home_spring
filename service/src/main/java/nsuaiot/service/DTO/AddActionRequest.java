package nsuaiot.service.DTO;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;

import java.util.List;
import java.util.Map;

@Getter
public class AddActionRequest {

    private Long groupId;
    private List<Map<String,String>> devices;
}

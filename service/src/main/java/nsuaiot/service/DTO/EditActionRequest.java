package nsuaiot.service.DTO;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class EditActionRequest {

    private Long groupId;
    private List<Map<String,String>> devices;
}

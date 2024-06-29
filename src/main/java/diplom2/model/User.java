package diplom2.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private String name;
    private String password;
    private String email;
}

package role;

import java.util.List;

public interface RoleDefinition {

    String getName();
    String getDescription();
    List<String> getFlows();

}

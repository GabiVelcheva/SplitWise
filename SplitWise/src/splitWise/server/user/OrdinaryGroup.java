package splitWise.server.user;

import java.util.Set;

public class OrdinaryGroup extends Group {
    private String groupName;

    public OrdinaryGroup(String groupName, Set<User> friends) {
        super(friends);
        this.groupName = groupName;
    }

    public Set<User> getUsersOfGroup() {
        return users;
    }

    public String getName() {
        return groupName;
    }
}

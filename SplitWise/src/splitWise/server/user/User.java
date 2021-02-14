package splitWise.server.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class User implements Serializable {
    private static final String NO_NOTIFICATIONS_MESSAGE = "No notifications to show.";
    private static final String NOTIFICATIONS_MESSAGE = "*** Notifications ***";

    private String username;
    private String password;
    private List<String> notifications;
    private Map<String, Friend> friends;
    private Map<String, OrdinaryGroup> groups;
    // add member for currency

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.notifications = new ArrayList<>();
        this.friends = new HashMap<>();
        this.groups = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Friend> getFriends() {
        return friends;
    }

    public Map<String, OrdinaryGroup> getGroups() {
        return groups;
    }

    private void addNewFriend(User friend, Friend friendGroup) {
        friends.put(friend.getUsername(), friendGroup);
    }

    public void addNewFriend(User friend) {
        Set<User> usersFromGroup = new HashSet<>();
        usersFromGroup.add(friend);
        usersFromGroup.add(this);
        Friend newFriend = new Friend(usersFromGroup);
        friends.put(friend.getUsername(), newFriend);
        friend.addNewFriend(this, newFriend);
    }

    public void putGroup(String groupName, OrdinaryGroup group) {
        groups.put(groupName, group);
    }

    public String getNotifications() {
        if (notifications.isEmpty()) {
            return NO_NOTIFICATIONS_MESSAGE;
        }
        String newNotifications = notifications.stream().collect(Collectors.joining(System.lineSeparator()));
        notifications.clear();
        return NOTIFICATIONS_MESSAGE + System.lineSeparator() + newNotifications;
    }

    public void addNotification(String notification) {
        notifications.add(notification);
    }

    public boolean hasFriend(String friendName) {
        return friends.containsKey(friendName);
    }

    public boolean isFriendsListEmpty() {
        return friends.isEmpty();
    }

    public boolean isGroupListEmpty() {
        return groups.isEmpty();
    }
}



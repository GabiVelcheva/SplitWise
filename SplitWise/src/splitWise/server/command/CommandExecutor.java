package splitWise.server.command;

import static splitWise.server.command.CommandConstants.ADD_FRIEND_COMMAND;
import static splitWise.server.command.CommandConstants.ADD_PAYMENT_COMMAND;
import static splitWise.server.command.CommandConstants.ALREADY_TAKEN_USERNAME;
import static splitWise.server.command.CommandConstants.BETWEEN_YOU;
import static splitWise.server.command.CommandConstants.CREATED_SUCCESSFULLY;
import static splitWise.server.command.CommandConstants.CREATE_GROUP_COMMAND;
import static splitWise.server.command.CommandConstants.DISCONNECTED;
import static splitWise.server.command.CommandConstants.DISCONNECT_COMMAND;
import static splitWise.server.command.CommandConstants.FRIENDS;
import static splitWise.server.command.CommandConstants.FRIEND_ADDED;
import static splitWise.server.command.CommandConstants.FRIEND_TOTALS_COMMAND;
import static splitWise.server.command.CommandConstants.GET_STATUS_COMMAND;
import static splitWise.server.command.CommandConstants.GROUP;
import static splitWise.server.command.CommandConstants.GROUPS;
import static splitWise.server.command.CommandConstants.GROUP_EXISTS;
import static splitWise.server.command.CommandConstants.GROUP_MEMBERS;
import static splitWise.server.command.CommandConstants.GROUP_TOTALS_COMMAND;
import static splitWise.server.command.CommandConstants.HELP_COMMAND;
import static splitWise.server.command.CommandConstants.HISTORY_COMMAND;
import static splitWise.server.command.CommandConstants.INVALID_USERNAME_PASSWORD;
import static splitWise.server.command.CommandConstants.LOGGED_IN_ALREADY_AS;
import static splitWise.server.command.CommandConstants.LOGIN_COMMAND;
import static splitWise.server.command.CommandConstants.LOGOUT_COMMAND;
import static splitWise.server.command.CommandConstants.NO_SUCH_GROUP;
import static splitWise.server.command.CommandConstants.NOT_LOGGED_IN;
import static splitWise.server.command.CommandConstants.NOT_REGISTERED_USER;
import static splitWise.server.command.CommandConstants.OK;
import static splitWise.server.command.CommandConstants.OPENING_BRACKET;
import static splitWise.server.command.CommandConstants.OWES_YOU;
import static splitWise.server.command.CommandConstants.REGISTER_COMMAND;
import static splitWise.server.command.CommandConstants.SEPARATOR;
import static splitWise.server.command.CommandConstants.SOMEONE;
import static splitWise.server.command.CommandConstants.SPLITTED;
import static splitWise.server.command.CommandConstants.SPLIT_COMMAND;
import static splitWise.server.command.CommandConstants.SPLIT_GROUP_ARGUMENTS;
import static splitWise.server.command.CommandConstants.SPLIT_GROUP_COMMAND;
import static splitWise.server.command.CommandConstants.SUCCESSFULLY_LOGGED_IN;
import static splitWise.server.command.CommandConstants.SUCCESSFULLY_LOGGED_OUT;
import static splitWise.server.command.CommandConstants.SUCCESSFULLY_REGISTERED;
import static splitWise.server.command.CommandConstants.TO_LOGIN_ANOTHER;
import static splitWise.server.command.CommandConstants.TO_REGISTER_OTHER;
import static splitWise.server.command.CommandConstants.UNKNOWN_COMMAND;
import static splitWise.server.command.CommandConstants.USER;
import static splitWise.server.command.CommandConstants.USERNAME;
import static splitWise.server.command.CommandConstants.USER_ALREADY_FRIEND;
import static splitWise.server.command.CommandConstants.USER_NOT_FRIEND_FIRST;
import static splitWise.server.command.CommandConstants.USER_NOT_FRIEND_SECOND;
import static splitWise.server.command.CommandConstants.YOU_OWE;

import splitWise.server.expenses.Expenses;
import splitWise.server.expenses.Split;
import splitWise.server.files.HistoryFileHandler;
import splitWise.server.files.RegisteredUsersFileHandler;
import splitWise.server.user.Friend;
import splitWise.server.user.Group;
import splitWise.server.user.OrdinaryGroup;
import splitWise.server.user.User;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class CommandExecutor {

    private Map<String, User> registeredUsers;
    private Map<String, SocketChannel> activeUsersByUsernames;
    private Set<SocketChannel> connectedClients;
    private static final String[] COMMANDS_MANUAL = {"register <username> <password>",
            "This command is used for registering a new user.", System.lineSeparator(), "login <username> <password>",
            "This command is used for logging in.", System.lineSeparator(), "add-friend <username>",
            "This command is used for adding user with name username to the friend list. The user should be " +
                    "registered.",
            System.lineSeparator(),
            "create-group <group_name> <username> <username> ... <username>",
            "This command is used for creating a group with name <group_name> and members <username> ... <username>.",
            "The members of the group should be at least 3, otherwise the group will not be created.",
            "The creator of " +
                    "the group is added implicit in the group members, you don't need to parse your name as an " +
                    "argument",
            "All of the users must be registered and must be in your friend list.", System.lineSeparator(),
            "split <amount> <username> <reason>",
            "This command is used for splitting money with user with name username.",
            "If the current user owes money to the other user, as much as possible",
            "balances are updated before adding a new split. The other user should be a friend",
            "with the current user.", System.lineSeparator(),
            "split-group <amount> <group_name> <reason>",
            "This command is used for splitting money with all of the members in the group with name group_name. The " +
                    "money are divided equally",
            "among the members. If the current user owes money to one of the other users, as much as possible",
            "balances are updated before adding a new split.", System.lineSeparator(),
            "payed <amount> <username> [<group_name>]",
            "This command is used for adding payment from a user with name username.",
            "The group_name argument is optional. If it is present, balances in the group are updated,",
            "otherwise splits splitted as friends are updated.", System.lineSeparator(), "get-status",
            "This command is used for printing the current status of the user.",
            System.lineSeparator(), "history",
            "This command is used for printing the history of all payments that the user has made.",
            System.lineSeparator(), "logout", "This command is used for logging out.", System.lineSeparator(),
            "help", "Displays a manual of all supported commands.", System.lineSeparator()};

    public CommandExecutor() {
        this.registeredUsers = new HashMap<>();
        this.activeUsersByUsernames = new HashMap<>();
        this.connectedClients = new HashSet<>();
    }

    public void addToConnected(SocketChannel socketChannel) {
        connectedClients.add(socketChannel);
    }


    public boolean hasUser(String user) {
        return registeredUsers.containsKey(user);
    }

    public void loadRegisteredUsers() {
        registeredUsers = RegisteredUsersFileHandler.loadRegisteredUsers();
    }

    private boolean isActive(SocketChannel socketChannel) {
        return activeUsersByUsernames.containsValue(socketChannel);
    }

    private boolean isRegistered(String username) {
        return registeredUsers.containsKey(username);
    }

    private boolean isPasswordCorrect(String username, String password) {
        return registeredUsers.get(username).getPassword().equals(password);
    }


    public String execute(Command cmd, SocketChannel socketChannel) {
        String[] message = cmd.message();
        String validation = CommandValidator.validate(cmd);
        if (!validation.equals(OK)) {
            return validation + System.lineSeparator();
        }
        return switch (cmd.command()) {
            case REGISTER_COMMAND -> register(message[1], message[2], socketChannel);
            case LOGIN_COMMAND -> login(message[1], message[2], socketChannel);
            case LOGOUT_COMMAND -> logout(socketChannel);
            case ADD_FRIEND_COMMAND -> addFriend(message[1], message, socketChannel);
            case CREATE_GROUP_COMMAND -> createGroup(message[1], message, socketChannel);
            case SPLIT_COMMAND -> split(Double.parseDouble(message[1]), message[2], message, socketChannel);
            case SPLIT_GROUP_COMMAND -> splitGroup(Double.parseDouble(message[1]), message[2], message, socketChannel);
            case GET_STATUS_COMMAND -> getStatus(socketChannel);
            case ADD_PAYMENT_COMMAND -> payment(Double.parseDouble(message[1]), message[2], message, socketChannel);
            case HISTORY_COMMAND -> history(socketChannel);
            case FRIEND_TOTALS_COMMAND -> friendTotals(message[1], socketChannel);
            case GROUP_TOTALS_COMMAND -> groupTotals(message[1], socketChannel);
            case HELP_COMMAND -> help();
            case DISCONNECT_COMMAND -> disconnect(socketChannel);
            default -> UNKNOWN_COMMAND + System.lineSeparator();
        };
    }

    private String register(String username, String password, SocketChannel socketChannel) {
        if (isActive(socketChannel)) {
            String userName = getKeyByValue(activeUsersByUsernames, socketChannel);
            return LOGGED_IN_ALREADY_AS + userName + TO_REGISTER_OTHER + System.lineSeparator();
        }
        if (isRegistered(username)) {
            return USERNAME + username + ALREADY_TAKEN_USERNAME + System.lineSeparator();
        } else {
            User newUser = new User(username, password);
            registeredUsers.put(username, newUser);
            RegisteredUsersFileHandler.saveRegisteredUsersToFile(registeredUsers);
            return USERNAME + username + SUCCESSFULLY_REGISTERED + System.lineSeparator();
        }
    }

    private String login(String username, String password, SocketChannel socketChannel) {
        if (isActive(socketChannel)) {
            String userName = getKeyByValue(activeUsersByUsernames, socketChannel);
            return LOGGED_IN_ALREADY_AS + userName + TO_LOGIN_ANOTHER + System.lineSeparator();
        }
        if (isRegistered(username)) {
            if (isPasswordCorrect(username, password)) {
                activeUsersByUsernames.put(username, socketChannel);
                User user = registeredUsers.get(username);
                return USER + username + SUCCESSFULLY_LOGGED_IN + System.lineSeparator() +
                        user.getNotifications() + System.lineSeparator();
            } else {
                return INVALID_USERNAME_PASSWORD + System.lineSeparator();
            }
        } else {
            return INVALID_USERNAME_PASSWORD + System.lineSeparator();
        }
    }

    private String logout(SocketChannel socketChannel) {
        if (isActive(socketChannel)) {
            activeUsersByUsernames.remove(getKeyByValue(activeUsersByUsernames, socketChannel));
            return SUCCESSFULLY_LOGGED_OUT + System.lineSeparator();
        } else {
            return NOT_LOGGED_IN + System.lineSeparator();
        }
    }

    private String addFriend(String friendName, String[] message, SocketChannel socketChannel) {
        if (!isActive(socketChannel)) {
            return NOT_LOGGED_IN + System.lineSeparator();
        } else {
            User user = registeredUsers.get(getKeyByValue(activeUsersByUsernames, socketChannel));
            if (!isRegistered(friendName)) {
                return NOT_REGISTERED_USER + System.lineSeparator();
            } else {
                if (friendName.equals(user.getUsername())) {
                    return "You can't add yourself as friend." + System.lineSeparator();
                }
                if (user.getFriends() != null && user.hasFriend(friendName)) {
                    return USER_ALREADY_FRIEND + System.lineSeparator();
                } else {
                    User friend = registeredUsers.get(friendName);
                    user.addNewFriend(friend);
                    return OPENING_BRACKET + friendName + SEPARATOR + FRIEND_ADDED + System.lineSeparator();
                }
            }
        }
    }

    private Set<User> getUsersByName(Set<String> userNames) {
        Set<User> setToReturn = new HashSet<>();
        for (String name : userNames) {
            setToReturn.add(registeredUsers.get(name));
        }
        return setToReturn;
    }

    private boolean usersAreRegistered(Set<String> listFriendsName) {
        boolean flag = true;
        for (String name : listFriendsName) {
            if (!isRegistered(name)) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private boolean usersAreFriends(User userCreator, Set<String> listFriendsName) {
        boolean flag = true;
        for (String name : listFriendsName) {
            if (!userCreator.hasFriend(name)) {
                flag = false;
            }
        }
        return flag;
    }

    private boolean groupExists(User userCreator, String groupName, Set<User> usersFromGroup) {
        boolean flag = false;
        if (userCreator.getGroups() != null && userCreator.getGroups().containsKey(groupName)) {
            if (userCreator.getGroups().get(groupName).getUsersOfGroup().equals(usersFromGroup)) {
                flag = true;
            }
        }
        return flag;
    }

    private boolean isCreatorInTheList(String creator, Set<String> listFriendsName) {
        boolean flag = false;
        for (String friend : listFriendsName) {
            if (friend.equals(creator)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private String createGroup(String groupName, String[] message, SocketChannel socketChannel) {
        if (!isActive(socketChannel)) {
            return NOT_LOGGED_IN + System.lineSeparator();
        } else {
            User userCreator = registeredUsers.get(getKeyByValue(activeUsersByUsernames, socketChannel));
            Set<String> listFriendsName = Arrays.stream(message).skip(2).collect(Collectors.toSet());
            if (isCreatorInTheList(userCreator.getUsername(), listFriendsName)) {
                return "You don't need to add you in the list of friends" + System.lineSeparator();
            }
            if (!usersAreRegistered(listFriendsName)) {
                return NOT_REGISTERED_USER + System.lineSeparator();
            }
            if (!usersAreFriends(userCreator, listFriendsName)) {
                return SOMEONE + USER_NOT_FRIEND_SECOND + System.lineSeparator();
            }
            Set<User> usersFromGroup = getUsersByName(listFriendsName);
            usersFromGroup.add(userCreator);
            if (groupExists(userCreator, groupName, usersFromGroup)) {
                return GROUP_EXISTS + System.lineSeparator();
            }
            OrdinaryGroup newGroup = new OrdinaryGroup(groupName, usersFromGroup);
            for (User user : usersFromGroup) {
                user.putGroup(groupName, newGroup);
            }
            return GROUP + groupName + CREATED_SUCCESSFULLY + System.lineSeparator();
        }
    }

    private void updateBalance(User paidBy, Double amount, List<Split> splits, String description, Group group) {
        int totalSplits = splits.size() + 1;
        double splitAmount = (amount * 100 / totalSplits) / 100;
        for (Split split : splits) {
            split.setAmount(splitAmount);
        }
        Expenses expenses = new Expenses(paidBy, amount, splits, description);
        group.addExpense(expenses);
        updateBalances(paidBy, expenses.getSplits(), description, group);
    }

    private void updateBalances(User paidBy, List<Split> splits, String description, Group group) {
        for (Split split : splits) {
            User friend = split.getPaidTo();
            if (group instanceof OrdinaryGroup) {
                friend.addNotification(
                        "* " + ((OrdinaryGroup) group).getName() + System.lineSeparator() + ":" + YOU_OWE +
                                paidBy.getUsername() + SEPARATOR + split.getAmount() + " [ " + description + " ].");
            } else if (group instanceof Friend) {
                friend.addNotification(
                        YOU_OWE + paidBy.getUsername() + SEPARATOR + split.getAmount() + " [ " + description + " ].");
            }

            String paidTo = friend.getUsername();
            Map<String, Double> balances = group.getDebts().get(paidBy.getUsername());
            if (!balances.containsKey(paidTo)) {
                balances.put(paidTo, 0.0);
            }
            balances.put(paidTo, balances.get(paidTo) + split.getAmount());

            balances = group.getDebts().get(paidTo);
            if (!balances.containsKey(paidBy.getUsername())) {
                balances.put(paidBy.getUsername(), 0.0);
            }
            balances.put(paidBy.getUsername(), balances.get(paidBy.getUsername()) - split.getAmount());
        }
    }

    private String split(Double amount, String friendName, String[] message, SocketChannel socketChannel) {
        if (!isActive(socketChannel)) {
            return NOT_LOGGED_IN + System.lineSeparator();
        } else {
            User paidBy = registeredUsers.get(getKeyByValue(activeUsersByUsernames, socketChannel));
            if (!isRegistered(friendName)) {
                return NOT_REGISTERED_USER + System.lineSeparator();
            }
            User friend = registeredUsers.get(friendName);
            if (!paidBy.hasFriend(friendName)) {
                return USER_NOT_FRIEND_FIRST + friendName + USER_NOT_FRIEND_SECOND + System.lineSeparator();
            } else {
                String description = Arrays.stream(message).skip(3).collect(Collectors.joining());
                List<Split> splits = new ArrayList<>();
                splits.add(new Split(friend));
                Friend friendGroup = paidBy.getFriends().get(friendName);
                friendGroup.addSpending(paidBy.getUsername(), amount);
                updateBalance(paidBy, amount, splits, description, friendGroup);
            }
        }
        return SPLITTED + amount + BETWEEN_YOU + friendName + System.lineSeparator();
    }


    private String splitGroup(double amount, String groupName, String[] message, SocketChannel socketChannel) {
        if (message.length < 4) {
            return SPLIT_GROUP_ARGUMENTS + System.lineSeparator();
        }
        if (!isActive(socketChannel)) {
            return NOT_LOGGED_IN + System.lineSeparator();
        }
        User paidBy = registeredUsers.get(getKeyByValue(activeUsersByUsernames, socketChannel));
        if (!paidBy.getGroups().containsKey(groupName)) {
            return NO_SUCH_GROUP + System.lineSeparator();
        }
        OrdinaryGroup group = paidBy.getGroups().get(groupName);
        group.addSpending(paidBy.getUsername(), amount);
        Set<User> listUsers = group.getUsersOfGroup();

        String description = Arrays.stream(message).skip(3).collect(Collectors.joining(" "));
        List<Split> splits = new ArrayList<>();
        for (User userGroup : listUsers) {
            if (!userGroup.getUsername().equals(paidBy.getUsername())) {
                splits.add(new Split(userGroup));
            }
        }
        updateBalance(paidBy, amount, splits, description, group);
        return SPLITTED + amount + BETWEEN_YOU + GROUP_MEMBERS + groupName + System.lineSeparator();
    }

    private List<String> getStatusFriends(User user, List<String> response) {
        if (user.isFriendsListEmpty()) {
            return response;
        }
        Map<String, Friend> friends = user.getFriends();
        Map<String, Map<String, Double>> friendDebts;
        Map<String, Double> balance;
        response.add(FRIENDS);
        for (Map.Entry<String, Friend> friend : friends.entrySet()) {
            friendDebts = friend.getValue().getDebts();
            balance = friendDebts.get(user.getUsername());
            for (Map.Entry<String, Double> entry : balance.entrySet()) {
                String friendName = entry.getKey();
                Double amount = entry.getValue();
                if (amount > 0) {
                    response.add("* " + friendName + OWES_YOU + amount);
                } else if (amount < 0) {
                    response.add("* " + friendName + ": " + YOU_OWE + amount * (-1));
                }
            }
        }
        return response;
    }

    private List<String> getStatusGroups(User user, List<String> response) {
        if (user.isGroupListEmpty()) {
            return response;
        }
        response.add(GROUPS);
        Map<String, OrdinaryGroup> groups = user.getGroups();
        String groupName;
        OrdinaryGroup group;
        Map<String, Map<String, Double>> groupDebts;
        Map<String, Double> balance;
        for (Map.Entry<String, OrdinaryGroup> entry : groups.entrySet()) {
            groupName = entry.getKey();
            group = entry.getValue();
            response.add("* " + groupName);
            groupDebts = group.getDebts();
            balance = groupDebts.get(user.getUsername());
            for (Map.Entry<String, Double> entryGroup : balance.entrySet()) {
                String friendName = entryGroup.getKey();
                Double amount = entryGroup.getValue();
                if (amount > 0) {
                    response.add("- " + friendName + OWES_YOU + amount);
                } else if (amount < 0) {
                    response.add("- " + friendName + ": " + YOU_OWE + amount * (-1));
                }
            }
        }
        return response;
    }

    private String getStatus(SocketChannel socketChannel) {
        if (!activeUsersByUsernames.containsValue(socketChannel)) {
            return NOT_LOGGED_IN + System.lineSeparator();
        }
        User user = registeredUsers.get(getKeyByValue(activeUsersByUsernames, socketChannel));
        List<String> response = new ArrayList<>();
        response = getStatusFriends(user, response);
        response = getStatusGroups(user, response);
        if(response.isEmpty()) {
            return "No status to show" + System.lineSeparator();
        }
        return response.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    private String payment(Double amount, String friendName, String[] message, SocketChannel socketChannel) {
        if (!isActive(socketChannel)) {
            return NOT_LOGGED_IN + System.lineSeparator();
        }
        if (!isRegistered(friendName)) {
            return NOT_REGISTERED_USER + System.lineSeparator();
        }
        User user = registeredUsers.get(getKeyByValue(activeUsersByUsernames, socketChannel));
        if (!user.hasFriend(friendName)) {
            return USER_NOT_FRIEND_FIRST + friendName + USER_NOT_FRIEND_SECOND + System.lineSeparator();
        }
        if (message.length == 3) {
            return addPaymentFriend(user, amount, friendName);
        } else if (message.length == 4) {
            String groupName = message[3];
            return addPaymentGroup(user, amount, friendName, groupName);
        }
        return "";
    }

    private String addPayment(String userName, Double amount, String friendName, Group group) {
        Map<String, Double> balances = group.getDebts().get(userName);
        if (!balances.containsKey(friendName)) {
            return "[ " + friendName + " doesn't owe you money ]" + System.lineSeparator();
        }
        Double debtFriend = balances.get(friendName);
        if (debtFriend < 0) {
            return "[ " + friendName + " doesn't owe you money ]" + System.lineSeparator();
        } else {
            if (debtFriend - amount < 0) {
                return "[ " + friendName + " doesn't owe you so much money ]" + System.lineSeparator();
            }
            User friend = registeredUsers.get(friendName);
            friend.addNotification(userName + "  approved your payment " + amount);
            addPaymentToHistoryFile(friend, userName, amount);
            balances.put(friendName, debtFriend - amount);

            balances = group.getDebts().get(friendName);
            balances.put(userName, balances.get(userName) + amount);
            return "[ " + friendName + " payed you " + amount + " ]" + System.lineSeparator();
        }
    }

    private void addPaymentToHistoryFile(User friend, String userName, Double amount) {
        HistoryFileHandler.addPayment(friend, userName, amount);
    }

    private String addPaymentFriend(User user, Double amount, String friendName) {
        Friend friendGroup = user.getFriends().get(friendName);
        String userName = user.getUsername();
        return addPayment(userName, amount, friendName, friendGroup);
    }


    private String addPaymentGroup(User user, Double amount, String friendName, String groupName) {
        if (!user.getGroups().containsKey(groupName)) {
            return "[ Group with name: " + groupName + " doesn't exist. ]" + System.lineSeparator();
        }
        OrdinaryGroup group = user.getGroups().get(groupName);
        if (!group.hasUser(friendName)) {
            return "[ User with name:" + friendName + " is not in the group: " + group.getName() + " ]" +
                    System.lineSeparator();
        }
        return addPayment(user.getUsername(), amount, friendName, group);
    }


    private String friendTotals(String friendName, SocketChannel socketChannel) {
        if (!isActive(socketChannel)) {
            return NOT_LOGGED_IN + System.lineSeparator();
        } else {
            User user = registeredUsers.get(getKeyByValue(activeUsersByUsernames, socketChannel));
            if (!isRegistered(friendName)) {
                return NOT_REGISTERED_USER + System.lineSeparator();
            }
            if (!user.hasFriend(friendName)) {
                return USER_NOT_FRIEND_FIRST + friendName + USER_NOT_FRIEND_SECOND + System.lineSeparator();
            }
            Friend friend = user.getFriends().get(friendName);
            Map<String, Double> totals = friend.getTotals();
            Double totalForGroup = 0.0;
            for (Map.Entry<String, Double> entry : totals.entrySet()) {
                totalForGroup += entry.getValue();
            }
            return "Total spending with " + friendName + " :" + totalForGroup + System.lineSeparator() +
                    "Total you paid for: " +
                    totals.get(user.getUsername()) + System.lineSeparator();
        }
    }

    private String groupTotals(String groupName, SocketChannel socketChannel) {
        if (!isActive(socketChannel)) {
            return NOT_LOGGED_IN + System.lineSeparator();
        }
        User user = registeredUsers.get(getKeyByValue(activeUsersByUsernames, socketChannel));
        if (!user.getGroups().containsKey(groupName)) {
            return NO_SUCH_GROUP + System.lineSeparator();
        }
        Group group = user.getGroups().get(groupName);
        Map<String, Double> totals = group.getTotals();
        Double totalForGroup = 0.0;
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            totalForGroup += entry.getValue();
        }
        return "Total group spending: " + totalForGroup + System.lineSeparator() + "Total you paid for: " +
                totals.get(user.getUsername()) + System.lineSeparator();
    }

    private String history(SocketChannel socketChannel) {
        User user = registeredUsers.get(getKeyByValue(activeUsersByUsernames, socketChannel));
        return HistoryFileHandler.loadHistoryOfPayments(user);
    }

    private String help() {
        return Arrays.stream(COMMANDS_MANUAL).collect(Collectors.joining(System.lineSeparator()));
    }

    private <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void saveToFile() {
        RegisteredUsersFileHandler.saveRegisteredUsersToFile(registeredUsers);
    }

    public String disconnect(SocketChannel socketChannel) {
        if (activeUsersByUsernames.containsValue(socketChannel)) {
            activeUsersByUsernames.remove(getKeyByValue(activeUsersByUsernames, socketChannel));
        }
        connectedClients.remove(socketChannel);
        return DISCONNECTED + System.lineSeparator();
    }

    public void disconnectEveryone() {
        for (SocketChannel socketChannel : connectedClients) {
            if (activeUsersByUsernames.containsValue(socketChannel)) {
                activeUsersByUsernames.remove(getKeyByValue(activeUsersByUsernames, socketChannel));
            }
            connectedClients.remove(socketChannel);
        }
    }
}

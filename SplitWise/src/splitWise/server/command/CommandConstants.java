package splitWise.server.command;

public class CommandConstants {
    static final String REGISTER_COMMAND = "register";
    static final String DISCONNECT_COMMAND = "disconnect";
    static final String LOGIN_COMMAND = "login";
    static final String LOGOUT_COMMAND = "logout";
    static final String ADD_FRIEND_COMMAND = "add-friend";
    static final String CREATE_GROUP_COMMAND = "create-group";
    static final String SPLIT_COMMAND = "split";
    static final String SPLIT_GROUP_COMMAND = "split-group";
    static final String GET_STATUS_COMMAND = "get-status";
    static final String ADD_PAYMENT_COMMAND = "payed";
    static final String FRIEND_TOTALS_COMMAND = "friend-totals";
    static final String GROUP_TOTALS_COMMAND = "group-totals";
    static final String HISTORY_COMMAND = "history";
    static final String HELP_COMMAND = "help";
    static final String UNKNOWN_COMMAND = "[ Unknown command ]";

    static final String ALREADY_TAKEN_USERNAME = " is already taken, select another one ]";
    static final String ADDING_YOURSELF = "You can't add yourself as friend.";
    static final String ADDING_YOURSELF_GROUP = "You don't need to add yourself in the list of group members";
    static final String APPROVED = "  approved your payment ";
    static final String CREATED_SUCCESSFULLY = " is created successfully ]";
    static final String DISCONNECTED = "[ Disconnected from server ]";
    static final String DOESNT_OWE_YOU  = " doesn't owe you money ]";
    static final String DOESNT_OWE_YOU_SO_MUCH  = " doesn't owe you so much money ]";
    static final String GROUP = "[ group: ";
    static final String INVALID_USERNAME_PASSWORD = "[ Invalid username/password combination ]";
    static final String SUCCESSFULLY_LOGGED_OUT = "[ Successfully logged out ]";
    static final String LOGGED_IN_ALREADY_AS = "[ You are logged in already as ";
    static final String NO_STATUS = "No status to show";
    static final String NOT_LOGGED_IN = "[ You are not logged in ]";
    static final String NOT_REGISTERED_USER = "[ This user is not registered ]";
    static final String OPENING_BRACKET = "[ ";
    static final String PAYED_YOU = " payed you ";
    static final String USER_ALREADY_FRIEND = "[ This user is already in you friend list ]";
    static final String FRIEND_ADDED = "added successfully as a friend ]";
    static final String GROUP_EXISTS = "[ A group with the same name and participants already exists ]";
    static final String SEPARATOR = " ";
    static final String SPLIT_ARGUMENTS_MESSAGE =
            "[ You are using the split command with less arguments : split <amount> <username> <reason_for_payment> ]";
    static final String SPLIT_GROUP_ARGUMENTS =
            "[ You are using the split-group command with less arguments : split-group <amount> <group-name> " +
                    "<reason_for_payment> ]";
    static final String SUCCESSFULLY_REGISTERED = " successfully registered ]";
    static final String SUCCESSFULLY_LOGGED_IN = " successfully logged in ]";
    static final String SOMEONE = "[ Someone";
    static final String SPLIT = "Have split ";
    static final String BETWEEN_YOU = "between you and ";
    static final String GROUP_MEMBERS = "the group members of ";
    static final String USER = "[ User ";
    static final String USERNAME = "[ Username ";
    static final String USER_NOT_FRIEND_FIRST = "[ This user: ";
    static final String USER_NOT_FRIEND_SECOND = " is not in your friend list, please add him first ]";
    static final String NO_SUCH_GROUP = "[ You are not in a group with that name ]";
    static final String TO_REGISTER_OTHER = " , to register other user, first logout ]";
    static final String TO_LOGIN_ANOTHER = " , to login as other user, first logout ]";
    static final String YOU_OWE = "You owe ";
    static final String OK = "Everything's ok";
    static final String FRIENDS = "Friends:";
    static final String OWES_YOU = ": Owes you ";
    static final String GROUPS = "Groups:";

    static final String INVALID_NUMBER_ARGUMENTS_MESSAGE =
            "The number of arguments for the current command is invalid";
}

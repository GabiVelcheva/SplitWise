package splitWise.server.command;

import static splitWise.server.command.CommandConstants.ADD_FRIEND_COMMAND;
import static splitWise.server.command.CommandConstants.ADD_PAYMENT_COMMAND;
import static splitWise.server.command.CommandConstants.CREATE_GROUP_COMMAND;
import static splitWise.server.command.CommandConstants.FRIEND_TOTALS_COMMAND;
import static splitWise.server.command.CommandConstants.GROUP_TOTALS_COMMAND;
import static splitWise.server.command.CommandConstants.INVALID_NUMBER_ARGUMENTS_MESSAGE;
import static splitWise.server.command.CommandConstants.LOGIN_COMMAND;
import static splitWise.server.command.CommandConstants.LOGOUT_COMMAND;
import static splitWise.server.command.CommandConstants.OK;
import static splitWise.server.command.CommandConstants.REGISTER_COMMAND;
import static splitWise.server.command.CommandConstants.SPLIT_ARGUMENTS_MESSAGE;
import static splitWise.server.command.CommandConstants.SPLIT_COMMAND;
import static splitWise.server.command.CommandConstants.SPLIT_GROUP_COMMAND;

public class CommandValidator {
    private static final int REGISTER_ARGUMENTS = 3;
    private static final int LOGIN_ARGUMENTS = 3;
    private static final int ADD_FRIEND_ARGUMENTS = 2;
    private static final int MIN_CREATE_GROUP_ARGUMENTS = 3;
    private static final int SPLIT_ARGUMENTS = 4;
    private static final int SPLIT_GROUP_ARGUMENTS = 4;
    private static final int TOTALS_ARGUMENTS = 2;
    private static final int LOGOUT_ARGUMENTS = 1;

    private static final String NEED_VALUE = "The second argument must be a numeric value";


    public static String validate(Command cmd) {
        String[] message = cmd.message();
        return switch (cmd.command()) {
            case REGISTER_COMMAND -> validateRegister(message);
            case LOGIN_COMMAND -> validateLogin(message);
            case LOGOUT_COMMAND -> validateLogout(message);
            case ADD_FRIEND_COMMAND -> validateAddFriend(message);
            case CREATE_GROUP_COMMAND -> validateCreateGroup(message);
            case SPLIT_COMMAND -> validateSplit(message);
            case SPLIT_GROUP_COMMAND -> validateSplitGroup(message);
            case ADD_PAYMENT_COMMAND -> validatePayment(message);
            case FRIEND_TOTALS_COMMAND -> validateFriendTotals(message);
            case GROUP_TOTALS_COMMAND -> validateGroupTotals(message);
            default -> OK;
        };
    }

    private static String validateRegister(String[] message) {
        if (message.length != REGISTER_ARGUMENTS) {
            return INVALID_NUMBER_ARGUMENTS_MESSAGE;
        }
        return OK;
    }

    private static String validateLogin(String[] message) {
        if (message.length != LOGIN_ARGUMENTS) {
            return INVALID_NUMBER_ARGUMENTS_MESSAGE;
        }
        return OK;
    }

    private static String validateLogout(String[] message) {
        if (message.length != LOGOUT_ARGUMENTS) {
            return INVALID_NUMBER_ARGUMENTS_MESSAGE;
        }
        return OK;
    }

    private static String validateAddFriend(String[] message) {
        if (message.length != ADD_FRIEND_ARGUMENTS) {
            return INVALID_NUMBER_ARGUMENTS_MESSAGE;
        }
        return OK;
    }

    private static String validateCreateGroup(String[] message) {
        if (message.length < MIN_CREATE_GROUP_ARGUMENTS) {
            return INVALID_NUMBER_ARGUMENTS_MESSAGE;
        }
        return OK;
    }


    private static String validateSplit(String[] message) {
        if (message.length < SPLIT_ARGUMENTS) {
            return SPLIT_ARGUMENTS_MESSAGE;
        }
        try {
            double amount = Double.parseDouble(message[1]);
            return OK;
        } catch (NumberFormatException e) {
            return NEED_VALUE;
        }
    }

    private static String validateSplitGroup(String[] message) {
        if (message.length < SPLIT_GROUP_ARGUMENTS) {
            return INVALID_NUMBER_ARGUMENTS_MESSAGE;
        }
        try {
            double amount = Double.parseDouble(message[1]);
            return OK;
        } catch (NumberFormatException e) {
            return NEED_VALUE;
        }
    }


    private static String validatePayment(String[] message) {
        if ((message.length != 3) && (message.length != 4)) {
            return INVALID_NUMBER_ARGUMENTS_MESSAGE;
        }
        try {
            double amount = Double.parseDouble(message[1]);
            return OK;
        } catch (NumberFormatException e) {
            return NEED_VALUE;
        }
    }

    private static String validateGroupTotals(String[] message) {
        if (message.length != TOTALS_ARGUMENTS) {
            return INVALID_NUMBER_ARGUMENTS_MESSAGE;
        }
        return OK;
    }

    private static String validateFriendTotals(String[] message) {
        if (message.length != TOTALS_ARGUMENTS) {
            return INVALID_NUMBER_ARGUMENTS_MESSAGE;
        }
        return OK;
    }
}

package splitWise.server.user;

import splitWise.server.expenses.Expenses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Group implements Serializable {
    protected Set<User> users;
    protected List<Expenses> expensesList;
    protected Map<String, Map<String, Double>> debts;
    protected Map<String, Double> groupTotals;

    public Group(Set<User> users) {
        this.users = new HashSet<>();
        this.expensesList = new ArrayList<>();
        this.debts = new HashMap<>();
        groupTotals = new HashMap<>();
        for (User user : users) {
            addUser(user);
            groupTotals.put(user.getUsername(), 0.0);
        }
    }

    public Map<String, Map<String, Double>> getDebts() {
        return debts;
    }

    public void addUser(User user) {
        users.add(user);
        debts.put(user.getUsername(), new HashMap<>());
    }

    public void addExpense(Expenses exp) {
        this.expensesList.add(exp);
    }

    public boolean hasUser(String userName) {
        for (User user : users) {
            if (user.getUsername().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    public void addSpending(String user, Double amount) {
        groupTotals.put(user, groupTotals.get(user) + amount);
    }

    public Map<String, Double> getTotals() {
        return groupTotals;
    }
}

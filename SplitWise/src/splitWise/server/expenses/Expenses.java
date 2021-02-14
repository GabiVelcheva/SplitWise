package splitWise.server.expenses;

import splitWise.server.user.User;

import java.io.Serializable;
import java.util.List;

public class Expenses implements Serializable {
    private User paidBy;
    private Double amount;
    private List<Split> splits;
    private String description;

    public Expenses(User paidBy, Double amount, List<Split> splits, String description) {
        this.paidBy = paidBy;
        this.amount = amount;
        this.splits = splits;
        this.description = description;
    }

    public List<Split> getSplits() {
        return splits;
    }
}

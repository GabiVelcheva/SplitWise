package splitWise.server.expenses;

import splitWise.server.user.User;

import java.io.Serializable;

public class Split implements Serializable {
    private User paidTo;
    private Double amount;

    public Split(User paidTo) {
        this.paidTo = paidTo;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public User getPaidTo() {
        return paidTo;
    }

    public Double getAmount() {
        return amount;
    }
}


import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction {

    private int id;
    private int senderId;
    private int receiverId;
    private BigDecimal amount;
    private String type;
    private Timestamp transactionDate;

    public Transaction(int id, int senderId, int receiverId,
                       BigDecimal amount, String type,
                       Timestamp transactionDate) {

        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.type = type;
        this.transactionDate = transactionDate;
    }

    public int getId() {
        return id;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getType() {
        return type;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }
}

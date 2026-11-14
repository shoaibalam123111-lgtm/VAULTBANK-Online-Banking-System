
public class LegacyTransaction {

    int id;
    String sender;
    String receiver;
    double amount;
    String type;

    public LegacyTransaction(int id, String sender, String receiver,
                             double amount, String type) {

        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.type = type;
    }

    public void display() {

        System.out.println("--------------------------------");
        System.out.println("Transaction ID : " + id);
        System.out.println("Sender         : " + sender);
        System.out.println("Receiver       : " + receiver);
        System.out.println("Amount         : Rs. " + amount);
        System.out.println("Type           : " + type);
        System.out.println("--------------------------------");
    }
}

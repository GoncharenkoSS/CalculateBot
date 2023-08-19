package Calculon88.bot.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "values")
public class Values {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "balance")
    private String balance;
    @Column(name = "risk")
    private String risk;
    @Column(name = "open")
    private String open;
    @Column(name = "currency")
    private String currency;
    @Column(name = "bet")
    private String bet;
    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private UserTG owner;

    public Values(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBet() {
        return bet;
    }

    public void setBet(String bet) {
        this.bet = bet;
    }

    public UserTG getOwner() {
        return owner;
    }

    public void setOwner(UserTG owner) {
        this.owner = owner;
    }
}
public class Asset {
    private String user;
    private String buyDate;
    private Double buyPrice;
    private Tradable tradable;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(String buyDate) {
        this.buyDate = buyDate;
    }

    public Double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(Double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Tradable getTradable() {
        return tradable;
    }

    public void setTradable(Tradable tradable) {
        this.tradable = tradable;
    }

    public String toFileFormat() {
        return "User:" + user +
                " | BuyDate:" + buyDate +
                " | BuyPrice:" + buyPrice +
                " | Tradable:" + tradable.getName();
    }
}

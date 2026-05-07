import java.util.List;

public abstract class Tradable {
    String name;
    String type;
    String description;
    Double price;
    Double totalAmount;
    Double amountLeft;

    List<Double> priceHistory;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getAmountLeft() {
        return amountLeft;
    }

    public void setAmountLeft(Double amountLeft) {
        this.amountLeft = amountLeft;
    }

    public List<Double> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<Double> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public void addToPriceHistory(Double price) {
        priceHistory.add(price);
        if (priceHistory.size() > 100) {
            priceHistory.removeFirst();
        }
    }
}

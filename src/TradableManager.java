import java.nio.file.Files;
import java.nio.file.Path;

public final class TradableManager {
    private static TradableManager tradableManager = null;
    private Tradable[] tradables;

    private TradableManager(){}

    public static TradableManager getTradableManager() {
        if (tradableManager == null) {
            tradableManager = new TradableManager();
        }
        return tradableManager;
    }

    public String loadText() throws Exception {
        return Files.readString(Path.of("tradables.txt"));
    }

    public Tradable[] getTradables() throws Exception {
        String content = loadText();
        String[] lines = content.split("\n");
        this.tradables = new Tradable[lines.length];

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) continue;

            String[] parts = lines[i].split("\\|");

            String name = "";
            String type = "";
            String description = "";
            Double price = 0.0;
            Double totalAmount = 0.0;
            Double amountLeft = 0.0;

            for (String part : parts) {
                String[] details = part.split(":");

                if (details[0].contains("Name")) name = details[1].trim();
                if (details[0].contains("Type")) type = details[1].trim();
                if (details[0].contains("Description")) description = details[1].trim();
                if (details[0].contains("Price")) price = Double.parseDouble(details[1].trim());
                if (details[0].contains("TotalAmount")) totalAmount = Double.parseDouble(details[1].trim());
                if (details[0].contains("AmountLeft")) amountLeft = Double.parseDouble(details[1].trim());
            }

            Tradable tradable;

            switch(type) {
                case "Stock":
                    tradable = new Stock();
                    break;
                case "Certificate":
                    tradable = new Certificate();
                    break;
                default:
                    tradable = new Tradable(){};
                    break;
            }

            tradable.setName(name);
            tradable.setType(type);
            tradable.setDescription(description);
            tradable.setPrice(price);
            tradable.addToPriceHistory(price);
            tradable.setTotalAmount(totalAmount);
            tradable.setAmountLeft(amountLeft);

            this.tradables[i] = tradable;
        }
        return tradables;
    }

    public void setTradables(Tradable[] tradables) {
        this.tradables = tradables;
    }
}

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
            Tradable tradable = new Tradable(){};

            for (String part : parts) {
                String[] details = part.split(":");

                if (details[0].contains("Name")) tradable.setName(details[1].trim());
                if (details[0].contains("Price")) tradable.setPrice(details[1].trim());
                if (details[0].contains("TotalAmount")) tradable.setTotalAmount(details[1].trim());
                if (details[0].contains("AmountLeft")) tradable.setAmountLeft(details[1].trim());
            }

            this.tradables[i] = tradable;
        }
        return tradables;
    }

    public void setTradables(Tradable[] tradables) {
        this.tradables = tradables;
    }
}

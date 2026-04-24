import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public final class TradeManager {
    private static TradeManager tradeManager = null;
    private Tradable[] tradables;

    private static final String ASSETS_FILE = "assets.txt";

    private TradeManager(){}

    public static TradeManager getTradeManager() {
        if (tradeManager == null) {
            tradeManager = new TradeManager();
        }
        return tradeManager;
    }

    public String buyAsset(Tradable tradable) {
        User currentUser = UserManager.getUserManager().getCurrentUser();
        Asset asset = new Asset();
        asset.setUser(currentUser.getUserName());
        asset.setBuyDate(java.time.LocalDateTime.now().toString());
        asset.setBuyPrice(tradable.getPrice());
        asset.setTradable(tradable);

        if (asset.getBuyPrice() <= currentUser.getCash()) {
            if (tradable.getAmountLeft() > 0) {
                try {
                    currentUser.setCash(currentUser.getCash() - asset.getBuyPrice());

                    String line = asset.toFileFormat() + System.lineSeparator();

                    Files.write(
                            Paths.get(ASSETS_FILE),
                            line.getBytes(),
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND
                    );
                    System.out.println("Köpet sparades i " + ASSETS_FILE);
                    return "success";
                } catch (IOException e) {
                    e.printStackTrace();
                    return "error";
                }
            } else {
                return "noAmount";
            }
        } else {
            return "noCash";
        }
    }

    public void SellAsset(Asset asset) {
        try {
            User currentUser = UserManager.getUserManager().getCurrentUser();
            currentUser.setCash(currentUser.getCash() + asset.getTradable().getPrice());

            File inputFile = new File(ASSETS_FILE);
            File tempFile = new File("temp_assets.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if(trimmedLine.contains(asset.getUser()) && trimmedLine.contains(asset.getTradable().getName())) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            if (inputFile.delete()) {
                if (tempFile.renameTo(inputFile)){
                    System.out.println("Asset togs bort från " + ASSETS_FILE);
                }
            } else {
                System.out.println("Error");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

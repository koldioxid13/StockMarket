import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public String buyTradable(Tradable tradable) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(ASSETS_FILE));
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if(trimmedLine.contains(loan.getUser().getUserName()) && trimmedLine.contains(loan.getBook().getTitle())) {
                    reader.close();
                    return false;
                }
            }
            reader.close();
            String line = loan.toFileFormat() + System.lineSeparator();

            Files.write(
                    Paths.get(LOAN_FILE),
                    line.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            System.out.println("Lånet sparades i " + LOAN_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}

import java.io.*;
import java.nio.file.*;
import java.util.List;

public final class AssetManager {
    private static AssetManager assetManager = null;

    private AssetManager(){}

    private static final String ASSET_FILE = "assets.txt";
    private Asset[] assets;

    public static AssetManager getAssetManager() {
        if (assetManager == null) {
            assetManager = new AssetManager();
        }
        return assetManager;
    }

    public String loadText() throws Exception {
        return Files.readString(Path.of(ASSET_FILE));
    }

    public Asset[] getAssets() throws Exception {
        String content = loadText();
        String[] lines = content.split("\n");
        this.assets = new Asset[lines.length];

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) continue;

            String[] parts = lines[i].split("\\|");
            Asset asset = new Asset();

            for (String part : parts) {
                String[] details = part.split(":");

                if (details[0].contains("User")) asset.setUser(new User(details[1].trim()));
                if (details[0].contains("Date")) b.setLoanDate(details[1].trim());
                if (details[0].contains("Time")) b.setTimeLeft(Double.parseDouble(details[1].trim()));
                if (details[0].contains("Book")) {
                    List<Book> results = Search.getSearch().searchTradables(details[1].trim());
                    b.setBook(results.getFirst());
                }
            }

            this.assets[i] = b;
        }
        return assets;
    }

    public boolean saveLoan(LoanedBook loan) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(LOAN_FILE));
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

    public void returnLoan(LoanedBook loan) {
        try {
            File inputFile = new File(LOAN_FILE);
            File tempFile = new File("temp_loans.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if(trimmedLine.contains(loan.getUser().getUserName()) && trimmedLine.contains(loan.getBook().getTitle())) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            if (inputFile.delete()) {
                if (tempFile.renameTo(inputFile)){
                    System.out.println("Lånet togs bort från " + LOAN_FILE);
                }
            } else {
                System.out.println("Error");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setAssets(LoanedBook[] assets) {
        this.assets = assets;
    }
}
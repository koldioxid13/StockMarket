import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public final class AssetManager {
    private static AssetManager assetManager = null;

    private AssetManager(){}

    private static final String ASSET_FILE = "assets.txt";
    private ArrayList<Asset> assets;

    public static AssetManager getAssetManager() {
        if (assetManager == null) {
            assetManager = new AssetManager();
        }
        return assetManager;
    }

    public String loadText() throws Exception {
        return Files.readString(Path.of(ASSET_FILE));
    }

    public ArrayList<Asset> getAssets() {
        return assets;
    }

    public void setAssets(ArrayList<Asset> assets) {
        this.assets = assets;
    }

    public void setAssetsFromFile() throws Exception {
        String content = loadText();
        String[] lines = content.split("\n");

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) continue;

            String[] parts = lines[i].split("\\|");
            Asset asset = new Asset();

            for (String part : parts) {
                String[] details = part.split(":");

                if (details[0].contains("User")) asset.setUser(details[1].trim());
                if (details[0].contains("BuyDate")) asset.setBuyDate(details[1].trim());
                if (details[0].contains("BuyPrice")) asset.setBuyPrice(Double.parseDouble(details[1].trim()));
                if (details[0].contains("Tradable")) {
                    List<Tradable> results = Search.getSearch().searchTradables(details[1].trim());
                    asset.setTradable(results.getFirst());
                }
            }
            this.assets.add(asset);
        }
    }

    public ArrayList<Asset> getUserAsset(String wantedAsset) throws Exception {
        ArrayList<Asset> allAssets = getUserAssets();
        ArrayList<Asset> wantedAssets = new ArrayList<>();

        for (int i = 0; i < allAssets.size(); i++) {
            if (allAssets.get(i).getTradable().getName().equals(wantedAsset)) {
                wantedAssets.add(allAssets.get(i));
            }
        }
        return wantedAssets;
    }

    public ArrayList<Asset> getUserAssets() throws Exception {
        ArrayList<Asset> allAssets = getAssets();
        ArrayList<Asset> wantedAssets = new ArrayList<>();

        if (allAssets == null) {
            return wantedAssets;
        }

        for (int i = 0; i < allAssets.size(); i++) {
            if (allAssets.get(i).getUser().equals(UserManager.getUserManager().getCurrentUser().getUserName())) {
                wantedAssets.set(i, allAssets.get(i));
            }
        }
        return wantedAssets;
    }

    public boolean saveAsset(Asset asset) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(ASSET_FILE));
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                // trim newline when comparing with lineToRemove
                String trimmedLine = currentLine.trim();
                if(trimmedLine.contains(asset.getUser()) && trimmedLine.contains(asset.getTradable().getName())) {
                    reader.close();
                    return false;
                }
            }
            reader.close();
            String line = asset.toFileFormat() + System.lineSeparator();

            Files.write(
                    Paths.get(ASSET_FILE),
                    line.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            System.out.println("The asset was saved to " + ASSET_FILE);

            this.assets.add(asset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void sellAsset(Asset asset) {
        try {
            File inputFile = new File(ASSET_FILE);
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
                    System.out.println("Asset was removed from " + ASSET_FILE);
                    this.assets.remove(asset);
                }
            } else {
                System.out.println("Error");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
import java.nio.file.*;
import java.io.IOException;
import java.util.List;

public final class AssetManager {
    private static AssetManager assetManager = null;

    private AssetManager(){}

    private static final String ASSET_FILE = "assets.txt";

    public static AssetManager getAssetManager() {
        if (assetManager == null) {
            assetManager = new AssetManager();
        }
        return assetManager;
    }
}
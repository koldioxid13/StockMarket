import java.util.ArrayList;
import java.util.List;

public final class Search {
    private static Search search = null;

    private Search(){}

    public static Search getSearch() {
        if (search == null) {
            search = new Search();
        }
        return search;
    }

    public List<Tradable> searchTradables(String query) {
        List<Tradable> filteredTradables = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        try {
            Tradable[] allTradables = TradableManager.getTradableManager().getTradables();
            for (Tradable tradable : allTradables) {
                if (tradable.getName().toLowerCase().contains(lowerQuery)) {
                    filteredTradables.add(tradable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filteredTradables;
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class StockMarketGUI extends JFrame {
    private Map<String, List<Tradable>> groupedTradables = new HashMap<>();
    private String currentCategory = "";
    private JPanel tabBar; // Flytta ut tabBar så vi kan rensa den
    private DefaultTableModel tableModel;
    JLabel statusLabel = new JLabel(" ");
    private JTextArea detailsArea;
    private ChartPanel chartPanel;
    private JTable stockTable;

    public StockMarketGUI() {
        // --- 1. Huvudinställningar för fönstret ---
        setTitle("Sigma Stocks Exchange");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        // Bakgrundsfärg för hela fönstret (något mörkare än själva panelen)
        getContentPane().setBackground(new Color(20, 20, 20));

        // --- 2. Huvudpanelen för marknadssidan ---
        JPanel marketPanel = new JPanel();
        marketPanel.setBackground(Color.BLACK);
        // En tunn mörkgrå kant för att definiera området
        marketPanel.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 1));
        marketPanel.setLayout(new BorderLayout(5, 5)); // Lite mellanrum mellan sektioner

        // --- 3. Toppsektionen (Finanser) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        topPanel.setOpaque(false); // Gör panelen genomskinlig mot svart

        Font smallMono = new Font(Font.MONOSPACED, Font.PLAIN, 14);

        // Användare
        JLabel userLabel = new JLabel("Entity: " + UserManager.getUserManager().getCurrentUser().getUserName());
        userLabel.setForeground(Color.LIGHT_GRAY);
        userLabel.setFont(smallMono);

        JLabel cashLabel = new JLabel("Cash: $" + UserManager.getUserManager().getCurrentUser().getCash());
        cashLabel.setForeground(Color.WHITE);
        cashLabel.setFont(smallMono);

        JLabel holdingsLabel = new JLabel("Total holdings: $0");
        holdingsLabel.setForeground(Color.WHITE);
        holdingsLabel.setFont(smallMono);

        // Utloggningsknapp
        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.setForeground(Color.RED);
        logoutBtn.setBackground(Color.BLACK);
        logoutBtn.setFont(smallMono.deriveFont(Font.BOLD));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutBtn.addActionListener(e -> {
            UserManager.getUserManager().setCurrentUser(null);
            this.dispose();
            new LoginGUI();
        });

        // Lägger till allt i rad (hamnar till höger tack vare FlowLayout.RIGHT)
        topPanel.add(userLabel);
        topPanel.add(cashLabel);
        topPanel.add(holdingsLabel);
        topPanel.add(logoutBtn);

        marketPanel.add(topPanel, BorderLayout.NORTH);

        // --- 4. Mittsektionen (Vänster och Höger kolumn) ---
        JPanel contentArea = new JPanel(new GridBagLayout());
        contentArea.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 5, 5, 5); // Marginaler

        // --- 4a. Vänster kolumn (Flikar och Aktielista) ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.35; // Breddfördelning
        gbc.weighty = 1.0;
        JPanel leftSidebar = new JPanel(new BorderLayout());
        leftSidebar.setOpaque(false);

        // -- Flikar (Nu dynamiska) --
        tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabBar.setOpaque(false);
        leftSidebar.add(tabBar, BorderLayout.NORTH);

        // -- Aktielista --
        String[] columns = {" ", "Ticker", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Laddar data från din TradableManager
        loadMarketData();

        stockTable = new JTable(tableModel);
        stockTable.setOpaque(false);
        stockTable.setBackground(Color.BLACK);

        // Custom font för listan (Monospaced, Bold)
        Font stockFont = new Font(Font.MONOSPACED, Font.BOLD, 14);
        stockTable.setFont(stockFont);

        // Custom rendering för att efterlikna stilen
        // Röd punkt-indikator till vänster
        stockTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        stockTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setText(" ● "); // Röd punkt
                label.setForeground(Color.RED);
                label.setBackground(isSelected ? table.getSelectionBackground() : Color.BLACK);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                return label;
            }
        });

        // Tickers och värden (grå/vit text)
        stockTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setForeground(new Color(220, 220, 220)); // Ljust grå
                label.setBackground(isSelected ? table.getSelectionBackground() : Color.BLACK);
                label.setOpaque(true);
                return label;
            }
        });
        stockTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setForeground(new Color(220, 220, 220));
                label.setBackground(isSelected ? table.getSelectionBackground() : Color.BLACK);
                label.setHorizontalAlignment(SwingConstants.RIGHT); // Värden till höger
                label.setOpaque(true);
                return label;
            }
        });

        // Ta bort rutnät och kant
        stockTable.setShowGrid(false);
        stockTable.setIntercellSpacing(new Dimension(0, 0));

        // Ta bort tabellrubrikens kant och anpassa font
        stockTable.getTableHeader().setBorder(null);
        stockTable.getTableHeader().setFont(smallMono.deriveFont(Font.BOLD));
        stockTable.getTableHeader().setBackground(Color.BLACK);
        stockTable.getTableHeader().setForeground(Color.LIGHT_GRAY);

        JScrollPane stockListScroll = new JScrollPane(stockTable);
        stockListScroll.setBorder(null);
        stockListScroll.getViewport().setBackground(Color.BLACK);
        stockListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Visa alltid scroll
        leftSidebar.add(stockListScroll, BorderLayout.CENTER);

        contentArea.add(leftSidebar, gbc);

        // --- 4b. Höger kolumn (Graf, Detaljer, Köp/Sälj) ---
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setOpaque(false);
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.insets = new Insets(0, 5, 0, 5); // Marginaler
        gbcMain.weightx = 1.0;

        // -- Diagram --
        gbcMain.gridy = 0;
        gbcMain.weighty = 0.4;
        chartPanel = new ChartPanel(); // Nu utan typ-prefix
        mainContentPanel.add(chartPanel, gbcMain);

        // -- Detaljtext --
        gbcMain.gridy = 1;
        gbcMain.weighty = 0.4;
        detailsArea = new JTextArea("SELECT AN ENTITY TO VIEW DATA INTERCEPT.");
        detailsArea.setEditable(false);
        detailsArea.setBackground(Color.BLACK);
        detailsArea.setForeground(Color.WHITE);
        detailsArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        detailsArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50)));
        mainContentPanel.add(detailsScroll, gbcMain);

        // -- Action Panel (Köp/Sälj/Status) --
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));

        // Köp-rad
        JPanel buyRow = createActionRow("Buy ", Color.GREEN, e -> {
            JButton btn = (JButton)e.getSource();
            setStatus("ORDER EXECUTED: Bought " + btn.getText() + " units", statusLabel);
        });

        // Sälj-rad
        JPanel sellRow = createActionRow("Sell", Color.RED, e -> {
            JButton btn = (JButton)e.getSource();
            setStatus("ORDER EXECUTED: Sold " + btn.getText() + " units", statusLabel);
        });

        statusLabel.setForeground(Color.YELLOW);
        statusLabel.setFont(smallMono);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setPreferredSize(new Dimension(400, 25));
        statusLabel.setMinimumSize(new Dimension(400, 25));

        actionPanel.add(buyRow);
        actionPanel.add(sellRow);
        actionPanel.add(Box.createVerticalStrut(10));
        actionPanel.add(statusLabel);

        gbcMain.gridy = 2;
        gbcMain.weighty = 0.2; // Ger plats åt knapparna längst ner
        mainContentPanel.add(actionPanel, gbcMain);

        stockTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = stockTable.getSelectedRow();
                if (row != -1) {
                    String ticker = (String) tableModel.getValueAt(row, 1);
                    // Hitta objektet i vår mapp
                    Tradable selected = groupedTradables.get(currentCategory).stream()
                            .filter(t -> t.getName().equals(ticker))
                            .findFirst().orElse(null);

                    if (selected != null) {
                        // Uppdatera texten
                        try {
                            ArrayList<Asset> userAsset = AssetManager.getAssetManager().getUserAsset(selected.getName());

                            Double amountOwned = 0.0;
                            Double ownedValue = 0.0;

                            if (!userAsset.isEmpty()) {
                                amountOwned = (double) userAsset.size();
                                ownedValue = amountOwned * selected.getPrice();
                            }

                            detailsArea.setText(
                                    "ENTITY: " + selected.getName() + "\n\n" +
                                            "MARKET CAP: $" + String.format("%.2f", selected.getPrice() * selected.getTotalAmount()) + "\n" +
                                            "PRICE: $" + String.format("%.2f", selected.getPrice()) + "\n" +
                                            "TOTAL AMOUNT: " + String.format("%.2f", selected.getTotalAmount()) + "\n" +
                                            "BUYABLE AMOUNT: " + String.format("%.2f", selected.getAmountLeft()) + "\n" +
                                            "AMOUNT OWNED: " + amountOwned + "\n" +
                                            "OWNED VALUE: $" + String.format("%.2f", ownedValue) + "\n" +
                                            selected.getDescription()
                            );
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        // Uppdatera grafen
                        chartPanel.updateData(selected.getPriceHistory());
                        mainContentPanel.revalidate();
                        mainContentPanel.repaint();
                    }
                }
            }
        });

        gbc.gridx = 1; // Flytta till höger kolumn
        contentArea.add(mainContentPanel, gbc);

        marketPanel.add(contentArea, BorderLayout.CENTER);

        add(marketPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // --- Hjälpmetod för att lägga till rader i tabellen ---
    private void addStockRow(DefaultTableModel model, String ticker, double value) {
        model.addRow(new Object[]{"", ticker, String.valueOf(value)});
    }

    // --- Hjälpmetod för att skapa "Flik"-knappar ---
    private JButton createTabButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setForeground(active ? Color.WHITE : Color.LIGHT_GRAY);
        btn.setBackground(active ? Color.DARK_GRAY : Color.BLACK);
        btn.setFont(new Font(Font.MONOSPACED, active ? Font.BOLD : Font.PLAIN, 16));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // padding
        btn.setFocusPainted(false); // Ta bort fokusring
        btn.setBorderPainted(false);
        return btn;
    }

    private JPanel createActionRow(String labelText, Color color, ActionListener action) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setForeground(color);
        label.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        row.add(label);

        row.add(new JLabel(" | "));

        // Skickar med action till alla knappar
        addQuantityButton(row, "1", color, action);
        addQuantityButton(row, "2", color, action);
        addQuantityButton(row, "5", color, action);
        addQuantityButton(row, "10", color, action);
        addQuantityButton(row, "100", color, action);

        return row;
    }

    // --- Hjälpmetod för att skapa mängdknappar ---
    private void addQuantityButton(JPanel panel, String text, Color color, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setForeground(color);
        btn.setBackground(Color.BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setFocusPainted(false);
        btn.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        btn.setBorderPainted(false);

        // --- HÄR LÄGGS LYSSNAREN TILL ---
        if (action != null) {
            btn.addActionListener(action);
        }

        panel.add(btn);
    }

    private void setStatus(String statusText, JLabel statusLabel) {
        statusLabel.setText(statusText);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        if (Objects.equals(statusLabel.getText(), statusText)) {
                            statusLabel.setText(" ");
                        }
                    }
                },
                3000
        );
    }

    private void loadMarketData() {
        try {
            Tradable[] allTradables = TradableManager.getTradableManager().getTradables();

            // Gruppera objekten efter deras klassnamn (t.ex. "Stock", "Fish")
            groupedTradables = Arrays.stream(allTradables)
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(Tradable::getType));

            updateTabs();

            // Visa första kategorin som standard
            if (!groupedTradables.isEmpty()) {
                showCategory(groupedTradables.keySet().iterator().next());
            }
        } catch (Exception e) {
            setStatus("ERROR LOADING DATA", statusLabel);
        }
    }

    private void updateTabs() {
        tabBar.removeAll();
        for (String category : groupedTradables.keySet()) {
            JButton tabBtn = createTabButton(category, category.equals(currentCategory));
            tabBtn.addActionListener(e -> showCategory(category));
            tabBar.add(tabBtn);
        }
        tabBar.revalidate();
        tabBar.repaint();
    }

    private void showCategory(String category) {
        currentCategory = category;
        tableModel.setRowCount(0); // Rensa tabellen

        List<Tradable> items = groupedTradables.get(category);
        if (items != null) {
            for (Tradable t : items) {
                // Vi använder t.getName() och t.getPrice() från din Tradable-klass
                tableModel.addRow(new Object[]{"", t.getName(), String.valueOf(t.getPrice())});
            }
        }
        updateTabs(); // Uppdatera utseendet på flikarna (vilken som är aktiv)
    }

    private class ChartPanel extends JPanel {
        private List<Double> history = new ArrayList<>();

        public ChartPanel() {
            setBackground(Color.BLACK);
            setOpaque(true);
            setPreferredSize(new Dimension(400, 300));
        }

        public void updateData(List<Double> newHistory) {
            this.history = newHistory;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (history == null || history.size() < 2) {
                g.setColor(Color.DARK_GRAY);
                g.drawString("Awaiting market data...", 20, 30);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double maxPrice = history.stream().max(Double::compare).orElse(1.0);
            double minPrice = history.stream().min(Double::compare).orElse(0.0);
            double range = Math.max(maxPrice - minPrice, 1.0);

            int padding = 20;
            int w = getWidth() - 2 * padding;
            int h = getHeight() - 2 * padding;

            for (int i = 1; i < history.size(); i++) {
                int x1 = padding + (i - 1) * w / (history.size() - 1);
                int y1 = (getHeight() - padding) - (int) ((history.get(i - 1) - minPrice) / range * h);
                int x2 = padding + i * w / (history.size() - 1);
                int y2 = (getHeight() - padding) - (int) ((history.get(i) - minPrice) / range * h);

                g2.setColor(history.get(i) >= history.get(i - 1) ? Color.GREEN : Color.RED);
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }
}
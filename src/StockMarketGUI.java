import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Random;

public class StockMarketGUI extends JFrame {

    public StockMarketGUI() {
        // --- 1. Huvudinställningar för fönstret ---
        setTitle("Sigma Stocks Exchange");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        JLabel statusLabel;

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

        // -- Flikar (simulerade) --
        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabBar.setOpaque(false);

        JButton stocksBtn = createTabButton("Stocks", true);
        JButton partsBtn = createTabButton("Parts", false);
        JButton fishBtn = createTabButton("Fish", false);

        tabBar.add(stocksBtn);
        tabBar.add(partsBtn);
        tabBar.add(fishBtn);

        leftSidebar.add(tabBar, BorderLayout.NORTH);

        // -- Aktielista --
        String[] columns = {" ", "Ticker", "Value"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ingen redigering
            }
        };

        // Hårdkodad data från bilden
        addStockRow(tableModel, "BRN", 0);
        addStockRow(tableModel, "AUGB", 0);
        addStockRow(tableModel, "PSYB", 0);
        addStockRow(tableModel, "WRMB", 0);
        addStockRow(tableModel, "LIVR", 0.3);
        addStockRow(tableModel, "TLVR", -0.1);
        addStockRow(tableModel, "KDNY", 0);
        addStockRow(tableModel, "HERT", 0.1);
        addStockRow(tableModel, "BHRT", 0.1);
        addStockRow(tableModel, "INTS", -0.2);
        addStockRow(tableModel, "PINT", 0.1);
        addStockRow(tableModel, "APNX", 0.1);
        addStockRow(tableModel, "SPNE", 0);
        addStockRow(tableModel, "RSPN", -0.1);
        addStockRow(tableModel, "GUT", 0);
        addStockRow(tableModel, "NGUT", 0);
        addStockRow(tableModel, "PNCR", 0);
        addStockRow(tableModel, "ADVP", 0);

        JTable stockTable = new JTable(tableModel);
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

        // -- Simulerat Kurvdiagram --
        gbcMain.gridy = 0;
        gbcMain.weighty = 0.6; // Tar upp mest plats
        gbcMain.gridheight = 1;
        ChartPanel chartPanel = new ChartPanel(); // Egen klass definierad nedan
        mainContentPanel.add(chartPanel, gbcMain);

        // -- Detaljtext area --
        gbcMain.gridy = 1;
        gbcMain.weighty = 0.2;
        String detailsText = "Steady bluechip company in the security business.\n" +
                "(0)Cruelty Squad\n" +
                "Price: $1240.216143 0.26%\n" +
                "MKC: $3612516463.279987";
        JTextArea detailsArea = new JTextArea(detailsText);
        detailsArea.setEditable(false);
        detailsArea.setBackground(Color.BLACK);
        detailsArea.setForeground(Color.WHITE);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(stockFont);
        detailsArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Padding

        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50)));
        mainContentPanel.add(detailsScroll, gbcMain);

        // --- Status Label ---
        gbcMain.gridy = 2;
        gbcMain.weighty = 0.05;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.YELLOW);
        statusLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setPreferredSize(new Dimension(400, 30));
        statusLabel.setMinimumSize(new Dimension(400, 30));
        statusLabel.setMaximumSize(new Dimension(400, 30));
        mainContentPanel.add(statusLabel, gbcMain);

        // -- Köp/Sälj Panel (Längst ner) --
        gbcMain.gridy = 3;
        gbcMain.weighty = 0.2;
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 5, 5)); // Två rader
        actionPanel.setOpaque(false);

        // Köp-rad
        JPanel buyRow = createActionRow("Buy", Color.GREEN, e -> {
            // Tar reda på vilken knapp som klickades (t.ex. "5" eller "10")
            JButton clickedButton = (JButton) e.getSource();
            String amount = clickedButton.getText();

     //       TradeManager.getTradeManager().buyAsset(Sigma);
            try {
                TradableManager.getTradableManager().getTradables();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("KÖPER: " + amount + " st");
        });
        actionPanel.add(buyRow);

        // Sälj-rad
        JPanel sellRow = createActionRow("Sell", Color.RED, e -> {
            JButton clickedButton = (JButton) e.getSource();
            String amount = clickedButton.getText();

            // TODO: Här lägger du in logiken för att SÄLJA.
            System.out.println("SÄLJER: " + amount + " st");
        });
        actionPanel.add(sellRow);

        mainContentPanel.add(actionPanel, gbcMain);

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
                            statusLabel.setText(null);
                        }
                    }
                },
                3000
        );
    }

    // --- Egen panelklass för att rita kurvdiagrammet ---
    private class ChartPanel extends JPanel {
        public ChartPanel() {
            setBackground(Color.BLACK);
            setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Gör linjerna lite tydligare
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2.0f));

            int w = getWidth();
            int h = getHeight();
            int margin = 30; // Marginal från kanterna
            int chartWidth = w - 2 * margin;
            int chartHeight = h - 2 * margin;

            // Generera statiska, slumpmässiga punkter för diagrammet
            int numPoints = 80;
            int[] xPoints = new int[numPoints];
            int[] yPoints = new int[numPoints];

            Random rand = new Random(42); // Fast seed för att grafen inte ska ändras
            int currentY = chartHeight / 2;
            for (int i = 0; i < numPoints; i++) {
                xPoints[i] = margin + (i * chartWidth) / (numPoints - 1);
                int delta = rand.nextInt(31) - 15; // -15 till +15
                currentY += delta;

                // Håll punkterna inom diagramytan
                if (currentY < margin) currentY = margin;
                if (currentY > margin + chartHeight) currentY = margin + chartHeight;
                yPoints[i] = currentY;
            }

            // Rita linjerna med skiftande färger för att simulera kurvan
            for (int i = 1; i < numPoints; i++) {
                // Om värdet går upp (lägre Y i Swing-koordinater), visa rött, annars grönt
                if (yPoints[i] < yPoints[i-1]) {
                    g2.setColor(Color.GREEN); // Uppgång (visuellt högre värde)
                } else {
                    g2.setColor(Color.RED); // Nedgång
                }
                g2.drawLine(xPoints[i-1], yPoints[i-1], xPoints[i], yPoints[i]);
            }
        }
    }
}
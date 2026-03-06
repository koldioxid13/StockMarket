import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Startar GUI:t på ett trådsäkert sätt enligt Swing-standard
        SwingUtilities.invokeLater(() -> {
            StockMarketGUI gui = new StockMarketGUI();
            gui.setVisible(true);
        });
    }
}
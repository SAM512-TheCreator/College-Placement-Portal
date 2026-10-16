package gui;

import service.PlacementManager;

public class Main {

    private static PlacementManager manager;

    public static void main(String[] args) {
        manager = new PlacementManager();
        javax.swing.SwingUtilities.invokeLater(() -> new LoginFrame(manager).setVisible(true));
    }

    public static PlacementManager getManager() { return manager; }
}

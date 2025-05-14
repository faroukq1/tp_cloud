import javax.swing.SwingUtilities;
import auth.AuthGUI;
import dashboard.Dashboard;
import states.AppStates;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (AppStates.logIn) {
                new Dashboard();
            } else {
                new AuthGUI();
            }
        });
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package tmasch;

/**
 *
 * @author Draugr
 */
public class TMasch {

    public static void main(String[] args) {
        // Set Nimbus Look and Feel
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info :
                    javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // Launch Login
        java.awt.EventQueue.invokeLater(() ->
            new Form.LoginForm().setVisible(true));
    }
    
}

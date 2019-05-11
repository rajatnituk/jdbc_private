import gui.PrivacyWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainScreen extends JFrame {
    private JTextField queryField;
    private JButton runButton;
    private JTextArea textArea1;
    private JButton addPrivacyFiltersButton;
    private JPanel root;
    ResultSet rs;

    public MainScreen() {
        add(root);
        setSize(500,500);
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDBCExample ex = new JDBCExample();
                rs = ex.getResultSet(queryField.getText());
                textArea1.setText(generateResult());
            }
        });
        addPrivacyFiltersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrivacyWindow p = new PrivacyWindow(rs);
                p.setVisible(true);
            }
        });
    }


    String generateResult(){
        String out = "";
        try {
            //Column
            for(int i=1; i<=rs.getMetaData().getColumnCount(); i++)
                out+= rs.getMetaData().getColumnName(i)+" ";
            out+="\n";
            while(rs.next()) {
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    System.out.println(i);
                    out += rs.getString(i) + " ";
                }
                out += "\n";
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return out;
    }


}

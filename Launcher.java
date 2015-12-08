import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author rezenter
 */

public class Launcher extends JFrame {

    static ArrayList<String> tasks = new ArrayList<String>();
    final static String PATH = "c:/tasks/";

    public static void main(String[] args) throws IOException {
        new Launcher();
    }

    public static final String FRAME_TITLE = "boring task launcher";

    public Launcher() throws IOException {
        super(FRAME_TITLE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        index();
        ButtonGroup buts = new ButtonGroup();
        c.ipadx = 20;


        for(int i = 0; i < tasks.size(); i ++){
            JRadioButton but = new JRadioButton();
            but.setText(tasks.get(i));
            buts.add(but);
            panel.add(but, c);
        }

        JButton start = new JButton();
        start.setText("Start");
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println(buts.getSelection());
                    new GUI(PATH + buts.getSelection());
                    dispose();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        panel.add(start);

        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        setVisible(true);
    }

    private void index(){
        File goodFolder = new File(PATH);
        for (final File fileEntry : goodFolder.listFiles()) {
            if(fileEntry.isDirectory()) {
                tasks.add(fileEntry.getName());
            }
        }
    }
}


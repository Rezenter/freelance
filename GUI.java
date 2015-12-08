import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author rezenter
 */

public class GUI extends JFrame {

    static FlowLayout fl;
    final static Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    static String GOOD_PATH = "c:/tasks/good/";
    final static String BAD_PATH = "c:/tasks/bad/";
    final static String SOUND_PATH = "c:/tasks/";
    final static ArrayList<String> good = new ArrayList<String>();
    final static ArrayList<String> bad = new ArrayList<String>();
    static Task task;
    static JPanel pics;
    static Clip clip;


    public static void main(String[] args) throws IOException {
        //GOOD_PATH = args[0];
        String task = "text of the task. U can set size,colour of letters and background. " +
                "\n Also here can be button for sound";
        new GUI(task);
        //Vector3f a = new Vector3f();
    }

    public static final String FRAME_TITLE = "boring task";

    public GUI(String t) throws IOException {
        super(FRAME_TITLE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        pics = new JPanel();
        c.gridx = 0;
        c.gridy = 0;
        panel.add(pics, c);
        fl = new FlowLayout();
        fl.setAlignment(FlowLayout.TRAILING);
        pics.setLayout(fl);
        index();
        task = generate(pics);

        final JTextArea task = new JTextArea();
        task.setText(t);
        task.setEditable(false);

        c.gridy = 2;
        panel.add(task, c);

        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(d);
        setVisible(true);
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type, boolean param){
        int h;
        int w;
        if(param){
            h = originalImage.getWidth()*d.height*3/(4*originalImage.getHeight());
            w = d.height*3/4;
        }else{
            h = (d.width/2)-10 ;
            w = originalImage.getHeight()*d.width/(2*originalImage.getWidth());
        }
        System.out.println();
        BufferedImage resizedImage = new BufferedImage(h, w, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, h, w, null);
        g.dispose();
        return resizedImage;
    }

    public static class Task{
        boolean answ;

        Task(boolean c){
            answ = c;
        }
    }

    private void index(){
        File goodFolder = new File(GOOD_PATH);
        for (final File fileEntry : goodFolder.listFiles()) {
            good.add(fileEntry.getName());
            }
        File badFolder = new File(BAD_PATH);
        for (final File fileEntry : badFolder.listFiles()) {
            bad.add(fileEntry.getName());
            }
        }

    public Task generate(JPanel pics) throws IOException {
        task = new Task(Math.random() < 0.5);
        Random random = new Random();
        BufferedImage pic = ImageIO.read(new File(GOOD_PATH + good.get(random.nextInt(good.size()))));

        if(pic.getHeight() > d.getHeight()*3/4){
            int type = pic.getType() == 0? BufferedImage.TYPE_INT_ARGB : pic.getType();
            pic = resizeImage(pic, type, true);
        }
        if(pic.getWidth() > d.getWidth()/2 - 10){
            int type = pic.getType() == 0? BufferedImage.TYPE_INT_ARGB : pic.getType();
            pic = resizeImage(pic, type, false);
        }

        JLabel picLabel = new JLabel(new ImageIcon(pic));
        add(picLabel);

        BufferedImage pic2 = ImageIO.read(new File(BAD_PATH + bad.get(random.nextInt(bad.size()))));
        if(pic2.getHeight() > d.getHeight()*3/4){
            int type = pic2.getType() == 0? BufferedImage.TYPE_INT_ARGB : pic2.getType();
            pic2 = resizeImage(pic2, type, true);
        }
        if(pic2.getWidth() > d.getWidth()/2 - 10){
            int type = pic2.getType() == 0? BufferedImage.TYPE_INT_ARGB : pic2.getType();
            pic2 = resizeImage(pic2, type, false);
        }
        JLabel picLabel2 = new JLabel(new ImageIcon(pic2));
        add(picLabel2);
        if(task.answ){
            pics.add(picLabel);
            pics.add(picLabel2);
            picLabel.addMouseListener(ml(true));
            picLabel2.addMouseListener(ml(false));
        }else{
            picLabel2.addMouseListener(ml(true));
            picLabel.addMouseListener(ml(false));
            pics.add(picLabel2);
            pics.add(picLabel);
        }
        fl.setHgap((d.width - pic.getWidth() - pic2.getWidth())/100);

        return task;
    }

    public MouseListener ml(boolean a){
        MouseListener res = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    clip.stop();
                    if(a == task.answ) {
                        pics.removeAll();
                        task = generate(pics);
                        pics.revalidate();
                        pics.repaint();
                        playSound(SOUND_PATH + "win.wav");
                    }else{
                        playSound(SOUND_PATH + "wrong.wav");
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                playSound(SOUND_PATH + "drums.wav");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                clip.stop();
            }
        };
        return res;
    }

    public static synchronized void playSound(String url) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(url));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
}

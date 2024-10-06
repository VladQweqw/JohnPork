import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Random;
import java.io.IOException;
import java.awt.event.*;

public class JohnPork {
    static boolean hasAnswered = false;
    static boolean hasDeclined = false;
    static Clip sound;

    public static void main(String[] args) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage("path/to/icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
            
            // Create a popup menu
            PopupMenu popup = new PopupMenu();
            MenuItem exitItem = new MenuItem("Exit");
            
            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);
            
            try {
                tray.add(trayIcon);
                // Hide the application window
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setUndecorated(true); // Hide title bar
                frame.setSize(0, 0); // Size of 0 to effectively hide
                frame.setVisible(false);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("System tray not supported!");
        }

        JFrame f = new JFrame("John Pork is Calling");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400, 400);    
        f.setUndecorated(true);

        PlaySound();
        Start(f);

        f.setLayout(null);
        f.setVisible(true);
        f.setResizable(false);
    }

    public static void Start(JFrame f) {
        f.getContentPane().removeAll();
        f.revalidate();
        f.repaint();

        DefineElements(f);
        positionOnMonitor(f, getRng(2));
        f.setVisible(true);
        sound.loop(Clip.LOOP_CONTINUOUSLY);
        
    }

    public static int getRng(int range) {
        Random rand = new Random();
        int rng = rand.nextInt(range);

        return rng;
    }

    public static void positionOnMonitor(JFrame f, int preferable) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();

        if(devices.length > 1) {
            Rectangle bounds = devices[preferable].getDefaultConfiguration().getBounds();
            f.setLocation(bounds.x + getRng(bounds.width - 400), bounds.y + getRng(bounds.height - 400));
        }else {
            Rectangle bounds = devices[0].getDefaultConfiguration().getBounds();
            f.setLocation(getRng(bounds.width - 400), getRng(bounds.height - 400));
        }
    }

    public static void AddBackground(JFrame f) {
        JLabel bg_img = new JLabel();
        bg_img.setBounds(0, 0, 400, 400);
        if(hasDeclined) {
            bg_img.setIcon(new ImageIcon("./images/john_pork_evil_400.png"));
        }else {
            bg_img.setIcon(new ImageIcon("./images/john_pork_400.png"));
        }

        f.add(bg_img);
    }

    public static void DefineElements(JFrame f) {
        JLabel bg_img = new JLabel();
        bg_img.setBounds(0, 0, 400, 400);
        bg_img.setIcon(new ImageIcon("images/john_pork_400.png"));
        
        JLabel bg_img_evil = new JLabel();
        bg_img_evil.setBounds(0, 0, 400, 400);
        bg_img_evil.setIcon(new ImageIcon("images/john_pork_evil_400.png"));


        // buttons
        JLabel answer_img = new JLabel();
        answer_img.setBounds(50, 250, 80, 106);
        ImageIcon asnwer_icon = new ImageIcon("./images/answer.png");
        answer_img.setIcon(asnwer_icon);
        answer_img.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        answer_img.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AnswerCall(f);
            }
        });

        // decline
        JLabel decline_img = new JLabel();
        decline_img.setBounds(250, 250, 80, 106);
        ImageIcon decline_icon = new ImageIcon("./images/decline.png");
        decline_img.setIcon(decline_icon);
        decline_img.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        decline_img.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DeclineCall(f);
            }
        });
        
        f.add(decline_img);
        f.add(answer_img);
        System.out.println(hasDeclined);

        if(hasDeclined) {
            f.add(bg_img_evil);
        }else {
            f.add(bg_img);
        }

    }

    public static void PlaySound() {
        try {
            File file = new File("./sounds/sound.wav");
            Clip clip = AudioSystem.getClip();

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip.open(audioStream);

            sound = clip;
        }
        catch(Exception err) {
            System.err.println(err.getMessage());

        }

    }

    public static void AnswerCall(JFrame f) {     
        if(hasDeclined) hasDeclined = false;
        
        hasAnswered = true;
        f.setVisible(false);
        sound.stop();
        sound.setFramePosition(0);
        
        StartRandomCall(f);
    }

    public static void StartRandomCall(JFrame f) {
        int rng = getRng(300);

        Timer timer = new Timer(rng * 1000, e -> {
            Start(f);
        });
        timer.setRepeats(false);
        timer.start();
    }

    public static void SleepPC() {
        String command = "rundll32.exe powrprof.dll,SetSuspendState 0,1,0";
        
        // Use ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        
        try {
            // Start the process
            Process process = processBuilder.start();
            
            // Wait for the process to complete (optional)
            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void DeclineCall(JFrame f) { 
        if(hasDeclined) SleepPC();
        
        hasDeclined = true;
        sound.stop();
        sound.setFramePosition(0);

        f.setVisible(false);
        StartRandomCall(f);
    }
}
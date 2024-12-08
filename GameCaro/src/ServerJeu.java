import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

class AddClient implements Runnable {

    JTextArea TAreaChat;
    Socket client;

    public AddClient(Socket client, JTextArea chatTextArea) {
        this.client = client;
        this.TAreaChat = chatTextArea;
    }

    public void run() {
        String line;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

        } catch (IOException e) {
            System.out.println("Connexion echouee: 4444");
            System.exit(-1);
        }
        while (true) {
            try {
                line = in.readLine();
                TAreaChat.append("Client : " + line + "\n");
            } catch (IOException ex) {
            }
        }
    }
}


public class ServerJeu extends javax.swing.JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Creation de la nouvelle forme serveur */
    public ServerJeu() {
        initComponents();
        setVisible(true);
        myself = new PlayNow(myselfPanel);
        you = new PlayNow(youPanel);
        myself.start();
        you.start();
        you.suspend();
        class Listen extends Thread {

            public Listen() {
                start();
            }

            @Override
            public void run() {
                listenSocket();
            }
        }
        new Listen();
        class ListenGame extends Thread {

            public ListenGame() {
                start();
            }

            @Override
            public void run() {
                listenSocketGamne();
            }
        }
        new ListenGame();
        createTrayIcon();
    }
    
    private void initComponents() {

        boardPanel = new javax.swing.JPanel(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g){
                this.setOpaque(false);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g.setColor(Color.MAGENTA);
                for(int r = 0;r<=Size;r++){
                    g.drawLine(X0, Y0+r*Width, X0+Size*Width, Y0+r*Width);
                }
                for(int c = 0;c<=Size;c++){
                    g.drawLine(X0+c*Width, Y0, X0+c*Width, Y0+Size*Width);
                }

                if(!isPause)
                if(currentColumn<Size&&currentColumn>=0&&currentRow<Size&&currentRow>=0){
                    g.setColor(new Color(0, 0, 0, 80));
                    g2.fillOval(X0+currentColumn*Width+Width/6+1,Y0+ currentRow*Width+Width/6+1, 2*Width/3, 2*Width/3);
                }
                
                if(checked.size()==0) return;
                for(int p = 0;p<checked.size();p++){
                    if(startUser){
                        if(p%2==0) g2.setColor(Color.ORANGE);
                        else g2.setColor(Color.YELLOW);
                    }else{
                        if(p%2!=0) g2.setColor(Color.ORANGE);
                        else g2.setColor(Color.YELLOW);
                    }
                    g2.fillOval(X0+checked.get(p).x*Width+Width/6+1,Y0+ checked.get(p).y*Width+Width/6+1, 2*Width/3, 2*Width/3);
                    
                }
                g.setColor(Color.RED);
                g.drawRect(checked.get(checked.size()-1).x*Width+X0, checked.get(checked.size()-1).y*Width+Y0, Width, Width);
                super.paintComponent(g);
            }
        }
        ;
        backButton = new javax.swing.JButton();
        myselfPanel = new javax.swing.JPanel();
        youPanel = new javax.swing.JPanel();
        typingTextField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatEditorPane = new javax.swing.JEditorPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        //jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Serveur du jeu de carre");

        boardPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        boardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                boardPanelMouseClicked(evt);
            }
        });
        boardPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                boardPanelMouseMoved(evt);
            }
        });

        javax.swing.GroupLayout boardPanelLayout = new javax.swing.GroupLayout(boardPanel);
        boardPanel.setLayout(boardPanelLayout);
        boardPanelLayout.setHorizontalGroup(
            boardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 521, Short.MAX_VALUE)
        );
        boardPanelLayout.setVerticalGroup(
            boardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 523, Short.MAX_VALUE)
        );

        backButton.setText("Revenir");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        myselfPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        myselfPanel.setPreferredSize(new java.awt.Dimension(150, 150));

        javax.swing.GroupLayout myselfPanelLayout = new javax.swing.GroupLayout(myselfPanel);
        myselfPanel.setLayout(myselfPanelLayout);
        myselfPanelLayout.setHorizontalGroup(
            myselfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        myselfPanelLayout.setVerticalGroup(
            myselfPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );

        youPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        youPanel.setPreferredSize(new java.awt.Dimension(150, 150));

        javax.swing.GroupLayout youPanelLayout = new javax.swing.GroupLayout(youPanel);
        youPanel.setLayout(youPanelLayout);
        youPanelLayout.setHorizontalGroup(
            youPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );
        youPanelLayout.setVerticalGroup(
            youPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );

        typingTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typingTextFieldActionPerformed(evt);
            }
        });

        sendButton.setText("Envoyer");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(chatEditorPane);

        fileMenu.setText("Fichier");

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setText("Sortie");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        /*jMenu2.setText("Options");
        menuBar.add(jMenu2);*/

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(boardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(myselfPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(youPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(typingTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendButton))
                    .addComponent(backButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(youPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(myselfPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(typingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sendButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(backButton))
                    .addComponent(boardPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }

    private void boardPanelMouseClicked(java.awt.event.MouseEvent evt) {
        if (isPause) {
            return;
        }

        Point p = new Point();
        if ((currentColumn < Size && currentColumn >= 0 && currentRow < Size && currentRow >= 0)) {
            p = new Point(currentColumn, currentRow);
        } else {
            return;
        }
        if (!checked.contains(p)) {
            checked.add(p);
            currentPoint = new Point(p);
            currentColumn = -1;
            currentRow = -1;
            boardPanel.repaint();
            try {
                outGame.writeObject(currentPoint);
            } catch (IOException ex) {
                Logger.getLogger(ServerJeu.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (isWin(true)) {
                JOptionPane.showMessageDialog(this, "Bravo, vous avez gagne la partie!");
                checked.removeAllElements();
                startUser = false;
            }
            user = false;
            myself.suspend();
            you.resume();
            myselfPanel.setBorder(new LineBorder(Color.RED));
        }
        isPause = true;
        boardPanel.repaint();

    }

    private void boardPanelMouseMoved(java.awt.event.MouseEvent evt) {
        int CX = evt.getX();
        int CY = evt.getY();
        if (CY - Y0 < 0) {
            currentColumn = (CX - X0) / Width;
            currentRow = -1 + (CY - Y0) / Width;
            return;
        }
        if (CX - X0 < 0) {
            currentColumn = -1 + (CX - X0) / Width;
            currentRow = (CY - Y0) / Width;
            return;
        }
        currentColumn = (CX - X0) / Width;
        currentRow = (CY - Y0) / Width;
        Point p = new Point(currentColumn, currentRow);
        if (checked.contains(p)) {
            currentColumn = -1;
            currentRow = -1;

        }
        boardPanel.repaint();
        boardPanel.validate();
    }

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (checked.size() == 0) {
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment annuler?", "Attention", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            user = !user;
            checked.remove(checked.size() - 1);
            boardPanel.repaint();
        }
    }

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            String s = typingTextField.getText();
            if (s.length() == 0) {
                return;
            }
            Vector d = new Vector();
            d.add(s);
            out.writeObject(d);
            chatEditorPane.setText(chatEditorPane.getText() + "Server : " + s + "\n");
            typingTextField.setText("");
        } catch (IOException ex) {
            Logger.getLogger(ServerJeu.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void typingTextFieldActionPerformed(java.awt.event.ActionEvent evt) {
        sendButtonActionPerformed(null);
    }
    
    public boolean isWin(boolean user) {
        int n = 6;
        int ok = 0;
        int jeu = 0;
        int u; 
        if (startUser) {
            if (user) {
                u = 0;
            } else {
                u = 1;
            }
        } else {
            if (user) {
                u = 1;
            } else {
                u = 0;
            }
        }
        
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x + i, currentPoint.y);
            if (!(p.x < Size)) {
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                	jeu++;
                }
                break;
            }
        }
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x - i, currentPoint.y);
            if (!(p.x >= 0)) {
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                	jeu++;
                }
                break;
            }
        }
        if (ok == 4 && jeu != 2) {
            return true;
        }
  
        ok = 0;
        jeu = 0;
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x, currentPoint.y + i);
            if (!(p.y < Size)) {
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                	jeu++;
                }
                
                break;
            }
        }
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x, currentPoint.y - i);
            if (!(p.y >= 0)) {
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                	jeu++;
                }
                break;
            }
        }
        if (ok == 4 && jeu != 2) {
            return true;
        }

        ok = 0;
        jeu = 0;
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x + i, currentPoint.y + i);
            if (!(p.x >= 0 && p.x < Size && p.y >= 0 && p.y < Size)) {
           
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                	jeu++;
                }
                
                break;
            }
        }
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x - i, currentPoint.y - i);
            if (!(p.x >= 0 && p.x < Size && p.y >= 0 && p.y < Size)) {
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                	jeu++;
                }
                
                break;
            }
        }
        if (ok == 4 && jeu != 2) {
            return true;
        }
        
        ok = 0;
        jeu = 0;
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x + i, currentPoint.y - i);
            if (!(p.x >= 0 && p.x < Size && p.y >= 0 && p.y < Size)) {
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                	jeu++;
                }
                
                break;
            }
        }
        for (int i = 1; i < n; i++) {
            Point p = new Point(currentPoint.x - i, currentPoint.y + i);
            if (!(p.x >= 0 && p.x < Size && p.y >= 0 && p.y < Size)) {
                break;
            }
            if (checked.contains(p) && checked.indexOf(p) % 2 == u) {
                ok++;
            }
            if ((checked.contains(p) && checked.indexOf(p) % 2 != u) || !checked.contains(p)) {
                if (checked.contains(p) && checked.indexOf(p) % 2 != u) {
                	jeu++;
                }
                
                break;
            }
        }
        if (ok == 4 && jeu != 2) {
            return true;
        }
        return false;
    }

    public void listenSocket() {

        try {
            server = new ServerSocket(1234);
            Socket socket = server.accept();
            OutputStream o = socket.getOutputStream();
            out = new ObjectOutputStream(o);
            InputStream i = socket.getInputStream();
            in = new ObjectInputStream(i);
        } catch (IOException e) {
            System.out.println("Ne peut pas ecouter sur le port 4444");
            System.exit(-1);
        }

        while (true) {
            try {
                Vector s = null;
                try {
                    s = (Vector) in.readObject();
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ServerJeu.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.toFront();
                chatEditorPane.setText(chatEditorPane.getText() + "Client : " + s.get(0).toString() + "\n");
            } catch (IOException ex) {
            }
        }
    }

    public void listenSocketGamne() {

        try {
            serverGame = new ServerSocket(1235);
            Socket socket = serverGame.accept();
            OutputStream o = socket.getOutputStream();
            outGame = new ObjectOutputStream(o);
            InputStream i = socket.getInputStream();
            inGame = new ObjectInputStream(i);
        } catch (IOException e) {
            System.out.println("Ne peut pas ecouter sur le port 4444");
            System.exit(-1);
        }

        while (true) {
            try {
                Point s = null;
                try {
                    s = (Point) inGame.readObject();
                    checked.add(s);
                    currentPoint = s;
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ServerJeu.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.toFront();
                boardPanel.repaint();
                if (isWin(false)) {
                    JOptionPane.showMessageDialog(this, "Desole, tu as perdu la partie!");
                    startUser = true;
                    checked.removeAllElements();
                    boardPanel.repaint();
                }
                user = true;
                isPause = false;
                you.suspend();
                myself.resume();
                youPanel.setBorder(new LineBorder(Color.BLACK));

            } catch (IOException ex) {
            }
        }
    }
public void createTrayIcon(){
    //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray n'est pas supporte");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon =
                new TrayIcon(img,"Tray Icon");
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        MenuItem aboutItem = new MenuItem("About");
        CheckboxMenuItem cb1 = new CheckboxMenuItem("Set auto size");
        CheckboxMenuItem cb2 = new CheckboxMenuItem("Set tooltip");
        Menu displayMenu = new Menu("Display");
        MenuItem errorItem = new MenuItem("Error");
        MenuItem warningItem = new MenuItem("Warning");
        MenuItem infoItem = new MenuItem("Info");
        MenuItem noneItem = new MenuItem("None");
        MenuItem exitItem = new MenuItem("Exit");

        //Add components to pop-up menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(cb1);
        popup.add(cb2);
        popup.addSeparator();
        popup.add(displayMenu);
        displayMenu.add(errorItem);
        displayMenu.add(warningItem);
        displayMenu.add(infoItem);
        displayMenu.add(noneItem);
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon ne peut pas etre ajoute.");
        }
}
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new ServerJeu();
            }
        });
    }
    // Variables declaration 
    private javax.swing.JButton backButton;
    private javax.swing.JPanel boardPanel;
    private javax.swing.JEditorPane chatEditorPane;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    //private javax.swing.JMenu jMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JPanel myselfPanel;
    private javax.swing.JButton sendButton;
    private javax.swing.JTextField typingTextField;
    private javax.swing.JPanel youPanel;
    
    // End of variables declaration
    private int X0 = 20;
    private int Y0 = 20;
    private int Width = 32;
    private int Size = 15;
    private int currentRow = -1;
    private int currentColumn = -1;
    private Point currentPoint = new Point();
    
    private boolean user = true;
    
    private Vector<Point> checked = new Vector<Point>();
    PlayNow myself;
    PlayNow you;
    ServerSocket server = null;
    Socket client = null;
    ServerSocket serverGame = null;
    Socket clientGame = null;
    ObjectInputStream in = null;
    ObjectOutputStream out = null;
    ObjectInputStream inGame = null;
    ObjectOutputStream outGame = null;
    String line;
    boolean isPause = false;
    boolean startUser = true;
    private Image img = new ImageIcon(this.getClass().getResource(
            "/images/im.png")).getImage();
}

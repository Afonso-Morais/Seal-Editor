/*
* SPDX-License-Identifier: ISC
*
* Copyright (c) 2024 Afonso L. Morais <moraisafonso@protonmail.com>
*
* Permission to use, copy, modify, and distribute this software for any
* purpose with or without fee is hereby granted, provided that the above
* copyright notice and this permission notice appear in all copies.
*
* THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
* WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
* MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
* ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
* WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
* ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
* OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
*/

/*==== main.java ======================================
 * This program (Seal Editor) is just a simple text
 * editor to replace overcomplicated text editors with
 * uneeded features.
 *
 * Date: September, 2023
 * Author: Afonso Morais
 *=====================================================*/


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


public class main {
    private JFrame frame;
    private JTextArea textArea;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem exitMenuItem;
    private JMenu viewMenu;
    private JMenuItem toggleThemeMenuItem;
    private JMenuItem toggleFullscreenMenuItem;
    private JMenu helpMenu;
    private JMenuItem aboutMenuItem;
    private JPanel mainPanel;

    private int WIDTH = 800;
    private int HEIGHT = 600;

    private File currentFile;
    private float fontSize = 12.0f;
    private boolean darkTheme = false;
    private boolean fullscreen = false;
    private boolean unsavedChanges = false;
    private boolean isNewFile = true;

    private Image image;
    
    public main() {
        frame = new JFrame("Seal Editor - Text Editor");
        frame.setSize(800, 600); 
        frame.setLocationRelativeTo(null);  // Center the frame on the screen
        frame.setFocusable(true);           // Set so frame can be focused
        frame.requestFocusInWindow();       // Request focus on the frame

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                askToSaveAndExit();
            }
        });

        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream iconStream = classLoader.getResourceAsStream("icon.png");
            ImageIcon icon = new ImageIcon(ImageIO.read(iconStream));
            frame.setIconImage(icon.getImage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainPanel = new JPanel(new BorderLayout());

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, (int) fontSize));
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Create the menu bar
        menuBar = new JMenuBar();

        // File menu
        fileMenu = new JMenu("File");
        newMenuItem = new JMenuItem("New File");
        openMenuItem = new JMenuItem("Open File");
        saveMenuItem = new JMenuItem("Save File");
        exitMenuItem = new JMenuItem("Exit");

        newMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                main newWindow = new main();
                newWindow.frame.setVisible(true);

            }
        });

        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                
                // Create filters
                FileNameExtensionFilter textFlt = new FileNameExtensionFilter(".txt", "txt");
                FileNameExtensionFilter configFlt = new FileNameExtensionFilter(".config", "config");
                FileNameExtensionFilter logFlt = new FileNameExtensionFilter(".log", "log");
                FileNameExtensionFilter xmlFlt = new FileNameExtensionFilter(".xml", "xml");
                FileNameExtensionFilter jsonFlt = new FileNameExtensionFilter(".json", "json");
                FileNameExtensionFilter mdFlt = new FileNameExtensionFilter(".md", "md");
                FileNameExtensionFilter shFlt = new FileNameExtensionFilter(".sh", "sh");
                FileNameExtensionFilter batFlt = new FileNameExtensionFilter(".bat", "bat");

                // Add the filters
                fileChooser.setFileFilter(textFlt);
                fileChooser.setFileFilter(configFlt);
                fileChooser.setFileFilter(logFlt);
                fileChooser.setFileFilter(xmlFlt);
                fileChooser.setFileFilter(jsonFlt);
                fileChooser.setFileFilter(mdFlt);
                fileChooser.setFileFilter(shFlt);
                fileChooser.setFileFilter(batFlt);

                // Set all files as default
                fileChooser.setFileFilter(null);
                fileChooser.setAcceptAllFileFilterUsed(true); 

                int choice = fileChooser.showOpenDialog(frame);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    currentFile = fileChooser.getSelectedFile();
                    readFile();
                }
            }
        });

        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                askToSaveAndExit();
            }
        });

        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        // View menu
        viewMenu = new JMenu("View");
        toggleThemeMenuItem = new JMenuItem("Toggle Theme");
        toggleFullscreenMenuItem = new JMenuItem("Toggle Fullscreen");

        toggleThemeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleTheme();
            }
        });

        toggleFullscreenMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                toggleFullscreen();
            }
        });

        viewMenu.add(toggleThemeMenuItem);
        viewMenu.add(toggleFullscreenMenuItem);

        // Help menu
        helpMenu = new JMenu("Help");
        aboutMenuItem = new JMenuItem("About");

        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutWindow();
            }
        });

        helpMenu.add(aboutMenuItem);

        // Add menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        // Set the menu bar for the frame
        frame.setJMenuBar(menuBar);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        frame.add(mainPanel);

        frame.setVisible(true);

        textArea.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    int rotation = e.getWheelRotation();
                    fontSize += (rotation > 0) ? -2.0f : 2.0f;
                    textArea.setFont(new Font("Monospaced", Font.PLAIN, (int) fontSize));
                } else if (e.isShiftDown()) {
                    JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
                    int newValue = horizontalScrollBar.getValue() + (e.getUnitsToScroll() * 40);
                    horizontalScrollBar.setValue(newValue);
                } else {
                    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                    int newValue = verticalScrollBar.getValue() + (e.getUnitsToScroll() * 40);
                    verticalScrollBar.setValue(newValue);
                }
            }
        });

        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_S) {
                    saveFile();
                }else if(e.getKeyCode() == KeyEvent.VK_F11){
                    toggleFullscreen();
                }
            }
        });

        //check for changes
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                unsavedChanges = true;
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                unsavedChanges = true;
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                unsavedChanges = true;
            }
        });

        // Create popup menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");
        JMenuItem cutItem = new JMenuItem("Cut");
        JMenuItem newFileItem = new JMenuItem("New File");
        JMenuItem toggleThemeItem = new JMenuItem("Toggle Theme");
        JMenuItem exitItem = new JMenuItem("Exit");

        // Add action listeners to menu items
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());
        cutItem.addActionListener(e -> textArea.cut());
        newFileItem.addActionListener(e -> newFile());
        toggleThemeItem.addActionListener(e -> toggleTheme());
        exitItem.addActionListener(e -> frame.dispose());

        // Add menu items to popup menu
        popupMenu.add(copyItem);
        popupMenu.add(pasteItem);
        popupMenu.add(cutItem);
        popupMenu.add(new JSeparator());
        popupMenu.add(newFileItem);
        popupMenu.add(toggleThemeItem);
        popupMenu.add(exitItem);

        // Add mouse listener to text area
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

    }

    // Get file content
    private void readFile() {
        if (currentFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                textArea.setText(content.toString());
                unsavedChanges = false;
                isNewFile = false; // Set isNewFile to false since the file has been opened
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    

    //save
    private void saveFile() {
        if (isNewFile || currentFile == null) {
            // If it's a new file or currentFile is not set, prompt for a save location
            JFileChooser fileChooser = new JFileChooser();

            // Create filters
            FileNameExtensionFilter textFlt = new FileNameExtensionFilter(".txt", "txt");
            FileNameExtensionFilter configFlt = new FileNameExtensionFilter(".config", "config");
            FileNameExtensionFilter logFlt = new FileNameExtensionFilter(".log", "log");
            FileNameExtensionFilter xmlFlt = new FileNameExtensionFilter(".xml", "xml");
            FileNameExtensionFilter jsonFlt = new FileNameExtensionFilter(".json", "json");
            FileNameExtensionFilter mdFlt = new FileNameExtensionFilter(".md", "md");
            FileNameExtensionFilter shFlt = new FileNameExtensionFilter(".sh", "sh");
            FileNameExtensionFilter batFlt = new FileNameExtensionFilter(".bat", "bat");

            // Add the filters
            fileChooser.setFileFilter(textFlt);
            fileChooser.setFileFilter(configFlt);
            fileChooser.setFileFilter(logFlt);
            fileChooser.setFileFilter(xmlFlt);
            fileChooser.setFileFilter(jsonFlt);
            fileChooser.setFileFilter(mdFlt);
            fileChooser.setFileFilter(shFlt);
            fileChooser.setFileFilter(batFlt);

            // Set all files as default
            fileChooser.setFileFilter(null);
            fileChooser.setAcceptAllFileFilterUsed(true);


            int choice = fileChooser.showSaveDialog(frame);
    
            if (choice == JFileChooser.APPROVE_OPTION) {
                currentFile = fileChooser.getSelectedFile();
            } else {
                return; // User canceled the save operation
            }
        }
    
        // Now save the content to the selected/current file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
            writer.write(textArea.getText());
            unsavedChanges = false;
            isNewFile = false; // Set isNewFile to false since the file has been saved
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //dark/light theme
    private void toggleTheme() {
        darkTheme = !darkTheme;

        if (darkTheme) {
            frame.getContentPane().setBackground(Color.BLACK);
            textArea.setBackground(Color.DARK_GRAY);
            textArea.setForeground(Color.WHITE);
            textArea.setCaretColor(Color.WHITE);
        } else {
            frame.getContentPane().setBackground(Color.WHITE);
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
            textArea.setCaretColor(Color.BLACK);
        }
    }

   private void toggleFullscreen() {
        fullscreen = !fullscreen;

        frame.setVisible(false);

        GraphicsDevice gd = getGraphicsDeviceForFrame(frame);
        if (gd != null && gd.isFullScreenSupported()) {
            if (fullscreen) {
                WIDTH = frame.getWidth();               // Save current window width
                HEIGHT = frame.getHeight();             // Save current window height
                frame.dispose();                        // Dispose the frame before going fullscreen
                frame.setUndecorated(true);
                gd.setFullScreenWindow(frame);
            } else {
                frame.dispose();
                gd.setFullScreenWindow(null);
                frame.setUndecorated(false);
                frame.setSize(WIDTH, HEIGHT);           // Set the size back to the saved size after exiting fullscreen mode 
                frame.setVisible(true);                 // Set the frame visible again
            }
        } else {
            System.err.println("Full-screen mode not supported");
        }
    } 

    private GraphicsDevice getGraphicsDeviceForFrame(JFrame frame) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();
        for (GraphicsDevice device : devices) {
            GraphicsConfiguration config = device.getDefaultConfiguration();
            Rectangle bounds = config.getBounds();
            if (bounds.contains(frame.getLocation())) {
                return device;
            }
        }
        return null;
    }

    // Create about window
    private void showAboutWindow() {
    JFrame aboutFrame = new JFrame("About"); 
    aboutFrame.setSize(400, 250); 
    aboutFrame.setResizable(false);
    aboutFrame.setLocationRelativeTo(frame);  // Set centered relative to main frame

    try {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream iconStream = classLoader.getResourceAsStream("icon.png");
        ImageIcon icon = new ImageIcon(ImageIO.read(iconStream));
        aboutFrame.setIconImage(icon.getImage());
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    // Get icon image just to draw in the about frame
    try {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream imageStream = classLoader.getResourceAsStream("icon.png");
        image = ImageIO.read(imageStream);
    } catch (IOException e) {
        e.printStackTrace();
    }

    JPanel aboutPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw the image at (0, 0) on the panel
            g.drawImage(image, 200 - 30, 10, 60, 60, this);
        }
    };

    JTextArea aboutTextArea = new JTextArea("\n\n\n\n\n  Seal Editor v1.2.0"
                                            + "\n  Made by Afonso Morais."
                                            + "\n  Contact me at moraisafonso@protonmail.com"
                                            + "\n\n  You can report any bug or see the source code here:\n  https://github.com/Afonso-Morais/Seal-Editor"
                                            + "\n\n  Thank you for using the Seal Editor!");
    aboutTextArea.setOpaque(false);
    aboutTextArea.setEditable(false);
    aboutPanel.add(aboutTextArea);
    aboutFrame.add(aboutPanel); // Add the aboutPanel to the aboutFrame
    aboutFrame.setVisible(true);
}


    //unsaved changes
    private void askToSaveAndExit() {
        if (unsavedChanges) {
            int choice = JOptionPane.showConfirmDialog(frame, "You have unsaved changes. Do you want to save them before exiting?",
                    "Confirm Exit", JOptionPane.YES_NO_CANCEL_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                saveFile();
                frame.dispose();
            } else if (choice == JOptionPane.NO_OPTION) {
                frame.dispose();
            }
        } else {
            frame.dispose();
        }
    }    

    private void newFile(){
        main newWindow = new main();
        newWindow.frame.setVisible(true);
    }

    private void loadFileFromCommandLine(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            currentFile = file;
            readFile();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            main editor = new main();
            if (args.length > 0) {
                editor.loadFileFromCommandLine(args[0]);
            }
        });
    }
}

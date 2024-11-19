import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;

public class DataStreamGUI extends JFrame
{
    JPanel mainPnl;

    JPanel origDisplayPnl;
    JTextArea origDisplayTA;
    JScrollPane origScroller;

    JPanel displayPnl;

    JPanel newDisplayPnl;
    JTextArea newDisplayTA;
    JScrollPane newScroller;

    JPanel optionPnl;
    JLabel fileLbl;
    JTextField fileTF;
    JButton openFileBtn;
    JLabel searchLbl;
    JTextField searchTF;

    JPanel controlPnl;
    JButton searchBtn;
    JButton quitBtn;

    JFileChooser chooser;

    public DataStreamGUI()
    {
        mainPnl = new JPanel();
        mainPnl.setLayout(new BorderLayout());

        CreateOptionsPanel();
        mainPnl.add(optionPnl, BorderLayout.NORTH);

        CreateDisplayPanel();
        mainPnl.add(displayPnl, BorderLayout.CENTER);

        CreateControlPanel();
        mainPnl.add(controlPnl, BorderLayout.SOUTH);

        add(mainPnl);
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        setSize(screenWidth * 2 / 4, screenHeight * 5 / 8);
        setLocation(screenWidth / 2, screenHeight / 8);
        setTitle("Data Stream Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void CreateControlPanel()
    {
        controlPnl = new JPanel();
        controlPnl.setLayout(new GridLayout(1, 2));

        searchBtn = new JButton("Search");
        searchBtn.addActionListener((ActionEvent ae) ->
        {
            String searchString = searchTF.getText();
            if (searchString.isEmpty())
            {
                JOptionPane.showMessageDialog(mainPnl, "Please enter a search string", "Error", JOptionPane.WARNING_MESSAGE);
            }
            if (chooser.getSelectedFile() == null)
            {
                JOptionPane.showMessageDialog(mainPnl, "Please select a file first", "Error", JOptionPane.WARNING_MESSAGE);
            }
            try
            {
                File selectedFile = chooser.getSelectedFile();
                Path file = selectedFile.toPath();
                try (Stream<String> lines = Files.lines(file))
                {
                  String filteredText = lines.filter(line -> line.contains(searchString)).collect(Collectors.joining("\n"));
                  newDisplayTA.setText(filteredText);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        quitBtn = new JButton("Quit");
        quitBtn.addActionListener((ActionEvent ae) ->
        {
            int selectedOption = JOptionPane.showConfirmDialog(null, "Do you want to quit?", "Quit", JOptionPane.YES_NO_OPTION);
            if (selectedOption == JOptionPane.YES_OPTION)
            {
                System.exit(0);
            }
        });

        controlPnl.add(searchBtn);
        controlPnl.add(quitBtn);
    }

    private void CreateDisplayPanel()
    {
        displayPnl = new JPanel();
        displayPnl.setLayout(new GridLayout(1, 2));

        CreateOriginalDisplayPanel();
        displayPnl.add(origDisplayPnl);

        CreateNewDisplayPanel();
        displayPnl.add(newDisplayPnl);

    }

    private void CreateNewDisplayPanel()
    {
        newDisplayPnl = new JPanel();
        newDisplayPnl.setLayout(new GridLayout(1, 1));
        newDisplayPnl.setBorder(new TitledBorder(new EtchedBorder(), "Filtered File"));
        newDisplayTA = new JTextArea(10,15);
        newDisplayTA.setEditable(false);
        newScroller = new JScrollPane(newDisplayTA);
        newDisplayPnl.add(newScroller);
    }

    private void CreateOriginalDisplayPanel()
    {
        origDisplayPnl = new JPanel();
        origDisplayPnl.setLayout(new GridLayout(1, 1));
        origDisplayPnl.setBorder(new TitledBorder(new EtchedBorder(), "Original File"));
        origDisplayTA = new JTextArea(10,15);
        origDisplayTA.setEditable(false);
        origScroller = new JScrollPane(origDisplayTA);
        origDisplayPnl.add(origScroller);
    }

    private void CreateOptionsPanel()
    {
        optionPnl = new JPanel();
        optionPnl.setLayout(new GridLayout(1, 5));

        fileLbl = new JLabel("File: ");
        fileTF = new JTextField(20);
        fileTF.setEditable(false);
        openFileBtn = new JButton("Open File");
        openFileBtn.addActionListener((ActionEvent ae) ->
        {
            chooser = new JFileChooser();
            File selectedFile;
            String rec = "";
            // uses a fixed known path:
            //  Path file = Paths.get("c:\\My Documents\\data.txt");

            // use the toolkit to get the current working directory of the IDE
            // Not sure if the toolkit is thread safe...
            File workingDirectory = new File(System.getProperty("user.dir"));

            // Typiacally, we want the user to pick the file so we use a file chooser
            // kind of ugly code to make the chooser work with NIO.
            // Because the chooser is part of Swing it should be thread safe.
            chooser.setCurrentDirectory(workingDirectory);
            // Using the chooser adds some complexity to the code.
            // we have to code the complete program within the conditional return of
            // the filechooser because the user can close it without picking a file

            if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                try{
                    selectedFile = chooser.getSelectedFile();
                    Path file = selectedFile.toPath();
                    String fileText = Files.lines(file).collect(Collectors.joining("\n"));
                    origDisplayTA.setText(fileText);
                    System.out.println("\n\nData file read!");
                    fileTF.setText(selectedFile.getPath());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }

        });

        searchLbl = new JLabel("Enter Search String: ");
        searchTF = new JTextField(20);
        searchTF.setEditable(true);

        optionPnl.add(fileLbl);
        optionPnl.add(fileTF);
        optionPnl.add(openFileBtn);
        optionPnl.add(searchLbl);
        optionPnl.add(searchTF);

    }
}

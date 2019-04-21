package assign3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class MetropolisView extends JFrame {
    private JTextField continent, metropolis, population;
    private JButton add, search;
    private JComboBox populationBox, exactBox;
    private MetropolisController controller;
    private JTable table;

    private static final int FIELD_SIZE = 12;
    private static final int APPLICATION_WIDTH = 600;
    private static final int APPLICATION_HEIGHT= 400;

    private static String LARGER_THAN = "Larger than";
    private static String SMALLER_THAN = "Smaller than or equal to";
    private static String EXACT_MATCH = "Exact match";
    private static String PARTIAL_MATCH = "Partial match";

    public MetropolisView(){
        super("Metropolis View");
        setLayout(new BorderLayout());
        initFields();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        controller = new MetropolisController();
        addAddListener();
        addSearchListener();
    }

    private void addSearchListener() {
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("kaikai");
                controller.addInfo(metropolis.getText().trim(), continent.getText().trim(), population.getText().trim());
                ResultSet rs = controller.searchInfo(metropolis.getText().trim(), continent.getText().trim(), population.getText().trim(), false, true);
            }
        });
    }

    private void addAddListener() {
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean indPop = false, indName = false;
                if (populationBox.getSelectedIndex() == 0){
                    indPop = true;
                }
                if (exactBox.getSelectedIndex() == 0){
                    indName = true;
                }
                ResultSet rs = controller.searchInfo(metropolis.getText().trim(), continent.getText().trim(), population.getText().trim(), indPop, indName);
            }
        });
    }

    private void initFields() {
        continent = new JTextField(FIELD_SIZE);
        metropolis = new JTextField(FIELD_SIZE);
        population = new JTextField(FIELD_SIZE);
        add = new JButton("Add");
        search = new JButton("Search");
        populationBox = new JComboBox();
        exactBox = new JComboBox();
        exactBox.setEditable(false);
        populationBox.setEditable(false);
        populationBox.addItem("Larger than");
        populationBox.addItem("Smaller than or equal to");
        exactBox.addItem("Exact match");
        exactBox.addItem("Partial match");


        Container contNorth = new Container();
        contNorth.setLayout(new FlowLayout());

        contNorth.add(new JLabel("Metropolis: "));
        contNorth.add(metropolis);
        contNorth.add(new JLabel("Continent: "));
        contNorth.add(continent);
        contNorth.add(new JLabel("Population: "));
        contNorth.add(population);

        add(contNorth, BorderLayout.NORTH);

        Container contEast = new Container();

        Box b = Box.createVerticalBox();
        Box searchBox = Box.createVerticalBox();
        searchBox.setBorder(new TitledBorder("Search options"));

        b.add(add);
        b.add(search);
        b.add(searchBox);
        searchBox.add(populationBox);
        searchBox.add(exactBox);

        add(b, BorderLayout.EAST);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        MetropolisView frame = new MetropolisView();
    }
}

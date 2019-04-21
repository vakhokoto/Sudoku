package assign3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MetropolisView extends JFrame {
    private JTextField continent, metropolis, population;
    private JButton add, search;
    private JComboBox populationBox, exactBox;
    private MetropolisController controller;
    private JTable table;

    private static final int FIELD_SIZE = 12;
    private static final int BOX_WIDTH = 150;
    private static final int BOX_HEIGHT= 50;

    private static String LARGER_THAN = "Larger than";
    private static String SMALLER_THAN = "Smaller than or equal to";
    private static String EXACT_MATCH = "Exact match";
    private static String PARTIAL_MATCH = "Partial match";

    public MetropolisView(){
        super("Metropolis View");

        setLayout(new BorderLayout());

        controller = new MetropolisController();
        table = new JTable(controller);
        JScrollPane scroll = new JScrollPane(table);

        add(scroll, BorderLayout.CENTER);

        initFields();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        addAddListener();
        addSearchListener();
    }

    /**
     * This method adds actionListeners of add
     * */
    private void addAddListener() {
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.addInfo(metropolis.getText().trim(), continent.getText().trim(), population.getText().trim());
                controller.searchInfo(metropolis.getText().trim(), continent.getText().trim(), population.getText().trim(), false, true);
            }
        });
    }

    /**
     * This method adds actionListeners of search
     * */
    private void addSearchListener() {
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
                controller.searchInfo(metropolis.getText().trim(), continent.getText().trim(), population.getText().trim(), indPop, indName);
            }
        });
    }

    /**
     * This method initializes all graphics objects
     * */
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
        populationBox.addItem(LARGER_THAN);
        populationBox.addItem(SMALLER_THAN);
        exactBox.addItem(EXACT_MATCH);
        exactBox.addItem(PARTIAL_MATCH);

        populationBox.setMaximumSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));
        exactBox.setMaximumSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));

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

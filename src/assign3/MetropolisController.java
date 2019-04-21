package assign3;

import javax.swing.table.AbstractTableModel;
import java.sql.*;

public class MetropolisController extends AbstractTableModel {
    private Connection connection;
    private static final String DROP = "drop table if exists metropolises;";
    private static final String COMMIT = "commit;";
    private static final String CREATE = "create table metropolises ( metropolis char(30), continent char(30), population int );";
    private static final String INSERT = "insert into metropolises values ";
    private static final String TABLE_NAME = "metropolises";
    private static final String POPULATION = "population";
    private static final String CONTINENT = "continent";
    private static final String METROPOLIS = "metropolis";
    private static final String[] NAMES = {METROPOLIS, CONTINENT, POPULATION};
    private int rowNum, columnNum = 3;
    private ResultSet rs = null;

    @Override
    public int getRowCount() {
        return rowNum;
    }

    @Override
    public String getColumnName(int column){
        return NAMES[column];
    }

    @Override
    public int getColumnCount() {
        return columnNum;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rs == null){
            return null;
        }
        try {
            rs.absolute(rowIndex + 1);
            return rs.getObject(columnIndex + 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MetropolisController(){
        rowNum = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(MyDBInfo.MYSQL_DATABASE_SERVER + MyDBInfo.MYSQL_DATABASE_NAME, MyDBInfo.MYSQL_USERNAME, MyDBInfo.MYSQL_PASSWORD);
            Statement st = null;
            st = connection.createStatement();
            st.executeUpdate(DROP);
            st.executeUpdate(CREATE);
            st.executeUpdate(COMMIT);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method inserts in DB new element
     * */
    public void addInfo(String metropolis, String continent, String population){
        if (!check(metropolis, continent, population)){
            return;
        }
        Statement st = null;
        try {
            st = connection.createStatement();
            st.executeUpdate(INSERT + "(\"" + metropolis + "\", " +"\"" + continent + "\", " + population + ");");
            st.executeUpdate(COMMIT);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method searches in DB
     * */
    public void searchInfo(String metropolis, String continent, String population, boolean exactPop, boolean nameMatch){
        for (int i=0; i<population.length(); ++i){
            if (!Character.isDigit(population.charAt(i))){
                return;
            }
        }
        metropolis = toLowerCase(metropolis);
        continent = toLowerCase(continent);
        String stat = "select * from " + TABLE_NAME;
        if (population.length() > 0 || continent.length() > 0 || metropolis.length() > 0) {
            stat += " where ";
        }
        if (exactPop && population.length() > 0) {
            stat += POPULATION + " > " + population;
        } else if (population.length() > 0){
            stat += POPULATION +  " <= " + population;
        }
        if (nameMatch && metropolis.length() > 0){
            if (population.length() > 0){
                stat += " and ";
            }
            stat += "lower(" + METROPOLIS + ")";
            stat += " = \'" + metropolis + "\'";
        } else if (metropolis.length() > 0){
            if (population.length() > 0){
                stat += " and ";
            }
            stat += "lower(" + METROPOLIS + ")";
            stat += " like \'%" + metropolis + "%\' ";
        }
        if (continent.length() > 0) {
            if (population.length() > 0 || metropolis.length() > 0){
                stat += " and ";
            }
            stat += "lower(" + CONTINENT + ")" + " = \'" + continent + "\'";
        }
        stat += ";";
        try {
            Statement st = connection.createStatement();
            rs = st.executeQuery(stat);
            rs.last();
            rowNum = rs.getRow();
            fireTableStructureChanged();
        } catch (Exception e){
        }
    }

    private String toLowerCase(String s){
        String ans = "";
        for (int i=0; i<s.length(); ++i){
            ans += Character.toLowerCase(s.charAt(i));
        }
        return ans;
    }

    /**
     * this method checks if data is valid
     * */
    private boolean check(String met, String cont, String pop){
        if (met.length() == 0 || cont.length() == 0 || pop.length() == 0){
            return false;
        }
        for (int i=0; i<pop.length(); ++i){
            if (!Character.isDigit(pop.charAt(i))){
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args){
        MetropolisController con = new MetropolisController();
    }
}

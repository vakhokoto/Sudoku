package assign3;

import java.sql.*;

public class MetropolisController {
    private Connection connection;
    private static final String CREATE = "create table metropolises ( metropolis char(30), continent char(30), population int );";
    private static final String INSERT = "insert into metropolises values ";
    private static final String TABLE_NAME = "metropolises";
    private static final String POPULATION = "population";
    private static final String CONTINENT = "continent";
    private static final String METROPOLIS = "metropolis";

    public MetropolisController(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(MyDBInfo.MYSQL_DATABASE_SERVER + MyDBInfo.MYSQL_DATABASE_NAME, MyDBInfo.MYSQL_USERNAME, MyDBInfo.MYSQL_PASSWORD);
            Statement st = null;
            st = connection.createStatement();
            st.executeUpdate(CREATE);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addInfo(String metropolis, String continent, String population){
        if (!check(metropolis, continent, population)){
            return;
        }
        Statement st = null;
        try {
            st = connection.createStatement();
            st.executeUpdate(INSERT + "(\"" + metropolis + "\", " +"\"" + continent + "\", " + population + "); ");
        } catch (SQLException e) {
//            System.out.println("shecdoma");
            e.printStackTrace();
        }
    }

    public ResultSet searchInfo(String metropolis, String continent, String population, boolean exactPop, boolean nameMatch){
        for (int i=0; i<population.length(); ++i){
            if (!Character.isDigit(population.charAt(i))){
                return null;
            }
        }
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
            stat += METROPOLIS;
            stat += " = \'" + metropolis + "\'";
        } else if (metropolis.length() > 0){
            if (population.length() > 0){
                stat += " and ";
            }
            stat += METROPOLIS;
            stat += " like \'%" + metropolis + "%\' ";
        }
        if (continent.length() > 0) {
            if (population.length() > 0 || metropolis.length() > 0){
                stat += " and ";
            }
            stat += CONTINENT + " = \'" + continent + "\'";
        }
        ResultSet rs = null;
        try {
            Statement st = connection.createStatement();
            rs = st.executeQuery(stat);
//            System.out.println(stat);
        } catch (Exception e){
        }
        return rs;
    }

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

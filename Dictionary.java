package heppic;

import java.sql.*;
/**
 *
 * @author Evaldas Karpys
 */
public class Dictionary {
    
   Connection c = null;
   Statement stmt = null;
   Statement stmt2 = null;
   Statement stmt3 = null;
   
   //dictionary constructor
   //establishes connection to sqllite database.db located in resources folder
    public Dictionary(){
        String url = "jdbc:sqlite:src\\resources\\database\\" + "database.db";
       
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(url);
                c.setAutoCommit(false);

                stmt = c.createStatement();
                stmt2 = c.createStatement();
                stmt3 = c.createStatement();

                }
   
                catch ( Exception e ) {
                  System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                  System.exit(0);
               }
                }
      

    /**
 * adjustWeights main logic - adjusts the weights in database for a given occasion core words (adjectives)
 * adjectives are stored in database.db in resources folder of the java project
 * adjectives are stored in respectful tables; birthdays, easter and christmas
 * in practice we could store large finite number of adjectives, thus making the program more creative by providing larger search space
 * @param rating - int, user submitted rating for current greeting
 * @param occasion - string, comes from adjustWeights in the ControlPoint class
 * 
 * HOW RATINGS ADJUST THE WEIGHTS:
 *  1 - Very bad, highly decrease weight of MAX(weight) word and highly increase complement word weights
 *  2 - Bad, slightly decrease weight of MAX(weight) word and slightly increase complement word weights
 *  3 - Do nothing, middle point is considered that user neither cares or doesn't care, so leave as it is
 *  4 - Good, very slightly increase weight of MAX(weight) word and even less increase the complement word weights
 *  5 - Very good, increase weight of MAX(weight) word and slightly decrease complement word weights
 */
    public void adjustWeights(int rating, String occasion){
        /*NOTE!!
        * 
        * Only Birthday case 1 is commented on its functionality as the same pattern just with different weight increase/decrease values
        * follow throughout all of the cases, except case where rating is 3 - where we do nothing
        *
        * I also use seperate query statements for each individual query so there is less confusion
        * they also leave less space for unexpected errors, where statements would get mixed or override each other
        */
        switch (occasion){
            //**************************** OCCASION BIRTHDAY ************************//
            case "birthday":
                switch(rating){
        
                    case 1: //************************ WHEN RATING == 1 ******************//
                        try{
                            
                ResultSet rs = stmt.executeQuery( "SELECT weight, MAX(weight) FROM birthdays;");
      
                    int weight_max = 0; //init max weight, will change value below in the if statement
                    if ( rs.next() ) {
       
                        weight_max = rs.getInt("weight"); //get current max weight
                        int n_weight_max = weight_max-7; //init new max weight in this case decrease by 7
                        String sql_update_max = "UPDATE birthdays set weight = "+n_weight_max+" where weight = "+weight_max+";";
                        stmt2.executeUpdate(sql_update_max);
                        c.commit();
                        //update weight_max to basically new max weight, because we will need to reuse it to find weights < than new max
                        weight_max -= 7; //if we don't update weight_max then in cases where max_weight is decreased it will roll back later
                    }
     
                     //get weights < than weight_max, if weight max was not updated then in following while loop it would roll back with +9
                    ResultSet rs2 = stmt3.executeQuery( "SELECT weight FROM birthdays WHERE weight < "+ weight_max +";");
                    //this loops through all of the results where weight value is < than max_weight
                    while ( rs2.next() ){
                        int weight_min = rs2.getInt("weight"); //get current min weight
                        int n_weight_min = weight_min+9;
                        String sql_update_min = "UPDATE birthdays set weight = "+n_weight_min+" where weight = "+weight_min+";";

                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate(sql_update_min);
                        c.commit();
                    
                    }
                    
                        //close all database related statements to reuse them in the next greeting
                        rs.close();
                        rs2.close();
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                    
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }

                        break;

                        
                    case 2: //************************ WHEN RATING == 2 ******************//
                        try{
                            
                ResultSet rs = stmt.executeQuery( "SELECT weight, MAX(weight) FROM birthdays;");
      
                    int weight_max = 0;
                    if ( rs.next() ) {
       
                        weight_max = rs.getInt("weight");
                        int n_weight_max = weight_max-4;
                        String sql_update_max = "UPDATE birthdays set weight = "+n_weight_max+" where weight = "+weight_max+";";
                        stmt2.executeUpdate(sql_update_max);
                        c.commit();
                        weight_max -= 4;
                    }
     
                     
                    ResultSet rs2 = stmt3.executeQuery( "SELECT weight FROM birthdays WHERE weight < "+ weight_max +";");
                    while ( rs2.next() ){
                        int weight_min = rs2.getInt("weight");
                        int n_weight_min = weight_min+6;
                        String sql_update_min = "UPDATE birthdays set weight = "+n_weight_min+" where weight = "+weight_min+";";

                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate(sql_update_min);
                        c.commit();
                    
                    }
                    
                        rs.close();
                        rs2.close();
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                    
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }

                        break;

                    case 3: //************************ WHEN RATING == 3 ******************//
                        //do nothing middle point rating indicates that the user doesn't really care
                        //close database statements, otherwise in the next generation they will be taken, thus crashing program
                        try{
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;

                    case 4: //************************ WHEN RATING == 4 ******************//
                        try{
                            
                ResultSet rs = stmt.executeQuery( "SELECT weight, MAX(weight) FROM birthdays;");
      
                    int weight_max = 0;
                    if ( rs.next() ) {
       
                        weight_max = rs.getInt("weight");
                        int n_weight_max = weight_max+3;
                        String sql_update_max = "UPDATE birthdays set weight = "+n_weight_max+" where weight = "+weight_max+";";
                        stmt2.executeUpdate(sql_update_max);
                        c.commit();
                        weight_max += 3;
                    }
     
                     
                    ResultSet rs2 = stmt3.executeQuery( "SELECT weight FROM birthdays WHERE weight < "+ weight_max +";");
                    while ( rs2.next() ){
                        int weight_min = rs2.getInt("weight");
                        int n_weight_min = weight_min+1;
                        String sql_update_min = "UPDATE birthdays set weight = "+n_weight_min+" where weight = "+weight_min+";";

                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate(sql_update_min);
                        c.commit();
                    
                    }
                    
                        rs.close();
                        rs2.close();
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                    
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;

                    case 5: //************************ WHEN RATING == 5 ******************//
                        try{
                            
                ResultSet rs = stmt.executeQuery( "SELECT weight, MAX(weight) FROM birthdays;");
      
                    int weight_max = 0;
                    if ( rs.next() ) {
       
                        weight_max = rs.getInt("weight");
                        int n_weight_max = weight_max+5;
                        String sql_update_max = "UPDATE birthdays set weight = "+n_weight_max+" where weight = "+weight_max+";";
                        stmt2.executeUpdate(sql_update_max);
                        c.commit();
                        weight_max += 5;
                    }
     
                     
                    ResultSet rs2 = stmt3.executeQuery( "SELECT weight FROM birthdays WHERE weight < "+ weight_max +";");
                    while ( rs2.next() ){
                        int weight_min = rs2.getInt("weight");
                        int n_weight_min = weight_min-3;
                        String sql_update_min = "UPDATE birthdays set weight = "+n_weight_min+" where weight = "+weight_min+";";

                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate(sql_update_min);
                        c.commit();
                    
                    }
                    
                        rs.close();
                        rs2.close();
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                    
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;
                        }
                break;
                
                //**************************** OCCASION CHRISTMAS ************************//
                case "christmas":
                switch(rating){
        
                    case 1: //************************ WHEN RATING == 1 ******************//
                         try{
                            
                ResultSet rs = stmt.executeQuery( "SELECT weight, MAX(weight) FROM christmas;");
      
                    int weight_max = 0;
                    if ( rs.next() ) {
       
                        weight_max = rs.getInt("weight");
                        int n_weight_max = weight_max-7;
                        String sql_update_max = "UPDATE christmas set weight = "+n_weight_max+" where weight = "+weight_max+";";
                        stmt2.executeUpdate(sql_update_max);
                        c.commit();
                        weight_max -= 7;
                    }
     
                     
                    ResultSet rs2 = stmt3.executeQuery( "SELECT weight FROM christmas WHERE weight < "+ weight_max +";");
                    while ( rs2.next() ){
                        int weight_min = rs2.getInt("weight");
                        int n_weight_min = weight_min+9;
                        String sql_update_min = "UPDATE christmas set weight = "+n_weight_min+" where weight = "+weight_min+";";

                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate(sql_update_min);
                        c.commit();
                    
                    }
                    
                        rs.close();
                        rs2.close();
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                    
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;

                    case 2: //************************ WHEN RATING == 2 ******************//
                         try{
                            
                ResultSet rs = stmt.executeQuery( "SELECT weight, MAX(weight) FROM christmas;");
      
                    int weight_max = 0;
                    if ( rs.next() ) {
       
                        weight_max = rs.getInt("weight");
                        int n_weight_max = weight_max-4;
                        String sql_update_max = "UPDATE christmas set weight = "+n_weight_max+" where weight = "+weight_max+";";
                        stmt2.executeUpdate(sql_update_max);
                        c.commit();
                        weight_max -= 4;
                    }
     
                     
                    ResultSet rs2 = stmt3.executeQuery( "SELECT weight FROM christmas WHERE weight < "+ weight_max +";");
                    while ( rs2.next() ){
                        int weight_min = rs2.getInt("weight");
                        int n_weight_min = weight_min+6;
                        String sql_update_min = "UPDATE christmas set weight = "+n_weight_min+" where weight = "+weight_min+";";

                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate(sql_update_min);
                        c.commit();
                    
                    }
                    
                        rs.close();
                        rs2.close();
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                    
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;

                    case 3: //************************ WHEN RATING == 3 ******************//
                        //do nothing middle point rating indicates that the user doesn't really care
                        //close database statements, otherwise in the next generation they will be taken, thus crashing program
                        try{
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;

                    case 4: //************************ WHEN RATING == 4 ******************//
                        try{
                            
                ResultSet rs = stmt.executeQuery( "SELECT weight, MAX(weight) FROM christmas;");
      
                    int weight_max = 0;
                    if ( rs.next() ) {
       
                        weight_max = rs.getInt("weight");
                        int n_weight_max = weight_max+3;
                        String sql_update_max = "UPDATE christmas set weight = "+n_weight_max+" where weight = "+weight_max+";";
                        stmt2.executeUpdate(sql_update_max);
                        c.commit();
                        weight_max += 3;
                    }
     
                     
                    ResultSet rs2 = stmt3.executeQuery( "SELECT weight FROM christmas WHERE weight < "+ weight_max +";");
                    while ( rs2.next() ){
                        int weight_min = rs2.getInt("weight");
                        int n_weight_min = weight_min+1;
                        String sql_update_min = "UPDATE christmas set weight = "+n_weight_min+" where weight = "+weight_min+";";

                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate(sql_update_min);
                        c.commit();
                    
                    }
                    
                        rs.close();
                        rs2.close();
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                    
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;

                    case 5: //************************ WHEN RATING == 5 ******************//
                         try{
                            
                ResultSet rs = stmt.executeQuery( "SELECT weight, MAX(weight) FROM christmas;");
      
                    int weight_max = 0;
                    if ( rs.next() ) {
       
                        weight_max = rs.getInt("weight");
                        int n_weight_max = weight_max+5;
                        String sql_update_max = "UPDATE christmas set weight = "+n_weight_max+" where weight = "+weight_max+";";
                        stmt2.executeUpdate(sql_update_max);
                        c.commit();
                        weight_max += 5;
                    }
     
                     
                    ResultSet rs2 = stmt3.executeQuery( "SELECT weight FROM christmas WHERE weight < "+ weight_max +";");
                    while ( rs2.next() ){
                        int weight_min = rs2.getInt("weight");
                        int n_weight_min = weight_min-3;
                        String sql_update_min = "UPDATE christmas set weight = "+n_weight_min+" where weight = "+weight_min+";";

                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate(sql_update_min);
                        c.commit();
                    
                    }
                    
                        rs.close();
                        rs2.close();
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                    
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;
                        }
                break;
                
                //**************************** OCCASION EASTER ************************//
                case "easter":
                switch(rating){
        
                    case 1: //************************ WHEN RATING == 1 ******************//
                         try{
                            
                ResultSet rs = stmt.executeQuery( "SELECT weight, MAX(weight) FROM easter;");
      
                    int weight_max = 0;
                    if ( rs.next() ) {
       
                        weight_max = rs.getInt("weight");
                        int n_weight_max = weight_max-7;
                        String sql_update_max = "UPDATE easter set weight = "+n_weight_max+" where weight = "+weight_max+";";
                        stmt2.executeUpdate(sql_update_max);
                        c.commit();
                        weight_max -= 7;
                    }
     
                     
                    ResultSet rs2 = stmt3.executeQuery( "SELECT weight FROM easter WHERE weight < "+ weight_max +";");
                    while ( rs2.next() ){
                        int weight_min = rs2.getInt("weight");
                        int n_weight_min = weight_min+9;
                        String sql_update_min = "UPDATE easter set weight = "+n_weight_min+" where weight = "+weight_min+";";

                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate(sql_update_min);
                        c.commit();
                    
                    }
                    
                        rs.close();
                        rs2.close();
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                    
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;

                    case 2: //************************ WHEN RATING == 2 ******************//
                        try{
                            
                ResultSet rs = stmt.executeQuery( "SELECT weight, MAX(weight) FROM easter;");
      
                    int weight_max = 0;
                    if ( rs.next() ) {
       
                        weight_max = rs.getInt("weight");
                        int n_weight_max = weight_max-4;
                        String sql_update_max = "UPDATE easter set weight = "+n_weight_max+" where weight = "+weight_max+";";
                        stmt2.executeUpdate(sql_update_max);
                        c.commit();
                        weight_max -= 4;
                    }
     
                     
                    ResultSet rs2 = stmt3.executeQuery( "SELECT weight FROM easter WHERE weight < "+ weight_max +";");
                    while ( rs2.next() ){
                        int weight_min = rs2.getInt("weight");
                        int n_weight_min = weight_min+6;
                        String sql_update_min = "UPDATE easter set weight = "+n_weight_min+" where weight = "+weight_min+";";

                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate(sql_update_min);
                        c.commit();
                    
                    }
                    
                        rs.close();
                        rs2.close();
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                    
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;

                    case 3: //************************ WHEN RATING == 3 ******************//
                        //do nothing middle point rating indicates that the user doesn't really care
                        //close database statements, otherwise in the next generation they will be taken, thus crashing program
                        try{
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;

                    case 4: //************************ WHEN RATING == 4 ******************//
                        try{
                            
                ResultSet rs = stmt.executeQuery( "SELECT weight, MAX(weight) FROM easter;");
      
                    int weight_max = 0;
                    if ( rs.next() ) {
       
                        weight_max = rs.getInt("weight");
                        int n_weight_max = weight_max+3;
                        String sql_update_max = "UPDATE easter set weight = "+n_weight_max+" where weight = "+weight_max+";";
                        stmt2.executeUpdate(sql_update_max);
                        c.commit();
                        weight_max += 3;
                    }
     
                     
                    ResultSet rs2 = stmt3.executeQuery( "SELECT weight FROM easter WHERE weight < "+ weight_max +";");
                    while ( rs2.next() ){
                        int weight_min = rs2.getInt("weight");
                        int n_weight_min = weight_min+1;
                        String sql_update_min = "UPDATE easter set weight = "+n_weight_min+" where weight = "+weight_min+";";

                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate(sql_update_min);
                        c.commit();
                    
                    }
                    
                        rs.close();
                        rs2.close();
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                    
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;

                    case 5: //************************ WHEN RATING == 5 ******************//
                         try{
                            
                ResultSet rs = stmt.executeQuery( "SELECT weight, MAX(weight) FROM easter;");
      
                    int weight_max = 0;
                    if ( rs.next() ) {
       
                        weight_max = rs.getInt("weight");
                        int n_weight_max = weight_max+5;
                        String sql_update_max = "UPDATE easter set weight = "+n_weight_max+" where weight = "+weight_max+";";
                        stmt2.executeUpdate(sql_update_max);
                        c.commit();
                        weight_max += 5;
                    }
     
                     
                    ResultSet rs2 = stmt3.executeQuery( "SELECT weight FROM easter WHERE weight < "+ weight_max +";");
                    while ( rs2.next() ){
                        int weight_min = rs2.getInt("weight");
                        int n_weight_min = weight_min-3;
                        String sql_update_min = "UPDATE easter set weight = "+n_weight_min+" where weight = "+weight_min+";";

                        Statement stmt4 = c.createStatement();
                        stmt4.executeUpdate(sql_update_min);
                        c.commit();
                    
                    }
                    
                        rs.close();
                        rs2.close();
                        stmt.close();
                        stmt2.close();
                        stmt3.close();
                        c.close();
                    
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                        break;
                        }
                break;
        
        
        
        }
    
    
    }
    
    
    /**
 * getHighestWeightWord - given the occasion this method returns the core word associated with the occasion with the highest weight from the database
 * @param occasion - sting identifying the occasion entered, so we know which database table to access
 * @return string - a word from a given database table which has the highest weight currently
 */
    public String getHighestWeightWord (String occasion){
    
        switch (occasion){
        
            case "birthday":
                
                try{
                ResultSet rs = stmt.executeQuery( "SELECT word, MAX(weight) FROM birthdays;");
      
                    //could od this with if, just gets the next word returned from above query
                    while ( rs.next() ) {
                    String  word = rs.getString("word");
                    return word;
                    }
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                break;
               
            case "christmas":
                
                try{
                ResultSet rs = stmt.executeQuery( "SELECT word, MAX(weight) FROM christmas;");
      
                    //could od this with if, just gets the next word returned from above query
                    while ( rs.next() ) {
                    String  word = rs.getString("word");
                        return word;
                    }
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                break;
                
            case "easter":
                
                try{
                ResultSet rs = stmt.executeQuery( "SELECT word, MAX(weight) FROM easter;");
      
                    //could od this with if, just gets the next word returned from above query
                    while ( rs.next() ) {
                    String  word = rs.getString("word");
                        return word;
                    }
                        }
                        catch ( Exception e ) {
                        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                        System.exit(0);
                        }
                break;
        
        
        
        }
        
        //this should really never get returned or displayed to the user
        return "Error in selecting word from database.";
    
    }
    
}

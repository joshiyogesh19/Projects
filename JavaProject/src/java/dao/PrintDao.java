/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import model.Clients;
import model.LocationXOrders;
import model.MarketingAgents;
import model.Locations;
import model.Login;
import model.Order;

/**
 *
 * @author Yogesh Joshi
 */
public class PrintDao {
    private String url;
    private String userDB;
    private String passDB;
    
    public PrintDao(){
    }
    
    public PrintDao(String url, String userDB, String passDB){
        this.url = url;
        this.userDB = userDB;
        this.passDB = passDB;
    }
    
    protected Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, userDB, passDB);
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return conn;
    }
    
    //------------------------- Login --------------------------------//
    public Login login(String userName, String password){
        Login loginObj = new Login();
        String query = "SELECT * FROM login WHERE userName = '" + userName + "' AND password = '" + password + "';";
        
        try{
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            
            while(resultSet.next()){
                loginObj.setUserName(resultSet.getString("userName"));
                loginObj.setPassword(resultSet.getString("password"));
                loginObj.setRole(resultSet.getString("role"));
                loginObj.setAgentId(resultSet.getInt("agentId"));
            }
            resultSet.close();
            stmt.close();
            
            if(conn != null && !conn.isClosed()){
                conn.close();
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        return loginObj;
    }
    
    //------------------------- Marketing Agents --------------------------------//
    //method for adding the marketing agents
    public int createMarketingAgents(MarketingAgents agentObj, Login loginObj){
        int res = 0;
        String insertAgent = "INSERT INTO marketingagent (firstName,lastName,phoneNo,email) VALUES (?,?,?,?)";
        String insertLogin = "INSERT INTO login (userName,password,role,agentId) VALUES (?,?,?,?)";
        int id = 0;
        
        try {
            Connection conn = getConnection();
            if(conn!=null){
                PreparedStatement stmt = conn.prepareStatement(insertAgent,Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, agentObj.getFirstName());
                stmt.setString(2, agentObj.getLastName());
                stmt.setString(3, agentObj.getPhoneNo());
                stmt.setString(4, agentObj.getEmail());
                res = stmt.executeUpdate();
                
                ResultSet resultSet = stmt.getGeneratedKeys();
                while(resultSet.next()){
                    id = resultSet.getInt(1);
                }
                loginObj.setAgentId(id);
                
                PreparedStatement stmt2 = conn.prepareStatement(insertLogin);
                stmt2.setString(1, loginObj.getUserName());
                stmt2.setString(2, loginObj.getPassword());
                stmt2.setString(3, loginObj.getRole());
                stmt2.setInt(4, loginObj.getAgentId());
                res = stmt2.executeUpdate();
                
                conn.close();
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return res;
    }
    
    //method for displaying the marketing agents
    public ArrayList<Object> readMarketingAgents(){
        ArrayList<Object> objectsList = new ArrayList<>();
        String query = "SELECT m.*,l.* FROM marketingagent m, login l WHERE l.agentId = m.id order by m.id;";
        
        try{
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            
            while(resultSet.next()){
                MarketingAgents agents = new MarketingAgents();
                agents.setId(resultSet.getInt("id"));
                agents.setFirstName(resultSet.getString("firstName"));
                agents.setLastName(resultSet.getString("lastName"));
                agents.setPhoneNo(resultSet.getString("phoneNo"));
                agents.setEmail(resultSet.getString("email"));
                Login loginObj = new Login();
                loginObj.setUserName(resultSet.getString("userName"));
                loginObj.setPassword(resultSet.getString("password"));
                loginObj.setAgentId(resultSet.getInt("agentId"));
                
                agents.setLogin(loginObj);
                objectsList.add(agents);                
            }
            resultSet.close();
            stmt.close();             
            
            if(conn!=null && !conn.isClosed()){
                conn.close();
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return objectsList;
    }
    
    public MarketingAgents showAgentInfo(int id, int agentId){
        MarketingAgents agent = null;
        Login loginObj = null;
        //String query = "SELECT * FROM marketingagent WHERE id = ?";
        String query = "SELECT m.*,l.userName,l.password FROM marketingagent m, login l WHERE m.id = ? AND l.agentId = ?";
        
        try{
            Connection conn = getConnection();
            PreparedStatement preStmt = conn.prepareStatement(query);
            preStmt.setInt(1, id);
            preStmt.setInt(2, agentId);
            ResultSet resultSet = preStmt.executeQuery();
            
            while(resultSet.next()){
                agent = new MarketingAgents();
                agent.setId(resultSet.getInt("id"));
                agent.setFirstName(resultSet.getString("firstName"));
                agent.setLastName(resultSet.getString("lastName"));
                agent.setPhoneNo(resultSet.getString("phoneNo"));
                agent.setEmail(resultSet.getString("email"));
                
                loginObj = new Login();
                loginObj.setUserName(resultSet.getString("userName"));
                loginObj.setPassword(resultSet.getString("password"));
                loginObj.setAgentId(agentId);
                agent.setLogin(loginObj);
            }
            resultSet.close();
            preStmt.close();
            
            if(conn != null || !conn.isClosed()){
                conn.close();
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        return agent;
    }
    
    public boolean updateMarketingAgent(MarketingAgents agent, Login loginObj) {
        boolean result = false;
        String agentUpdate = "UPDATE marketingagent SET firstName = ?, lastName = ?, phoneNo = ?, email = ? WHERE id = ?";
        String loginUpdate = "UPDATE login SET userName = ?, password = ? WHERE agentId = ?";
        
        try{
            Connection conn = getConnection();
            PreparedStatement preStmt = conn.prepareStatement(agentUpdate);
            preStmt.setString(1, agent.getFirstName());
            preStmt.setString(2, agent.getLastName());
            preStmt.setString(3, agent.getPhoneNo());
            preStmt.setString(4, agent.getEmail());
            preStmt.setInt(5, agent.getId());
            
            PreparedStatement preStmt1 = conn.prepareStatement(loginUpdate);
            preStmt1.setString(1, loginObj.getUserName());
            preStmt1.setString(2, loginObj.getPassword());
            preStmt1.setInt(3, loginObj.getAgentId());
            
            result = preStmt.executeUpdate() > 0 && preStmt1.executeUpdate() > 0;
            
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }
    
    public String deleteMarketingAgent(int id, int agentId){
        boolean result = false;
        String message = "";
        String agentDelete = "DELETE FROM marketingagent WHERE id = ?";
        String loginDelete = "DELETE FROM login WHERE agentId = ?";
        
        try{
            Connection conn = getConnection();
            PreparedStatement preStmt = conn.prepareStatement(agentDelete);
            preStmt.setInt(1, id);
            
            PreparedStatement preStmt1 = conn.prepareStatement(loginDelete);
            preStmt1.setInt(1, agentId);
            
            result = preStmt.executeUpdate() > 0 && preStmt1.executeUpdate() > 0;
        }
        catch(SQLException ex){
            message = "Sorry the row can't be deleted...";
            message += ex.getMessage();
        }
        return message;
    }
    
    //------------------------- Clients --------------------------------//
    
    public int createClients(Clients clientObj) {
        int res = 0;
        String sql = "INSERT INTO clients (agentId,firstName,lastName,streetNumber,streetName,city,province,postalCode,telOffice,telCell,email,company,companyType) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        try {
            Connection conn = getConnection();
            if(conn!=null){
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, clientObj.getAgentId());
                stmt.setString(2, clientObj.getFirstName());
                stmt.setString(3, clientObj.getLastName());
                stmt.setInt(4, clientObj.getStreetNumber());
                stmt.setString(5, clientObj.getStreetName());
                stmt.setString(6, clientObj.getCity());
                stmt.setString(7, clientObj.getProvince());
                stmt.setString(8, clientObj.getPostalCode());
                stmt.setString(9, clientObj.getTelOffice());
                stmt.setString(10, clientObj.getTelCell());
                stmt.setString(11, clientObj.getEmail());
                stmt.setString(12, clientObj.getCompany());
                stmt.setString(13, clientObj.getCompanyType());                
                res = stmt.executeUpdate();
                conn.close();
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return res;
    }

    public ArrayList<Clients> readClients() {
        ArrayList<Clients> clientsList = new ArrayList<>();
        String query = "SELECT * FROM clients";
        
        try{
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            
            while(resultSet.next()){
                Clients clients = new Clients();
                clients.setId(resultSet.getInt("id"));
                clients.setAgentId(resultSet.getInt("agentId"));
                clients.setFirstName(resultSet.getString("firstName"));
                clients.setLastName(resultSet.getString("lastName"));
                clients.setStreetNumber(resultSet.getInt("streetNumber"));
                clients.setStreetName(resultSet.getString("streetName"));
                clients.setCity(resultSet.getString("city"));
                clients.setProvince(resultSet.getString("province"));
                clients.setPostalCode(resultSet.getString("postalCode"));
                clients.setTelOffice(resultSet.getString("telOffice"));
                clients.setTelCell(resultSet.getString("telCell"));
                clients.setEmail(resultSet.getString("email"));
                clients.setCompany(resultSet.getString("company"));
                clients.setCompanyType(resultSet.getString("companyType"));
                
                clientsList.add(clients);
            }
            resultSet.close();
            stmt.close();
             
            if(conn!=null && !conn.isClosed()){
                conn.close();
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return clientsList;
    }
    
      public Clients showClientInfo(int id) {
        Clients client = null;
        String query = "SELECT * FROM clients WHERE id = ?";
        
        try{
            Connection conn = getConnection();
            PreparedStatement preStmt = conn.prepareStatement(query);
            preStmt.setInt(1, id);
            ResultSet resultSet = preStmt.executeQuery();
            
            while(resultSet.next()){
                client = new Clients();
                client.setId(resultSet.getInt("id"));
                client.setAgentId(resultSet.getInt("agentId"));
                client.setFirstName(resultSet.getString("firstName"));
                client.setLastName(resultSet.getString("lastName"));
                client.setStreetNumber(resultSet.getInt("streetNumber"));
                client.setStreetName(resultSet.getString("streetName"));
                client.setCity(resultSet.getString("city"));
                client.setProvince(resultSet.getString("province"));
                client.setPostalCode(resultSet.getString("postalCode"));
                client.setTelOffice(resultSet.getString("telOffice"));
                client.setTelCell(resultSet.getString("telCell"));
                client.setEmail(resultSet.getString("email"));
                client.setCompany(resultSet.getString("company"));
                client.setCompanyType(resultSet.getString("companyType"));
            }
            resultSet.close();
            preStmt.close();
            
            if(conn != null || !conn.isClosed()){
                conn.close();
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        return client;
    }
    
     public boolean updateClient(Clients client) {
        boolean result = false;
        String query = "UPDATE clients SET agentId = ?, firstName = ?, lastName = ?, streetNumber = ?, streetName = ?, city = ?, province = ?, postalCode = ?, telOffice = ?, telCell = ?, email = ?, company = ?, companyType = ? WHERE id = ?";
        
        try{
            Connection conn = getConnection();
            PreparedStatement preStmt = conn.prepareStatement(query);
            preStmt.setInt(1, client.getAgentId());
            preStmt.setString(2, client.getFirstName());
            preStmt.setString(3, client.getLastName());
            preStmt.setInt(4, client.getStreetNumber());
            preStmt.setString(5, client.getStreetName());
            preStmt.setString(6,client.getCity());
            preStmt.setString(7, client.getProvince());
            preStmt.setString(8, client.getPostalCode());
            preStmt.setString(9, client.getTelOffice());
            preStmt.setString(10, client.getTelCell());
            preStmt.setString(11, client.getEmail());
            preStmt.setString(12, client.getCompany());
            preStmt.setString(13, client.getCompanyType());
            preStmt.setInt(14, client.getId());
            
            if(preStmt.executeUpdate() > 0){
                result = true;
            }
            else{
                result = false;
            }
            
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }
    
    public String deleteClient(int id){
        boolean result = false;
        String message = "";
        String query = "DELETE FROM clients WHERE id = ?";
        
        try{
            Connection conn = getConnection();
            PreparedStatement preStmt = conn.prepareStatement(query);
            preStmt.setInt(1, id);
            int res = preStmt.executeUpdate();
            
            if(res > 0){
                result = true;
            }
            else{
                result = false;
            }
        }
        catch(SQLException ex){
            message = "Sorry the row can't be deleted...";
            message += ex.getMessage();
        }
        return message;
    }
    
    
    //------------------------- Locations --------------------------------//
    public int createLocations(Locations locationObj) {
        int res = 0;
        String sql = "INSERT INTO location (locationName, distributionCapacity) VALUES (?, ?)";
        
        try {
            Connection conn = getConnection();
            if(conn!=null){
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, locationObj.getLocationName());
                stmt.setInt(2, locationObj.getDistributionCapacity());
                res = stmt.executeUpdate();
                conn.close();
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return res;
    }
    
    public ArrayList<Locations> readLocations(){
        ArrayList<Locations> locationsList = new ArrayList<>();
        String query = "SELECT * FROM location";
        
        try{
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            
            while(resultSet.next()){
                Locations locations = new Locations();
                locations.setId(resultSet.getInt("id"));
                locations.setLocationName(resultSet.getString("locationName"));
                locations.setDistributionCapacity(resultSet.getInt("distributionCapacity"));
                locationsList.add(locations);
            }
            resultSet.close();
            stmt.close();
             
            if(conn!=null && !conn.isClosed()){
                conn.close();
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return locationsList;
    }
    
    public Locations showLocationInfo(int id){
        Locations location = null;
        String query = "SELECT * FROM location WHERE id = ?";
        
        try{
            Connection conn = getConnection();
            PreparedStatement preStmt = conn.prepareStatement(query);
            preStmt.setInt(1, id);
            ResultSet resultSet = preStmt.executeQuery();
            
            while(resultSet.next()){
                location = new Locations();
                location.setId(resultSet.getInt("id"));
                location.setLocationName(resultSet.getString("locationName"));
                location.setDistributionCapacity(resultSet.getInt("distributionCapacity"));
            }
            resultSet.close();
            preStmt.close();
            
            if(conn != null || !conn.isClosed()){
                conn.close();
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        return location;
    }
    
    public boolean updateLocationInfo(Locations location) {
        boolean result = false;
        String query = "UPDATE location SET locationName = ?, distributionCapacity = ? WHERE id = ?";
        
        try{
            Connection conn = getConnection();
            PreparedStatement preStmt = conn.prepareStatement(query);
            preStmt.setString(1, location.getLocationName());
            preStmt.setInt(2, location.getDistributionCapacity());
            preStmt.setInt(3, location.getId());
            
            if(preStmt.executeUpdate() > 0){
                result = true;
            }
            else{
                result = false;
            }
            
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }
    
    public String deleteLocation(int id){
        boolean result = false;
        String message = "";
        String query = "DELETE FROM location WHERE id = ?";
        
        try{
            Connection conn = getConnection();
            PreparedStatement preStmt = conn.prepareStatement(query);
            preStmt.setInt(1, id);
            int res = preStmt.executeUpdate();
            
            if(res > 0){
                result = true;
            }
            else{
                result = false;
            }
        }
        catch(SQLException ex){
            message = "Sorry the row can't be deleted...";
            message += ex.getMessage();
        }
        return message;
    }
    
    //------------------------- Marketing Orders --------------------------------//
    //All 11 parameters
    public int createMarketingOrders(Order orderObj, int locationId){
        int res = 0;
        String sql = "INSERT INTO orders (agentId, clientId, flyerQty, flyerLayout, flyerImg, personalCopy, paymentInformation, invoiceNumber, comments, isFlyerArtApproved, isPaymentReceived) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        String insertLocationXOrder = "INSERT INTO locationxorders (orderId, locationId) VALUES (?,?)";
        int orderId = 0;
        LocationXOrders locationxordersObj = new LocationXOrders();
        
        try {
            Connection conn = getConnection();
            //InputStream is = new FileInputStream(orderObj.getFlyerImg());
            if(conn!=null){
                PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                stmt.setInt(1, orderObj.getAgentId());
                stmt.setInt(2, orderObj.getClientId());
                stmt.setInt(3, orderObj.getFlyerQty());
                stmt.setString(4, orderObj.getFlyerLayout());
                //stmt.setBlob(5, is);
                stmt.setBlob(5, orderObj.getFlyerImg());
                stmt.setInt(6, orderObj.getPersonalCopy());
                stmt.setString(7, orderObj.getPaymentInformation());
                stmt.setString(8, orderObj.getInvoiceNumber());
                stmt.setString(9, orderObj.getComments());
                stmt.setInt(10, orderObj.getIsFlyerArtApproved());
                stmt.setInt(11, orderObj.getIsPaymentReceived());
                
                res = stmt.executeUpdate();
                
                ResultSet resultSet = stmt.getGeneratedKeys();
                while(resultSet.next()){
                    orderId = resultSet.getInt(1);
                }
                locationxordersObj.setOrderId(orderId);
                locationxordersObj.setLocationId(locationId);
                
                PreparedStatement stmt2 = conn.prepareStatement(insertLocationXOrder);
                stmt2.setInt(1, locationxordersObj.getOrderId());
                stmt2.setInt(2, locationxordersObj.getLocationId());
                res = stmt2.executeUpdate();
                
                conn.close();
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        /*catch(FileNotFoundException ex){
            ex.printStackTrace();
        }*/
        return res;
    }
    
    public ArrayList<Order> readMarketingOrders(){
        ArrayList<Order> ordersList = new ArrayList<>();
        String query = "SELECT * FROM orders ORDER BY isFlyerArtApproved AND isPaymentReceived";
        
        try{
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            
            while(resultSet.next()){
                Order orders = new Order();
                orders.setId(resultSet.getInt("id"));
                orders.setClientId(resultSet.getInt("clientId"));
                orders.setFlyerQty(resultSet.getInt("flyerQty"));
                orders.setFlyerLayout(resultSet.getString("flyerLayout"));
                orders.setFlyerImg(resultSet.getBlob("flyerImg"));
                orders.setPersonalCopy(resultSet.getInt("personalCopy"));
                orders.setPaymentInformation(resultSet.getString("paymentInformation"));
                orders.setInvoiceNumber(resultSet.getString("invoiceNumber"));
                orders.setComments(resultSet.getNString("comments"));
                orders.setIsFlyerArtApproved(resultSet.getInt("isFlyerArtApproved"));
                orders.setIsPaymentReceived(resultSet.getInt("isPaymentReceived"));
                ordersList.add(orders);
            }
            resultSet.close();
            stmt.close();
             
            if(conn!=null && !conn.isClosed()){
                conn.close();
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return ordersList;
    }
    
    public Order showOrderInfo(int id){
        Order orders = null;
        String query = "SELECT * FROM orders WHERE id = ?";
        
        try{
            Connection conn = getConnection();
            PreparedStatement preStmt = conn.prepareStatement(query);
            preStmt.setInt(1, id);
            ResultSet resultSet = preStmt.executeQuery();
            
            while(resultSet.next()){
                orders = new Order();
                orders.setId(resultSet.getInt("id"));
                orders.setClientId(resultSet.getInt("clientId"));
                orders.setAgentId(resultSet.getInt("clientId"));
                orders.setFlyerQty(resultSet.getInt("flyerQty"));
                orders.setFlyerLayout(resultSet.getString("flyerLayout"));
                //orders.setFlyerImg(resultSet.getString("flyerImg"));
                orders.setFlyerImg(resultSet.getBlob("flyerImg"));
                orders.setPersonalCopy(resultSet.getInt("personalCopy"));
                orders.setPaymentInformation(resultSet.getString("paymentInformation"));
                orders.setInvoiceNumber(resultSet.getString("invoiceNumber"));
                orders.setComments(resultSet.getNString("comments"));
                orders.setIsFlyerArtApproved(resultSet.getInt("isFlyerArtApproved"));
                orders.setIsPaymentReceived(resultSet.getInt("isPaymentReceived"));
            }
            resultSet.close();
            preStmt.close();
            
            if(conn != null || !conn.isClosed()){
                conn.close();
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
        return orders;
    }

        public boolean updateMarketingOrder(Order order) {
        boolean result = false;
        String query = "UPDATE orders SET flyerQty = ?, flyerLayout = ?, flyerImg = ?, personalCopy = ?, paymentInformation = ?, comments = ?, isFlyerArtApproved = ?, isPaymentReceived = ? WHERE id = ?";
        
        try{
            Connection conn = getConnection();
            PreparedStatement preStmt = conn.prepareStatement(query);
            preStmt.setInt(1, order.getFlyerQty());
            preStmt.setString(2, order.getFlyerLayout());
            preStmt.setBlob(3, order.getFlyerImg());
            preStmt.setInt(4, order.getPersonalCopy());
            preStmt.setString(5, order.getPaymentInformation());
            preStmt.setString(6, order.getComments());
            preStmt.setInt(7, order.getIsFlyerArtApproved());
            preStmt.setInt(8, order.getIsPaymentReceived());
            preStmt.setInt(9, order.getId());
            
            if(preStmt.executeUpdate() > 0){
                result = true;
            }
            else{
                result = false;
            }
            
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }
 
    
    public String deleteMarketingOrder(int id){
        boolean result = false;
        String message = "";
        String query = "DELETE FROM orders WHERE id = ?";
        
        try{
            Connection conn = getConnection();
            PreparedStatement preStmt = conn.prepareStatement(query);
            preStmt.setInt(1, id);
            int res = preStmt.executeUpdate();
            
            if(res > 0){
                result = true;
            }
            else{
                result = false;
            }
        }
        catch(SQLException ex){
            message = "Sorry the row can't be deleted...";
            message += ex.getMessage();
        }
        return message;
    }
    
    public ArrayList<Locations> getLocations() {
        ArrayList<Locations> locationList = new ArrayList<>();
        Locations locationObj = new Locations();
        String query = "Select * from location";
        
        try {
            Connection conn = getConnection();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet resultSet = stmt.executeQuery(query);
                
                while(resultSet.next()){
                    locationObj.setId(resultSet.getInt("id"));
                    locationObj.setLocationName(resultSet.getString("locationName"));
                    locationList.add(locationObj);
                }
                resultSet.close();
                stmt.close();
                conn.close();
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return locationList;
    }
}
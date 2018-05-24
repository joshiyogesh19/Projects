/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import dao.PrintDao;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.rowset.serial.SerialBlob;
import model.Clients;
import model.MarketingAgents;
import model.Locations;
import model.Login;
import model.Order;
import service.PrintService;

/**
 *
 * @author Yogesh Joshi
 */
public class PrintServlet extends HttpServlet {
PrintService printService;
    PrintDao printDao;
    String jdbcUserName;
    String jdbcPassword;
    String jdbcURL;
    HttpSession session;
    int agentId;
    Random random;
    
    @Override
    public void init() throws ServletException {
        jdbcURL = getServletContext().getInitParameter("jdbcURL");
        jdbcUserName = getServletContext().getInitParameter("jdbcUserName");
        jdbcPassword = getServletContext().getInitParameter("jdbcPassword");
        
        printDao = new PrintDao(jdbcURL, jdbcUserName, jdbcPassword);
        printService = new PrintService();
        random = new Random();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getServletPath();
        
        switch(action){
            case "/login":
                login(request, response);
                break;
                
            case "/logout":
                logout(request, response);
                break;
            
            //------------ Agents -----------------//
            case "/addAgent":
                addMarketingAgents(request, response);
                break;
            
            case "/editAgent":
                showMarketingAgent(request, response);
                break;
                
            case "/updateAgent":
                updateMarketingAgent(request,response);
                break;
            
            case "/deleteAgent":
                deleteMarketingAgent(request, response);
                break;
            
            case "/listAgents":
                readMarketingAgents(request, response);
                break;  
                
            //------------------- Locations ----------------------//                
            case "/addLocation":
                addLocations(request, response);
                break;
            
            case "/editLocation":
                showLocation(request, response);
                break;
            
            case "/updateLocation":
                updateLocation(request, response);
                break;
                
            case "/deleteLocation":
                deleteLocation(request, response);
                break;
                
            case "/listLocations":
                readLocations(request, response);
                break;
                
            //------------------------ Orders ---------------------------//
            case "/addOrder":
                addMarketingOrders(request, response);
                break;
                
            case "/editOrder":
                showOrder(request, response);
                break;
                
            case "/updateOrder":
                updateMarketingOrders(request, response);
                break;
            
            case "/deleteOrder":
                deleteMarketingOrders(request, response);
                break;
                
            case "/listOrders":
                readMarketingOrders(request, response);
                break;
                
            case "/createOrder":
                showOrderForm(request, response);
                break;
                
            case "/showEditOrder":
                editOrderForm(request,response);
                break;
            //------------------------ Clients ------------------------------//
            case "/addClient":
                addClients(request, response);
                break;
            
            case "/editClient":
                showClient(request, response);
                break;
                
            case "/updateClient":
                updateClient(request,response);
                break;
            
            case "/deleteClient":
                deleteClient(request, response);
                break;    
           
            case "/listClients":
                readClients(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    //---------------------------------------- Login -----------------------------------------------//
    protected void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String message = "";
        
        try{
        agentId = printService.login(userName, password, printDao);
        if(agentId == 0 && userName.equals("admin")){ 
            session = request.getSession();
            session.setAttribute("username", userName);
            RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");
            dispatcher.forward(request, response);            
        }
        else if(agentId != 0 && !userName.equals("admin")){
            session = request.getSession();
            session.setAttribute("username", userName);
            RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");
            dispatcher.forward(request, response);
        }
        }catch(Exception e){
            e.printStackTrace();
            message = "Invalid Username and/or password";
            request.setAttribute("message", message);
            RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
            dispatcher.forward(request, response);
        }
    }
    
    //--------------------------------------------- Logout -----------------------------------------------------//
    protected void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        session = request.getSession();
        session.removeAttribute("username");
        RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");
        dispatcher.forward(request, response);
    }
    
    //----------------------------------------- Marketing Agents ---------------------------------------------------//
    protected void addMarketingAgents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phoneNo = request.getParameter("phoneNo");
        String email = request.getParameter("email");
        
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        
        int res = printService.createMarketingAgents(firstName, lastName, phoneNo, email, userName, password, role, printDao);
        
        if(res > 0){
            RequestDispatcher dispatcher = request.getRequestDispatcher("listAgents");
            dispatcher.forward(request, response);
        }
        else{
            response.sendRedirect("error.jsp");
        }
    }
    
    protected void readMarketingAgents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<Object> objectList = new ArrayList();
        objectList = printService.readMarketingAgents(printDao);
        
        request.setAttribute("objectList", objectList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("displayMarketingAgents.jsp");
        dispatcher.forward(request, response);
    }
    
    protected void showMarketingAgent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        int idOfAgent = Integer.parseInt(request.getParameter("agentId"));
        try{
            MarketingAgents agent = printService.showAgentInfo(id, idOfAgent, printDao);
            request.setAttribute("agent", agent);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("editAgents.jsp");
            dispatcher.forward(request,response);
            
        } catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }
    }
    
    protected void updateMarketingAgent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String fName = request.getParameter("fName");
        String lName = request.getParameter("lName");
        String phoneNo = request.getParameter("phoneNo");
        String email = request.getParameter("email");
        
        int idOfAgent = Integer.parseInt(request.getParameter("idOfAgent"));
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        
        MarketingAgents agent = new MarketingAgents(id, fName, lName, phoneNo, email);
        Login loginObj = new Login(userName, password, idOfAgent);
        printService.updateMarketingAgent(agent, loginObj, printDao);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("listAgents");
        dispatcher.forward(request, response);
    }
    
    protected void deleteMarketingAgent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        int idOfAgent = Integer.parseInt(request.getParameter("agentId"));
        String message = printService.deleteMarketingAgent(id, idOfAgent, printDao);
        
        request.setAttribute("message", message);
        RequestDispatcher dispatcher = request.getRequestDispatcher("listAgents");
        dispatcher.forward(request,response);
    }
    
    //----------------------------------------- Locations ------------------------------------------------//    
    protected void addLocations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String locationName = request.getParameter("locationName");
        int distributionCapacity = Integer.parseInt(request.getParameter("distributionCapacity"));
        
        int res = printService.createLocations(locationName, distributionCapacity, printDao);
        
        if(res > 0){
            RequestDispatcher dispatcher = request.getRequestDispatcher("listLocations");
            dispatcher.forward(request, response);
        }
        else{
            response.sendRedirect("error.jsp");
        }
    }
    
    protected void readLocations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<Locations> locationsList = new ArrayList();
        locationsList = printService.readLocations(printDao);
        
        request.setAttribute("locationsList", locationsList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("displayLocations.jsp");
        dispatcher.forward(request, response);
    }
    
    protected void showLocation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        try{
            Locations location = printService.showLocationInfo(id, printDao);
            request.setAttribute("location", location);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("editLocations.jsp");
            dispatcher.forward(request, response);
            
        } catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }
    }
    
    protected void updateLocation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String locationName = request.getParameter("locationName");
        int distributionCapacity = Integer.parseInt(request.getParameter("distributionCapacity"));
        
        Locations location = new Locations(id, locationName, distributionCapacity);
        printService.updateLocationInfo(location, printDao);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("listLocations");
        dispatcher.forward(request, response);
    }
    
    protected void deleteLocation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String message = printService.deleteLocation(id, printDao);
        
        request.setAttribute("message", message);
        RequestDispatcher dispatcher = request.getRequestDispatcher("listLocations");
        dispatcher.forward(request,response);
    }

    //----------------------------------------- Marketing Orders ---------------------------------------------------//
    protected void addMarketingOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //TODO: GET and SET 
        
        int clientId = Integer.parseInt(request.getParameter("clients"));
        int locationXorderId = Integer.parseInt(request.getParameter("location"));
        byte[] flyerImg = null;

        int flyerQty = Integer.parseInt(request.getParameter("quantity"));
        String flyerLayout = request.getParameter("layouts");
        int personalCopy = Integer.parseInt(request.getParameter("personalcopy"));
        flyerImg = request.getParameter("pic").getBytes();
        Blob blob = null;
        try {
            blob = new SerialBlob(flyerImg);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        String cardNum = request.getParameter("cardnum");
        String expireDate = request.getParameter("expdate");
        String cvvNum = request.getParameter("cvvnum");
        String paymentInformation = cardNum + "/" + expireDate + "/" + cvvNum;
        //String invoiceNumber = request.getParameter("invoicenum");
        String invoiceNumber = String.valueOf(random.nextInt(99999-10001) + 10001);
        String comments = request.getParameter("comments");
        //default optional for create order 
        int isFlyerArtAprroved = 0;
        int isPaymentReceived = 0;
        
        int res = printService.createMarketingOrders(locationXorderId, agentId, clientId, flyerQty, flyerLayout, blob, personalCopy, paymentInformation, invoiceNumber, comments, isFlyerArtAprroved, isPaymentReceived, printDao);
        if (res > 0) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("listOrders");
            dispatcher.forward(request, response);
        } else {
            response.sendRedirect("error.jsp");
        }
    }
    
    protected void readMarketingOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<Order> ordersList = new ArrayList();
        ordersList = printService.readMarketingOrders(printDao);

        request.setAttribute("ordersList", ordersList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("displayOrders.jsp");
        dispatcher.forward(request, response);
    }

    protected void showOrder(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        try{
            Order orderObj = printService.showOrderInfo(id, printDao);
            request.setAttribute("order", orderObj);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("editOrders.jsp");
            dispatcher.forward(request, response);
            
        } catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }
    }
    
    protected void updateMarketingOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        byte[] flyerImg = null;
        int flyerQty = Integer.parseInt(request.getParameter("quantity"));
        String flyerLayout = request.getParameter("layouts");
        int personalCopy = Integer.parseInt(request.getParameter("personalCopy"));
        flyerImg = request.getParameter("pic").getBytes();
        Blob blob = null;
        try {
            blob = new SerialBlob(flyerImg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String paymentInformation = request.getParameter("paymentInfo");
        String comments = request.getParameter("comments");
        String artWork = request.getParameter("artwork");        
        String payment = request.getParameter("payment");
        int isFlyerArtAprroved = 0;
        int isPaymentReceived = 0;
        if (artWork == null) {
            isFlyerArtAprroved = 0;
        } else {
            isFlyerArtAprroved = 1;
        }
        if (payment == null) {
            isPaymentReceived = 0;
        } else {
            isPaymentReceived = 1;
        }
        int locationXorderId = Integer.parseInt(request.getParameter("location"));
        int clientId = Integer.parseInt(request.getParameter("clients"));
        
        Order orderObj = new Order(id, agentId, clientId, flyerQty, flyerLayout, blob, personalCopy, paymentInformation, comments, isFlyerArtAprroved, isPaymentReceived);
        printService.updateMarketingOrder(orderObj, printDao);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("listOrders");
        dispatcher.forward(request, response);
    }
    
    protected void deleteMarketingOrders(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String message = "";
        int id = Integer.parseInt(request.getParameter("id"));
        message = printService.deleteMarketingOrders(id, printDao);

        request.setAttribute("message", message);
        RequestDispatcher dispatcher = request.getRequestDispatcher("listOrders");
        dispatcher.forward(request, response);
    }

    //----------------------------------------- Clients -------------------------------------------------//
    protected void addClients(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        int stNum = Integer.parseInt(request.getParameter("stNum"));
        String stName  = request.getParameter("stName");
        String city  = request.getParameter("city");
        String province  = request.getParameter("province");
        String postalCode  = request.getParameter("postalCode");
        String telOffice  = request.getParameter("telOffice");
        String telCell = request.getParameter("telCell");
        String email  = request.getParameter("email");
        String company  = request.getParameter("company");
        String companyType  = request.getParameter("companyType");
        
        int res = printService.createClients(agentId,firstName, lastName, stNum, stName, city, province, postalCode, telOffice, telCell, email,company, companyType, printDao);
        
        if(res > 0){
            RequestDispatcher dispatcher = request.getRequestDispatcher("listClients");
            dispatcher.forward(request, response);
        }
        else{
            response.sendRedirect("error.jsp");
        }
    }
    
    protected void readClients(HttpServletRequest request, HttpServletResponse response) 
         throws ServletException, IOException {
        ArrayList<Clients> clientsList = new ArrayList();
        clientsList = printService.readClients(printDao);
        
        request.setAttribute("clientsList", clientsList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("dispalyClient.jsp");
        dispatcher.forward(request, response);
    }
     
    protected void showClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        try{
            Clients client = printService.showClientInfo(id, printDao);
            request.setAttribute("clients", client);
            
            RequestDispatcher dispatcher = request.getRequestDispatcher("editClient.jsp");
            dispatcher.forward(request,response);
            
        } catch(SQLException sqlEx){
            sqlEx.printStackTrace();
        }
    }
    
    protected void updateClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        agentId = Integer.parseInt(request.getParameter("agentId"));
        String fName = request.getParameter("fName");
        String lName = request.getParameter("lName");
        int stNum = Integer.parseInt(request.getParameter("stNum"));
        String stName  = request.getParameter("stName");
        String city  = request.getParameter("city");
        String province  = request.getParameter("province");
        String postalCode  = request.getParameter("postalCode");
        String telOffice  = request.getParameter("telOffice");
        String telCell = request.getParameter("telCell");
        String email  = request.getParameter("emaill");
        String company  = request.getParameter("company");
        String companyType  = request.getParameter("companyType");
       
        Clients client = new Clients(id ,agentId, fName, lName, stNum, stName, city, province, postalCode, telOffice, telCell, email, company, companyType);
        printService.updateClient(client, printDao);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("listClients");
        dispatcher.forward(request, response);
    }
    
    protected void deleteClient(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String message = printService.deleteClient(id, printDao);
        
        request.setAttribute("message", message);
        RequestDispatcher dispatcher = request.getRequestDispatcher("listClients");
        dispatcher.forward(request,response);
    }
    
    
    protected void showOrderForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<Clients> clientsList = new ArrayList();
        clientsList = printService.readClients(printDao);
        
        ArrayList<Locations> locationsList = new ArrayList();
        locationsList = printService.readLocations(printDao);
        
        request.setAttribute("clientsList",clientsList);
        request.setAttribute("locationsList",locationsList);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("createOrder.jsp");
        dispatcher.forward(request,response);
    }
    
    protected void editOrderForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<Clients> clientsList = new ArrayList();
        clientsList = printService.readClients(printDao);
        
        ArrayList<Locations> locationsList = new ArrayList();
        locationsList = printService.readLocations(printDao);
        
        request.setAttribute("clientsList",clientsList);
        request.setAttribute("locationsList",locationsList);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("editOrder");
        dispatcher.forward(request,response);
    }
}

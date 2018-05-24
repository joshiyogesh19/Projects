<%-- 
    Document   : editOrders
    Created on : 23-Apr-2018, 1:42:16 PM
    Author     : Yogesh Joshi
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="model.Locations"%>
<%@page import="dao.PrintDao"%>
<%@page import="service.PrintService"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
        <script src="https://code.jquery.com/jquery-3.2.1.slim.min.js" integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN" crossorigin="anonymous"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>

        <title>Edit Orders</title>
    </head>
    <body>
        <c:choose>
            <c:when test="${username==null}">
                <script>window.location.href = "login.jsp";</script>
            </c:when>
            <c:when test="${username=='admin'}">
                <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
                    <span class="navbar-brand mb-0 h1">PRINT MARKETING</span>
                    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
                        <div class="navbar-nav">
                            <a class='nav-item nav-link' href='createLocation.jsp'>Create Locations</a>
                            <a class='nav-item nav-link' href='createAgent.jsp'>Create Agents</a>
                            <a class='nav-item nav-link' href='listClients'>View Client Records</a>
                            <a class='nav-item nav-link' href='listOrders'>View Orders</a>
                        </div>
                    </div>
                    <form action="logout" method="post">
                        <button type="submit" class="btn btn-outline-success my-2 my-sm-0">
                            <c:out value="${username} (logout)"></c:out>
                            </button>
                        </form>
                    </nav>
            </c:when>
            <c:otherwise>
                <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
                    <span class="navbar-brand mb-0 h1">PRINT MARKETING</span>
                    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavAltMarkup" aria-controls="navbarNavAltMarkup" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarNavAltMarkup">
                        <div class="navbar-nav">
                            <a class='nav-item nav-link' href='createClient.jsp'>Create Clients</a>
                            <a class='nav-item nav-link' href='createOrder'>Create Orders</a>
                        </div>
                    </div>
                    <form action="logout" method="post">
                        <button type="submit" class="btn btn-outline-success my-2 my-sm-0">
                            <c:out value="${username} (logout)"></c:out>
                            </button>
                        </form>
                    </nav>
            </c:otherwise>
        </c:choose>
    <center> 
        <h1 style="margin-top: 50px">Edit Orders</h1><br/>
        <form action="updateOrder" method="post">
            <input type="hidden" name="id" id="id" value="${order.id}"/>
            <div class="form-inline col-md-2">
                <label>Clients:&nbsp;  </label>
                <div class="form-group">
                    <select class="form-control" id="clientselection1" name="clients" required>
                        <c:forEach var="client" items="${clientsList}">
                            <option value='<c:out value="${client.id}"/>'><c:out value="${client.firstName} ${client.lastName}"/></option>
                        </c:forEach>
                    </select>
                </div>
            </div><br/>
            <div class="form-inline col-md-2">
                <label>FlyerQty: </label>&nbsp;
                <input class="form-control" type="text" name="quantity" id="quantity" value="${order.flyerQty}" required/>
            </div><br/>
            <div class="form-inline col-md-2">
                <div class="form-group">
                    <label for="Flyerlayout">Flyer Layout: </label>&nbsp;
                    <select class="form-control" id="exampleFormControlSelect1" name="layouts" required>
                        <c:choose>
                            <c:when test="${order.flyerLayout == 'Portrait'}">
                                <option>${order.flyerLayout}</option>
                                <option>Landscape</option>
                                <option>Both</option>
                            </c:when>
                            <c:when test="${order.flyerLayout == 'Landscape'}">
                                <option>${order.flyerLayout}</option>
                                <option>Portrait</option>
                                <option>Both</option>
                            </c:when>
                            <c:otherwise>
                                <option>Portrait</option>
                                <option>Landscape</option>
                                <option>${order.flyerLayout}</option>
                            </c:otherwise>
                        </c:choose>
                    </select>
                </div>
            </div><br/>
            <div class="form-inline col-md-2">
                <label>Locations:&nbsp;  </label>
                <div class="form-group">
                    <c:forEach var="location" items="${locationsList}"><br/>
                        <input type="checkbox" name="location" id="location" value='<c:out value="${location.id}"/>'>&nbsp;&nbsp;
                        <label><c:out value="${location.locationName}"/></label>&nbsp;&nbsp;<br/>
                    </c:forEach>
                </div>
            </div><br>

            <div class="form-inline col-md-2">
                <label>Sample image:</label>&nbsp;
                <input type="file" name="pic" id="pic" required><br><br>
            </div>
            <div class="form-inline col-md-2">
                <label>Personal Copy: </label>&nbsp;
                <input class="form-control" type="text" name="personalCopy" id="personalCopy" value="${order.personalCopy}" required/>
            </div><br/>
            <div class="form-inline col-md-2">
                <label>Payment Information: </label>&nbsp;
                <input class="form-control" type="text" name="paymentInfo" id="paymentInfo" value="${order.paymentInformation}" required/>
            </div><br/>             
            <div class="form-inline col-md-2">
                <label>Art Approved: </label>&nbsp;
                <c:choose>
                    <c:when test="${order.isFlyerArtApproved == 0}">
                        <input class="form-control" type="checkbox" name="artwork" id="artwork" value="artwork"/>
                    </c:when>
                    <c:otherwise>
                        <input class="form-control" type="checkbox" name="artwork" id="artwork" value="artwork" checked/>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="form-inline col-md-2">      
                <label>Payment: </label>&nbsp;
                <c:choose>
                    <c:when test="${order.isPaymentReceived == 0}">
                        <input class="form-control" type="checkbox" name="payment" value="payment" id="payment"/>
                    </c:when>
                    <c:otherwise>
                        <input class="form-control" type="checkbox" name="payment" value="payment" id="payment" checked/>
                    </c:otherwise>
                </c:choose>
            </div><br/>
            <div class="form-inline col-md-2">
                <label>Comments: </label>&nbsp;
                <input class="form-control" type="text" name="comments" id="comments" value="${order.comments}" required/>
            </div><br/>

            <div class="form-group">
                <input class="btn btn-success" type="submit" name="submit" id="submit" value="Update"/>
            </div>
        </form>
    </center>
</body>
</html>

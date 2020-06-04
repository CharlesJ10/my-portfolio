// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson; //Convert json to string
import java.util.ArrayList; //To Enable Arraylist functionality through its class 

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Query query = new Query("UserComment").addSort("Timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    PreparedQuery results = datastore.prepare(query);

    ArrayList<String> userCommentList = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = (long) entity.getKey().getId();
      String firstName = (String) entity.getProperty("FirstName");
      String lastName = (String) entity.getProperty("LastName");
      String subjectText = (String) entity.getProperty("Subject");
      String messageDescription = (String) entity.getProperty("Message");
      long timestamp = (long) entity.getProperty("Timestamp");

      String myUserComment = "Name: " + firstName + " " + lastName + "\n" + "ID: " + id + "\n Subject: " + subjectText + "\n" + messageDescription + "\n" + id;
      userCommentList.add(myUserComment);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(userCommentList));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Records input from the contact form.
    String firstName = request.getParameter("firstname");
    String lastName = request.getParameter("lastname");
    String subjectText = request.getParameter("subject");
    String messageDescription = request.getParameter("message");
    long timestamp = System.currentTimeMillis();

    // Initiate the Datastore service for storage of entity created
    Entity commentEntity = new Entity("UserComment");
    commentEntity.setProperty("FirstName", firstName);
    commentEntity.setProperty("LastName", lastName);
    commentEntity.setProperty("Subject", subjectText);
    commentEntity.setProperty("Message", messageDescription);
    commentEntity.setProperty("Timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the Contact page.
    response.sendRedirect("contact.html");
    
  }
}
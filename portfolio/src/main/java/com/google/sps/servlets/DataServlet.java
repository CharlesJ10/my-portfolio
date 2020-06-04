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

  ArrayList<String> storeCommentList = new ArrayList<String>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String json = convertToJsonUsingGson(storeCommentList);

    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  private String convertToJsonUsingGson(ArrayList storeCommentList) {
    Gson gson = new Gson();
    String json = gson.toJson(storeCommentList);
    return json;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the input from the form.
    String fName = request.getParameter("firstname");
    String lName = request.getParameter("lastname");
    String subject = request.getParameter("subject");
    String message = request.getParameter("message");

    String myUserComment = "Name: " + fName + " " + lName + "\n" + "Subject: " + subject + "\n" + message;
    storeCommentList.add(myUserComment);

    // Redirect back to the Contact page.
    response.sendRedirect("contact.html");
    
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter( String name) {
    String value = name;
    if (value == null) {
      return "";
    }
    return value;
  }
}



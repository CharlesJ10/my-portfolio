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

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Servlet sends mails to personal account by constructing emails from user info entered on the contact page
@WebServlet("/mail")
public class MailServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // Records input from the contact form.
    String userName = request.getParameter("inputName");
    String userEmail = request.getParameter("inputEmail");
    String subjectText = request.getParameter("inputSubject");
    String messageDescription = request.getParameter("inputMessage");
    
    // Implementation of sentiment analysis for opinion mining
    Document doc = Document.newBuilder().setContent(messageDescription).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float score = sentiment.getScore();
    float magnitude = sentiment.getMagnitude();
    languageService.close();

    // Implementation of Mail API
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress("noreply@charlesogbogu-step-2020.appspotmail.com", userName));
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress("chiderajnr@gmail.com", "Charles Ogbogu"));
      msg.setSubject(subjectText);
      msg.setText("Name: " + userName + "\n\n" + "Email: " + userEmail +"\n\n" + "Sentiment Score: " + score + ". Magnitude Score: " + magnitude + "\n\n" + messageDescription);
      Transport.send(msg);
    } catch (AddressException e) {
      System.err.println("Email Address was invalid");
    } catch (MessagingException e) {
      System.err.println("There was an error contacting the mail service.");
    } catch (UnsupportedEncodingException e) {
      System.err.println("Email format could not be supported");
    } finally {
      // Redirect back to the Contact page.
      response.sendRedirect("contact.html");
    }
  }  
}
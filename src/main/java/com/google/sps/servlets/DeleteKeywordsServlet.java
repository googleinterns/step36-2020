package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that deletes the keywords.*/
@WebServlet("/delete-keywords")
public class DeleteKeywordsServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String keyString = request.getParameter("k");
    Key key = KeyFactory.stringToKey(keyString);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.delete(key);
  }
}

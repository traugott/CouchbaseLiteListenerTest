/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package couchbaselistelistenertest;

import com.couchbase.lite.Database;
import com.couchbase.lite.JavaContext;
import com.couchbase.lite.Manager;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.listener.Credentials;
import com.couchbase.lite.listener.LiteListener;
import com.couchbase.lite.replicator.Replication;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author rainer
 */
public class MainClass {

  public static void main(String... args) throws Exception {
    Manager managerServer = new Manager(new JavaContext(), Manager.DEFAULT_OPTIONS);
    Database databaseServer = managerServer.getDatabase("server");
    LiteListener listener = new LiteListener(managerServer, 55654, new Credentials("user", "password"));
    listener.start();
    
    Manager managerClient = new Manager(new JavaContext(), Manager.DEFAULT_OPTIONS);
    Database databaseClient = managerServer.getDatabase("client");
    Replication replicator = databaseClient.createPullReplication(new URL("http://localhost:55654/server"));
    Authenticator auth = AuthenticatorFactory.createBasicAuthenticator("user", "password");
    replicator.setAuthenticator(auth);
    replicator.setContinuous(true);
    replicator.start();

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Wait till threads Acme.Utils.ThreadPool(0)-PooledThread: ... appear");
    br.readLine();
    replicator.stop();
    System.out.println("Now the threads Acme.Utils.ThreadPool(0)-PooledThread: ... stuck");
    br.readLine();
    
  }
}

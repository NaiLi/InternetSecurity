// A client-side class that uses a secure TCP/IP socket

import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.ssl.*;

public class SecureAdditionClient {
	private InetAddress host;
	private int port;
	// This is not a reserved port number 
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "linneakeystore.ks";
	static final String TRUSTSTORE = "linneatruststore.ks";
	static final String STOREPASSWD = "linnea";
	static final String ALIASPASSWD = "linnea";
	
	String option, filename; 
	
	// Constructor @param host Internet address of the host where the server is located
	// @param port Port number on the host where the server is listening
	public SecureAdditionClient( InetAddress host, int port ) {
		this.host = host;
		this.port = port;
	}
	
  // The method used to start a client object
	public void run() {
		try {
			KeyStore ks = KeyStore.getInstance( "JCEKS" );
			ks.load( new FileInputStream( KEYSTORE ), STOREPASSWD.toCharArray() );
			
			KeyStore ts = KeyStore.getInstance( "JCEKS" );
			ts.load( new FileInputStream( TRUSTSTORE ), STOREPASSWD.toCharArray() );
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
			kmf.init( ks, ALIASPASSWD.toCharArray() );
			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
			tmf.init( ts );
			
			SSLContext sslContext = SSLContext.getInstance( "TLS" );
			sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
			SSLSocketFactory sslFact = sslContext.getSocketFactory();      	
			SSLSocket client =  (SSLSocket)sslFact.createSocket(host, port);
			client.setEnabledCipherSuites( client.getSupportedCipherSuites() );
			System.out.println("\n>>>> SSL/TLS handshake completed");

			displayMenu();
			
			BufferedReader socketIn;
			socketIn = new BufferedReader( new InputStreamReader( client.getInputStream() ) );
			PrintWriter socketOut = new PrintWriter( client.getOutputStream(), true );
			
			String numbers = option; //"1.2 3.4 5.6";
			System.out.println( ">>>> Sending the numbers " + numbers+ " to SecureAdditionServer" );
			socketOut.println(option); //+ filename);
			socketOut.println(filename);
			System.out.println( socketIn.readLine() );
			//System.out.println( socketIn.readLine() );

			socketOut.println ( "" );
		}
		catch( Exception x ) {
			System.out.println( x );
			x.printStackTrace();
		}
	}
	
	private void displayMenu() throws IOException {
		
		System.out.println("Select an option:");
		System.out.println("1. Download file");
		System.out.println("2. Upload file");
		System.out.println("3. Delete file");
		System.out.println("4. Quit");
		System.out.println("Enter option: ");
		
		BufferedReader optionIn = new BufferedReader(new InputStreamReader(System.in));
		option = optionIn.readLine();
		System.out.println("Enter filename");
		filename = optionIn.readLine();
		System.out.println(option + " " + filename);
	}
	
	// The test method for the class @param args Optional port number and host name
	public static void main( String[] args ) {
		try {
			InetAddress host = InetAddress.getLocalHost();
			int port = DEFAULT_PORT;
			if ( args.length > 0 ) {
				port = Integer.parseInt( args[0] );
			}
			if ( args.length > 1 ) {
				host = InetAddress.getByName( args[1] );
			}
			SecureAdditionClient addClient = new SecureAdditionClient( host, port );
			addClient.run();
		}
		catch ( UnknownHostException uhx ) {
			System.out.println( uhx );
			uhx.printStackTrace();
		}
	}
}

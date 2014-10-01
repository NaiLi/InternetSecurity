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

			int op;
			
			do {
				displayMenu();
				
				BufferedReader socketIn;
				socketIn = new BufferedReader( new InputStreamReader( client.getInputStream() ) );
				PrintWriter socketOut = new PrintWriter( client.getOutputStream(), true );
				
				// sending option and filename to server
				socketOut.println(option);
				socketOut.println(filename);
				
				op = Integer.parseInt(option); 
				
				// handle options on the client side
				switch(op) {
					case 1:
						// read everything from the server
						FileWriter fileWriterOut = new FileWriter(filename);
						PrintWriter printWriterOut = new PrintWriter(new BufferedWriter(fileWriterOut), true);

						int lenghtFile = Integer.parseInt(socketIn.readLine()); // gets the filelength so we know when to stop reading

						String line1 = socketIn.readLine();
					    while (new File(filename).length() < lenghtFile) { // to stop when end of file
					    	printWriterOut.println(line1); 
					        line1 = socketIn.readLine();
					    }
					    System.out.println("finished reading ");
					    fileWriterOut.close();
					    printWriterOut.close();
					    
					    break;
					case 2:
						// upload
						FileReader fileReaderIn = new FileReader(filename);
						BufferedReader br = new BufferedReader( fileReaderIn );

						socketOut.println(new File(filename).length()); // sends the length of the file

						String line2 = br.readLine();
					    while (line2!=null) {
					        socketOut.println(line2); // sending text from fileName to server
					        line2 = br.readLine();
					    }
					    fileReaderIn.close();
						break;
					case 3: 
						// delete
						break;
					case 4:
						// quit program
						break;
					default:
						op  = 4; // just to quit everything 
						break;
				}
			}while(op !=4);		    
		}
		catch( Exception x ) {
			System.out.println( "Exception: " + x );
			x.printStackTrace();
		}
	}
	
	/* Function to display menu to user
	 */
	private void displayMenu() throws IOException {
		System.out.println(" ");
		System.out.println("Select an option:");
		System.out.println("1. Download file");
		System.out.println("2. Upload file");
		System.out.println("3. Delete file");
		System.out.println("4. Quit");
		System.out.println("Enter option: ");
		
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader optionIn = new BufferedReader(input);
		option = optionIn.readLine();
		System.out.println("Enter filename");
		filename = optionIn.readLine();
		
		//input.close();
		//optionIn.close();
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

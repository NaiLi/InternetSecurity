
// An example class that uses the secure server socket class

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.*;
import java.util.StringTokenizer;


public class SecureAdditionServer {
	private int port;
	// This is not a reserved port number
	static final int DEFAULT_PORT = 8189;
	static final String KEYSTORE = "linuskeystore.ks";
	static final String TRUSTSTORE = "linustruststore.ks";
	static final String STOREPASSWD = "linuss";
	static final String ALIASPASSWD = "linuss";
	
	private String file1 = "This is the first file in the supercool file place";
	
	/** Constructor
	 * @param port The port where the server
	 *    will listen for requests
	 */
	SecureAdditionServer( int port ) {
		this.port = port;
	}
	
	/** The method that does the work for the class */
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
            SSLServerSocketFactory sslServerFactory = sslContext.getServerSocketFactory();
            SSLServerSocket sss = (SSLServerSocket) sslServerFactory.createServerSocket( port );
            sss.setEnabledCipherSuites( sss.getSupportedCipherSuites() );
            sss.setNeedClientAuth(true); //this is added in order to force the server to ask client for authentication
            System.out.println("\n>>>> SecureAdditionServer: active ");
            SSLSocket incoming = (SSLSocket)sss.accept();

            BufferedReader in = new BufferedReader( new InputStreamReader( incoming.getInputStream() ) );
			PrintWriter out = new PrintWriter( incoming.getOutputStream(), true );			
			
			int option = Integer.parseInt(in.readLine());
			String filename = in.readLine();
			
			switch(option) {
				case 1:
					out.println(downloadFile(filename));
					break;
				case 2:
					uploadFile(filename);
					break;
				case 3:
					deleteFile(filename);
					break;
				default:
					incoming.close();
			}
			
			
			//out.println(option);
			//out.println(filename);
			
			incoming.close();
		}
		catch( Exception x ) {
			System.out.println( x );
			x.printStackTrace();
		}
	}
	
	/** Functions for handeling the different client options
	 * 
	 */
	public String downloadFile(String fileName) {
		//out.println(fileName);
		System.out.println("Downloading...");
		return file1;
		
	}
	public void uploadFile(String fileName) {
		//push file to the files
		System.out.println("Uploading...");
	}
	public void deleteFile(String fileName) {
		//pop file from files
		System.out.println("Deleting...");
	}
	
	/** The test method for the class
	 * @param args[0] Optional port number in place of
	 *        the default
	 */
	public static void main( String[] args ) {
		int port = DEFAULT_PORT;
		if (args.length > 0 ) {
			port = Integer.parseInt( args[0] );
		}
		SecureAdditionServer addServe = new SecureAdditionServer( port );
		addServe.run();
	}
}


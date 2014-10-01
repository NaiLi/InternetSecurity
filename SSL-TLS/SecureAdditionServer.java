
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
	
	PrintWriter out; 
	BufferedReader in;
	
	
	/** Constructor
	 * @param port The port where the server
	 *    will listen for requests
	 */
	SecureAdditionServer( int port ) {
		this.port = port;
	}
	
	/** The method that does the work for the class */
	public void run() throws IOException {
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

            in = new BufferedReader( new InputStreamReader( incoming.getInputStream() ) );
			out = new PrintWriter( incoming.getOutputStream(), true );			
			
			//do{
				// reads option and filename sent from client
				System.out.println("Reads option");
				int option = Integer.parseInt(in.readLine());
				String filename = in.readLine();
				
				// handle options on the server side
				switch(option) {
					case 1:
						downloadFile(filename);
						break;
					case 2:
						int lenghtFile = Integer.parseInt(in.readLine()); // gets the filelength so we know when to stop reading
						uploadFile(filename, lenghtFile);
						break;
					case 3:
						deleteFile(filename);
						break;
					case 4: 
						System.out.println("Closing down");
						incoming.close();
						break;
					default:
						System.out.println("Wrong option");
						incoming.close();
						break;
				}
			//} while(incoming.isConnected());
		}
		catch( Exception x ) {
			System.out.println( "Exception: " + x );
			x.printStackTrace();
		}
		
		System.out.println("End of function run..");
	}
	
	/** Functions for handeling the different client options
	 * 
	 */
	public void downloadFile(String fileName) throws IOException{
		System.out.println("Downloading...");
		out.println(new File("server/"+fileName).length()); // send the length of the file we are sending to the client
		readFromFile("server/"+fileName);
		
	}
	public void uploadFile(String fileName, int fileLength) throws IOException {
		//push file to the files
		System.out.println("Uploading...");
		FileWriter fileWriterOut = new FileWriter("server/"+fileName);
		PrintWriter printWriterOut = new PrintWriter(new BufferedWriter(fileWriterOut), true);
		
		String line = in.readLine();

	    while (new File("server/"+fileName).length() < fileLength) {
	    	printWriterOut.println(line); // behšver spara alt. skicka till clienten..
	        line = in.readLine();

	    }
	    fileWriterOut.close();
	    printWriterOut.close();
	    System.out.println("Uploading finished");
	}
	public void deleteFile(String fileName) {
		//pop file from files
		System.out.println("Deleting...");
		File file = new File("server/"+fileName);
		if(!file.delete()){
			System.out.println("Deleting failed.");
		}
	}
	
	private void readFromFile(String fileName) throws IOException{

		FileReader in = new FileReader(fileName);
		BufferedReader br = new BufferedReader( in );
		String line = br.readLine();
	    while (line!=null) {
	        out.println(line); // behšver spara alt. skicka till clienten..
	        line = br.readLine();
	    }
	    in.close();
	    br.close();
	    
	}
	
	/** The test method for the class
	 * @param args[0] Optional port number in place of
	 *        the default
	 */
	public static void main( String[] args ) throws IOException {
		int port = DEFAULT_PORT;
		if (args.length > 0 ) {
			port = Integer.parseInt( args[0] );
		}
		SecureAdditionServer addServe = new SecureAdditionServer( port );
		addServe.run();
	}
}


package secure_txt_file_content_retrieval;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Arrays;

/* PURPOSE: Sender objects send encrypted messages (hard-coded; stored in queue), along with message digests, to
 * Receiver objects. The Sender objects then retrieve the Receiver objects' responses and verify the integrity of that
 * response. If the response has not been tampered with, the file contents are displayed to the terminal.
 */
public class Sender implements Runnable {

    /* Constant value */
    // Value used to conveniently switch between showing encrypted messages and not showing encrypted messages
    static final boolean SHOW_ENCRYPT = false;

    /* Properties */

    // Queue data structure containing the messages to be sent to Receiver object
    private final Queue<String> msgQ = new LinkedList<String>(Arrays.asList("syllogism.txt", "ada.txt", "computer.txt", "world", "hello", "alan.txt"));

    // Message currently being sent to the Receiver object
    private String currMsg;

    // Response sent by the Receiver object to Sender object
    private ResponseMessage responseMsg;

    /* Instance of the MBRC (Message Buffer and Response Connector) class that is used to connect the communication
     * between the Sender and Receiver objects
     */
    private final MBRC connector;

    // Secret key used to encrypt messages - shared with Receiver object, generated in MBRC's main function
    private final SecretKey secretKey;

    // Cipher used to encrypt messages - shared with Receiver object, instantiated in MBRC's main function
    private final Cipher cipherDES;

    // MessageDigest object used to generate message digests (used in verifying the integrity of messages)
    private final MessageDigest mdGen;


    /* Constructor */
    public Sender(MBRC sessionConnector, SecretKey secKey, Cipher desCipher, MessageDigest msgDigGen) {

        this.connector = sessionConnector;

        this.secretKey = secKey;

        this.cipherDES = desCipher;

        this.mdGen = msgDigGen;

        // Instantiate and start new thread for Sender object
        Thread senderThread = new Thread(this, "Sender");

        senderThread.start();

    }

    /* Methods */

    /* Method called when thread's start() method is called */
    @Override
    public void run() {

        // Establish active connection
        this.connector.setActiveConnection(true);

        // Execute block while there are messages to send left in the queue
        while ((!(msgQ.isEmpty())) && (this.connector.getActiveConnection())) {

            // Dequeue message from msgQ & print the message to standard output
            this.currMsg = this.msgQ.remove();
            try {

                // Get current message in byte form and save to msgInBytes var
                byte[] msgInBytes = this.currMsg.getBytes();

                byte[] msgDigest = MessageSecurity.generate(this.mdGen, msgInBytes);

                // Pass in msgInBytes to encrypt() method; method returns cipher text as a byte array
                byte[] cipherText = MessageSecurity.encrypt(this.cipherDES, this.secretKey, msgInBytes);

                if (SHOW_ENCRYPT) {
                    String cipherTextString = new String(cipherText);
                    System.out.println("Encrypted message sent: " + cipherTextString);
                }

                /* Pass cipherText and msgDigest byte arrays into send() method
                 * Response from Receiver object is returned by the send() method
                 */
                this.responseMsg = this.connector.send(cipherText, msgDigest);
                byte[] decryptContentsBytes = MessageSecurity.decrypt(this.cipherDES, this.secretKey, this.responseMsg.getFileContents());
                String decryptContents = new String(decryptContentsBytes);

                // Verify the integrity of the message
                // If message integrity is not compromised, display message contents
                if (MessageSecurity.verify(this.mdGen, this.responseMsg.getMessageDigest(), decryptContentsBytes)) {
                    System.out.println("Message integrity of " + this.responseMsg.getFileName() + " has been verified.");
                    System.out.println(decryptContents + "\n");
                } else {
                    // If message integrity is compromised, display error message and kill connection to Receiver object
                    System.out.println("ERROR: Message integrity of " + this.responseMsg.getFileName() + " has been compromised.");
                    this.connector.setActiveConnection(false);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Runtime.getRuntime().exit(2);
            }
        }
        // Stop program execution
        this.connector.setActiveConnection(false);
        Runtime.getRuntime().exit(0);
    }
}

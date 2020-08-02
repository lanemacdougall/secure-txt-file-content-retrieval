package secure_txt_file_content_retrieval;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

/* PURPOSE: MBRC: "Message Buffer and Response Connector"
 *  MBRC connects the communications between Sender and Receiver objects
 *
 *  MBRC object's have buffers and boolean variables that facilitate encrypted and integral communication
 *  between Sender and Receiver objects
 */
public class MBRC {

    /* Properties */

    // Buffer containing Sender object's message in byte form
    private ByteBuffer messageBuffer;

    // Message digest of the message sent by the Sender object
    private ByteBuffer senderMessageDigestBuffer;

    // ResponseMessage object containing the Receiver object's response
    private ResponseMessage responseMsg;

    // Flag indicating whether or not the buffer containing the message is full
    private boolean msgBufferFull;

    // Flag indicating whether or not the buffer containing the response is full
    private boolean respReady;

    // Flag indicating that an active connection exists between Sender and Receiver objects
    private boolean activeConnection;


    /* Constructor */
    public MBRC() {

        this.msgBufferFull = false;

        this.respReady = false;

    }


    /* Methods */

    // activeConnection getter method
    public boolean getActiveConnection() {
        return this.activeConnection;
    }

    //activeConnection setter method
    public void setActiveConnection(boolean status) {
        this.activeConnection = status;
    }

    /* Method places a message (in byte array form) in the messageBuffer, waits for a response,
     * and then returns the response
     */
    public synchronized ResponseMessage send(byte[] message, byte[] md) throws InterruptedException {

        // Place byte array in message buffer using ByteBuffer wrap() method
        this.messageBuffer = ByteBuffer.wrap(message);

        this.senderMessageDigestBuffer = ByteBuffer.wrap(md);

        // Indicate that message buffer is full
        this.msgBufferFull = true;

        // Notify Receiver thread
        notify();

        // Pause Sender thread while there is not response from Receiver object
        while (!this.respReady) wait();

        // Reset the response ready flag to false
        this.respReady = false;

        return this.responseMsg;

    }


    // Method retrieves message from response buffer and returns it to the Receiver object
    public synchronized SenderMessage receive() throws InterruptedException {

        // Pause thread while message buffer is empty
        while (!this.msgBufferFull) wait();

        /* Once there is a message, retrieve it from the message buffer, store the message and
         * the corresponding message digest in a SenderMessage object, indicate that
         * the message buffer is no longer full, and return the SenderMessage object to the
         * Receiver object
         */
        SenderMessage receivedMsg = new SenderMessage(this.messageBuffer.array(), this.senderMessageDigestBuffer.array());

        this.msgBufferFull = false;

        return receivedMsg;

    }

    // Method places Receiver object's response message in the response buffer and notifies Sender thread
    public synchronized void reply(ResponseMessage respMsg) {

        this.responseMsg = respMsg;

        this.respReady = true;

        notify();
    }


    /* start method instantiates MBRC object, connector, a SecretKey object, a Cipher object, and Sender and
     * Receiver objects, thus activating synchronized communication between the Sender object's thread and
     * the Receiver object's thread
     */
    public static void main(String[] args) {

        MBRC connector = new MBRC();

        SecretKey secretKey = null;

        Cipher cipherDES = null;

        MessageDigest msgDigestGen = null;

        try {
            // Create KeyGenerator object
            KeyGenerator keyGenObj = KeyGenerator.getInstance("DES");
            // Generate a secret key
            secretKey = keyGenObj.generateKey();

            // Instantiate Cipher object - DES algorithm, ECB mode, PKCS5 Padding
            cipherDES = Cipher.getInstance("DES/ECB/PKCS5Padding");

            // Instantiate MessageDigest object - SHA-1 algorithm
            msgDigestGen = MessageDigest.getInstance("SHA-1");
        } catch (Exception e) {
            e.printStackTrace();
            Runtime.getRuntime().exit(1);
        }

        // Instantiate the Sender and Receiver objects - this starts running their individual threads
        Sender sender = new Sender(connector, secretKey, cipherDES, msgDigestGen);

        Receiver receiver = new Receiver(connector, secretKey, cipherDES, msgDigestGen);
    }

}

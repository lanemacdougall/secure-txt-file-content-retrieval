package secure_txt_file_content_retrieval;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.io.*;
import java.nio.file.*;

/* PURPOSE: Receiver object retrieves encrypted messages from Sender object, decrypts those messages, verifies the
 * integrity of those messages, and, if a message's integrity has not been compromised, retrieves the file specified in
 * the message, reads and encrypts its contents, and writes that encrypted data into a new file.
 *
 * That new file is sent, along with a message digest, in response to the Sender object. The message digest is included
 * in the response message so that the Sender object can verify that the response message's integrity has not been
 * compromised.
 */
public class Receiver implements Runnable {

    /* Constant value */
    // Value used to conveniently switch between showing encrypted messages and not showing encrypted messages
    static final boolean SHOW_ENCRYPT = false;

    /* Properties */

    // Message sent by Sender Object in byte form
    private SenderMessage currMsg;

    // File containing encrypted data to be returned to the Sender object
    private File returnFile;

    // Message digest generated using the contents of the file specified by the Sender object's message
    private byte[] messageDigest;

    // Number of files specified in Sender messages that could not be found
    private int filesNotFound;

    /* Instance of the MBRC (Message Buffer and Response Connector) class that is used to connect the communication
     * between the Sender and Receiver objects
     */
    private final MBRC connector;

    // Secret key used to decrypt messages - shared with Sender object, generated in MBRC's main function
    private final SecretKey secretKey;

    // Cipher used to decrypt messages - shared with Sender object, instantiated in MBRC's main function
    private final Cipher cipherDES;

    // MessageDigest object used to generate message digests (used in verifying the integrity of messages)
    private final MessageDigest mdGen;


    /* Constructor */
    public Receiver(MBRC sessionConnector, SecretKey secKey, Cipher desCipher, MessageDigest msgDigGen) {

        this.filesNotFound = 0;

        this.connector = sessionConnector;

        this.secretKey = secKey;

        this.cipherDES = desCipher;

        this.mdGen = msgDigGen;

        // Instantiate and start new thread for Sender object
        Thread receiverThread = new Thread(this, "Receiver");

        receiverThread.start();

    }


    /* Methods */

    /* Method retrieves the file specified by the filepath sent from the Sender object
     * (if the file exists), encrypts the files contents, and then returns a new file
     * containing the encrypted file contents.
     *
     * Method utilizes the java.nio API's Files class
     */
    public void prepareOutgoingFile(String filepath) throws Exception {
        byte[] inputFileBytes;
        byte[] encryptedFileBytes;
        String newFilePath = "";
        try {
            inputFileBytes = Files.readAllBytes(Paths.get(filepath));
            /* Generate a message digest to be sent along with result in a reply message to Sender object
             *
             *  Sender object uses the message digest is used to confirm the integrity of the message and to verify
             *  that the message has not been tampered with
             */
            this.messageDigest = MessageSecurity.generate(this.mdGen, inputFileBytes);
            encryptedFileBytes = MessageSecurity.encrypt(this.cipherDES, this.secretKey, inputFileBytes);
            newFilePath = "secure_" + filepath;
            Files.write(Paths.get(newFilePath), encryptedFileBytes);

        } catch (NoSuchFileException e) {
            inputFileBytes = ("Filename \"" + filepath + "\" Was Not Found.").getBytes();
            this.messageDigest = MessageSecurity.generate(this.mdGen, inputFileBytes);
            encryptedFileBytes = MessageSecurity.encrypt(this.cipherDES, this.secretKey, inputFileBytes);
            if (this.filesNotFound == 0)
                newFilePath = "File_Not_Found.txt";
            else
                newFilePath = "File_Not_Found_" + this.filesNotFound + ".txt";
            this.filesNotFound++;

            Files.write(Paths.get(newFilePath), encryptedFileBytes);
        }
        this.returnFile = new File(newFilePath);
    }

    /* Method called when thread's start() method is called */
    @Override
    public void run() {

        // While there is an active connection between the Sender and Receiver objects, process messages
        while (this.connector.getActiveConnection()) {

            try {

                // Call MBRC object's receive() method to retrieve method
                this.currMsg = this.connector.receive();

                if (SHOW_ENCRYPT) {
                    String cipherTextString = new String(this.currMsg.getMessage());
                    System.out.println("Encrypted message received: " + cipherTextString);
                }


                // Call decrypt() method to retrieve plain text message in byte form and then convert to a String
                byte[] plainTextBytes = MessageSecurity.decrypt(this.cipherDES, this.secretKey, this.currMsg.getMessage());

                // If the message has not been tampered with, respond to Sender object
                if (MessageSecurity.verify(this.mdGen, this.currMsg.getMessageDigest(), plainTextBytes)) {

                    String plainTextStr = new String(plainTextBytes);

                    // Prepare the outgoing file using the contents of the file specified by the Sender object's message
                    this.prepareOutgoingFile(plainTextStr);

                    // Store the outgoing file and the message digest in a ResponseMessage object to be sent back to Sender object
                    ResponseMessage replyMsg = new ResponseMessage(this.returnFile, this.messageDigest);

                    // Call MBRC object's reply() method to send response to Sender object
                    this.connector.reply(replyMsg);
                } else {
                    // If the message has been tampered with, kill the connection with the Sender object
                    this.connector.setActiveConnection(false);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Runtime.getRuntime().exit(5);
            }
        }
    }
}

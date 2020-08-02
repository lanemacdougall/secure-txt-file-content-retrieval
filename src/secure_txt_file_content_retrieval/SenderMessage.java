package secure_txt_file_content_retrieval;

/* PURPOSE: A SenderMessage object is used as an efficient means of passing both a message and a message digest between
 *  (both byte[] arrays) the Sender object, the MBRC object, and the Receiver object. An instance of the SenderMessage
 *  class has attributes that can contain both of these pieces of information.
 */
public class SenderMessage {

    // Message being sent by the Sender object
    private final byte[] message;

    // Message digest of the message being sent by the Sender object - used to verify message integrity
    private final byte[] messageDigest;

    /* Constructor */
    public SenderMessage(byte[] msg, byte[] md) {

        this.message = msg;

        this.messageDigest = md;

    }


    /* Methods */

    // message getter method
    public byte[] getMessage() {
        return this.message;
    }

    // messageDigest getter method
    public byte[] getMessageDigest() {
        return this.messageDigest;
    }

}

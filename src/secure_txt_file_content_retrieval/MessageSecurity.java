package secure_txt_file_content_retrieval;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.MessageDigest;

/* PURPOSE: The MessageSecurity class provides static methods that allow for the encryption and decryption of messages
 *  and for the creation and verification of message digests (used to verify the integrity of messages).
 */
public class MessageSecurity {


    /* Given an array of bytes, a Cipher object, and a secret key, method encrypts the
     * byte array
     */
    public static byte[] encrypt(Cipher cipherDES, SecretKey secretKey, byte[] byteArray) throws Exception {
        // Initialize the cipher in encryption mode with a given secret key
        cipherDES.init(Cipher.ENCRYPT_MODE, secretKey);

        // Encrypt and return the given byte array
        return cipherDES.doFinal(byteArray);

    }

    /* Given a byte array, a Cipher object, and a secret key, method decrypts a previously
     * encrypted message (represented by the byte array)
     */
    public static byte[] decrypt(Cipher cipherDES, SecretKey secretKey, byte[] fileContents) throws Exception {

        // Initialize a cipher in decryption mode with the given secret key
        cipherDES.init(Cipher.DECRYPT_MODE, secretKey);

        // Decrypt and return the given byte array
        return cipherDES.doFinal(fileContents);
    }

    /* Method generates a method digest that is sent along with the reply message to the Sender object, so that the Sender
     * object can verify the integrity of the message (implemented for integrity security)
     */
    public static byte[] generate(MessageDigest mdGen, byte[] fileContents) throws Exception {

        // Update the MessageDigest object with the file contents that will be used to generate the message digest
        mdGen.update(fileContents);

        // Generate message digest
        return mdGen.digest();
    }

    /* Method verifies the integrity of the message by comparing a generated message digest to the
     * message digest received with the message
     */
    public static boolean verify(MessageDigest mdGen, byte[] receivedMD, byte[] fileContents) throws Exception {

        // Update the MessageDigest object with the file contents that will be used to make the message digest
        mdGen.update(fileContents);

        // Generate message digest to be compared with the received message digest
        byte[] generatedMD = mdGen.digest();

        // Compare generated message digest with received message digest to verify the integrity of the received message
        return MessageDigest.isEqual(receivedMD, generatedMD);
    }

}

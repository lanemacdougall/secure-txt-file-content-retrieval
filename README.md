# Secure Text File Content Retrieval

### Notes: 
(1) This is an adaptation of a homework project assigned in Texas Tech University's Secure Software Engineering course (CS 5332). See "Description of Original Project" for more details. 

(2) This program is intended to be a learning resource and is not interactive nor very useful on its own. However, it is a great source of information for anyone interested in multi-threaded programs, synchronous communication between threads, or message security. I want to provide others with the best resource possible, so please feel free to contribute in order to make this program a better learning tool.


## Overview

Secure Text File Content Retrieval is a multi-threaded Java program implementing secure, synchronous communication between a Sender object and a Receiver object through a message buffer and response connector (MBRC) object, allowing for the secure retrieval of text file contents.

The main method of the MBRC class initializes the secret key, cipher, and Sender and Receiver objects to be used by the program. The Sender and Receiver objects run on two separate threads, and the functionality of the program is carried out by these objects' run methods.

The Sender object sends an encrypted (DES algorithm) message specifying the name of a text file to the Receiver object through the MBRC object (and then waits for a response - hence, synchronous). 

The Receiver object receives and decrypts the message, and then attempts to retrieve the file. If the file exists, the Receiver object reads and encrypts (DES algorithm) the file's contents, and then writes the encrypted file contents into a new text file. If the file does not exist, a message stating such is encrypted and written into a new file
This new text file is sent, along with a message digest (generated using the SHA-1 algorithm), as a response to the Sender object through the MBRC object (the Receiver object then waits for the next message). 

Once the Sender object has received the response, the file contents are decrypted, and the message digest is used to verify that the integrity of the message has not been compromised. If the message has not been tampered with, the file contents are converted into a readable format and printed out to the console. Otherwise, an error message is displayed.

Names of text files, both retrievable and not, are stored in a queue within the Sender object. So long as the queue is not empty, the Sender object will encrypt and send these file names to the Receiver object using the MBRC object. Once all of the filenames in the queue have been sent and responded to, the program finishes executing.


## Description of Original Project

Please note that, while this program is an adaptation of a previous project, all work uploaded to this repository (before any outside contributions that may come after the writing of this README file) are my own. The work done for the original program and the adaptations reflected in this program are all my own.

The original project required that the Sender object send instructions specifying arithmetic operations that the Receiver object was to carry out. The Receiver object calculated the result of the instruction and then sent the result back to the Sender object. The original project implemented the same communication and security patterns as this project.

The patterns, tools, and concepts implemented and used in this original project were very interesting to me, however, I wanted to build an application that performed a more complex task than simple arithmetic. Therefore, I adapted the project to retrieve text file contents.


## Download

git clone https://github.com/lanemacdougall/secure-txt-file-content-retrieval.git or download the zip file.

## Usage

Run MBRC.main()

### Expected output

Message integrity of secure_syllogism.txt has been verified.
“I'm afraid that the following syllogism may be used by some in the future.

Turing believes machines think
Turing lies with men
Therefore machines do not think

Yours in distress,

Alan”

Message integrity of secure_ada.txt has been verified.
“I never am really satisfied that I understand anything; 
because, understand it well as I may, my comprehension can only be an infinitesimal fraction 
of all I want to understand about the many connections and relations which occur to me, 
how the matter in question was first thought of or arrived at…”

- Ada Lovelace

Message integrity of File_Not_Found.txt has been verified.
Filename "computer.txt" Was Not Found.

Message integrity of File_Not_Found_1.txt has been verified.
Filename "world" Was Not Found.

Message integrity of File_Not_Found_2.txt has been verified.
Filename "hello" Was Not Found.

Message integrity of secure_alan.txt has been verified.
"This is the real secret of life — to be completely engaged with what you are doing in the here and now. And instead of calling it work, realize it is play."

- Alan Watts


Process finished with exit code 0


## Contributing
Pull requests are welcome. Please see the "Planned Additions/Changes" section for any features that I plan to add or change in the future.


## Planned Additions/Changes

Allow for the user to enter filenames, rather than having the filenames hardcoded into the program.

Encrypt the text files, rather than their contents.

Build a dynamic interface that allows for the user to better visualize what is happening in the program.


## License

Secure Text File Content Retrieval is available under the [MIT license](https://github.com/lanemacdougall/secure-txt-file-content-retrieval/blob/master/LICENSE).

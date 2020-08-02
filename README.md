# Secure Text File Content Retrieval


## Overview

Note: This is an adaptation of a homework project assigned in Texas Tech University's Secure Software Engineering course (CS 5332). See "Description of Original Project" for more details. 

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

git clone https://github.com/lanemacdougall/secure-txt-file-content-retrieval.git


## Usage

Run MBRC.java file

## Contributing
Pull requests are welcome. Please see the "Planned Additions/Changes" section for any features that I plan to add or change in the future.


## Planned Additions/Changes

Allow for the user to enter filenames, rather than having the filenames hardcoded into the program.

Encrypt the text files, rather than their contents.

Build a dynamic interface that allows for the user to better visualize what is happening in the program.

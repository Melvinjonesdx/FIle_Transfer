package raynal.file_transfer_socket;

import java.io.*;
import java.net.*;

public class FileTransfer_Server {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {
            // Start server on port 8000
            serverSocket = new ServerSocket(8000);
            System.out.println("Server started. Waiting for client...");

            // Accept a client connection
            socket = serverSocket.accept();
            System.out.println("Client connected.");

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            // Directory to store files
            File folder = new File("Server Files");

            // Check if folder exists or create it
            if (!folder.exists() && !folder.mkdirs()) {
                System.err.println("Error: Unable to create 'Server Files' directory.");
                dos.writeUTF("Server error: Unable to create directory.");
                return;
            }

            // Validate folder is a directory
            if (!folder.isDirectory()) {
                System.err.println("Error: 'Server Files' is not a valid directory.");
                dos.writeUTF("Server error: Invalid directory.");
                return;
            }

            // Main communication loop
            boolean running = true;
            while (running) {
                // Send menu to the client
                dos.writeUTF("Choose an option:\n1. Download File\n2. List Files\n3. Exit");
                dos.flush();

                // Get client option
                int choice = dis.readInt();
                switch (choice) {
                    case 1: // Download file
                        dos.writeUTF("Enter the name of the file to download:");
                        dos.flush();
                        String requestedFileName = dis.readUTF();

                        File requestedFile = new File(folder, requestedFileName);
                        if (requestedFile.exists() && requestedFile.isFile()) {
                            dos.writeUTF("File found. Sending file...");
                            dos.flush();

                            // Send file to the client
                            FileInputStream fis = new FileInputStream(requestedFile);
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = fis.read(buffer)) != -1) {
                                dos.write(buffer, 0, bytesRead);
                            }
                            fis.close();
                            dos.flush();
                            dos.writeUTF("File transfer complete.");
                        } else {
                            dos.writeUTF("File not found.");
                        }
                        dos.flush();
                        break;

                    case 2: // List files
                        File[] listOfFiles = folder.listFiles();
                        if (listOfFiles == null || listOfFiles.length == 0) {
                            dos.writeUTF("No files available on the server.");
                        } else {
                            StringBuilder fileList = new StringBuilder("Available files:\n");
                            for (File file : listOfFiles) {
                                if (file.isFile()) {
                                    fileList.append(file.getName()).append("\n");
                                }
                            }
                            dos.writeUTF(fileList.toString());
                        }
                        dos.flush();
                        break;

                    case 3: // Exit
                        dos.writeUTF("Goodbye!");
                        dos.flush();
                        running = false;
                        break;

                    default:
                        dos.writeUTF("Invalid option. Try again.");
                        dos.flush();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) dis.close();
                if (dos != null) dos.close();
                if (socket != null) socket.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

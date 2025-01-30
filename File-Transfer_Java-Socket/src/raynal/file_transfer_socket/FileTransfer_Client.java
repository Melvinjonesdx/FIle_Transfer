package raynal.file_transfer_socket;

import java.io.*;
import java.net.*;

public class FileTransfer_Client {
    public static void main(String[] args) {
        Socket socket = null;
        DataInputStream dis = null;
        DataOutputStream dos = null;

        try {
            // Connect to the server
            socket = new Socket("localhost", 8000);
            System.out.println("Connected to the server.");

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            boolean running = true;
            while (running) {
                // Read and display server message
                System.out.println("Server: " + dis.readUTF());

                // Get user choice
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                int choice = Integer.parseInt(userInput.readLine());
                dos.writeInt(choice);
                dos.flush();

                switch (choice) {
                    case 1: // Download file
                        System.out.println("Server: " + dis.readUTF());
                        String requestedFileName = userInput.readLine();
                        dos.writeUTF(requestedFileName);
                        dos.flush();

                        String response = dis.readUTF();
                        if (response.equals("File found. Sending file...")) {
                            System.out.println("Receiving file...");

                            FileOutputStream fos = new FileOutputStream("Downloaded_" + requestedFileName);
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = dis.read(buffer)) > 0) {
                                fos.write(buffer, 0, bytesRead);
                            }
                            fos.close();
                            System.out.println("File received.");
                        } else {
                            System.out.println("Server: " + response);
                        }
                        break;

                    case 2: // List files
                        System.out.println("Server: " + dis.readUTF());
                        break;

                    case 3: // Exit
                        System.out.println("Server: " + dis.readUTF());
                        running = false;
                        break;

                    default:
                        System.out.println("Server: " + dis.readUTF());
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadedDownloader {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of threads: ");
        int numThreads = scanner.nextInt();

        System.out.print("Enter the file with links: ");
        String linksFilePath = scanner.next();

        try {
            List<String> links = readLinksFromFile(linksFilePath);

            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

            for (String link : links) {
                Runnable downloader = new FileDownloader(link);
                executorService.execute(downloader);
            }

            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static List<String> readLinksFromFile(String filePath) throws IOException {
        List<String> links = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                links.add(line);
            }
        }
        return links;
    }

    static class FileDownloader implements Runnable {
        private final String fileUrl;

        public FileDownloader(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(fileUrl);
                try (InputStream in = url.openStream();
                     BufferedInputStream bis = new BufferedInputStream(in)) {

                    String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
                    try (OutputStream out = new FileOutputStream(fileName)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = bis.read(buffer, 0, 4096)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        System.out.println("Downloaded: " + fileName);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

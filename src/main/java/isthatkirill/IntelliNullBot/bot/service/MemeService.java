package isthatkirill.IntelliNullBot.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

@Slf4j
@Service
public class MemeService {

    private String randomURL() throws IOException {
        Random random = new Random();
        Integer randInt = random.nextInt(1000);
        Integer targetPhotoId = 457239992 - randInt;
        String url = "https://vk.com/photo-205359325_" + targetPhotoId;
        Document document = Jsoup.connect(url).get();
        Elements images = document.select("meta");
        return images.get(5).attr("value");
    }

    public InputFile getMeme() {
        try {
            String imageURL = randomURL();
            while (imageURL.isBlank()) {
                imageURL = randomURL();
            }
            return convertToInputFile(downloadPhoto(imageURL));
        } catch (IOException e) {
            log.warn("[meme] Error then downloading photo");
            return null;
        }
    }

    private InputFile convertToInputFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        InputFile inputFile = new InputFile(inputStream, file.getName());
        Files.delete(Paths.get("src/main/resources/temp/" + file.getName()));
        return inputFile;
    }

    private File downloadPhoto(String photoUrl) throws IOException {
        URL url = new URL(photoUrl);
        String generateFileName = System.currentTimeMillis() + ".jpg";
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
             FileOutputStream fileOutputStream =
                     new FileOutputStream("src/main/resources/temp/ph" + generateFileName)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
        return new File("src/main/resources/temp/ph" + generateFileName);
    }


}

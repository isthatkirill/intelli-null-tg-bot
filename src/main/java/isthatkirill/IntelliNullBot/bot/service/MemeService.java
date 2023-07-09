package isthatkirill.IntelliNullBot.bot.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
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
            return convertToInputFile(downloadPhoto(imageURL), LocalDateTime.now().toString());
        } catch (IOException e) {
            log.warn("[meme] Error then downloading photo");
            return null;
        }
    }

    private InputFile convertToInputFile(byte[] imageData, String fileName) {
        InputStream inputStream = new ByteArrayInputStream(imageData);
        return new InputFile(inputStream, fileName);
    }

    private byte[] downloadPhoto(String photoUrl) throws IOException {
        URL url = new URL(photoUrl);
        try (InputStream in = new BufferedInputStream(url.openStream())) {
            return IOUtils.toByteArray(in);
        }
    }


}

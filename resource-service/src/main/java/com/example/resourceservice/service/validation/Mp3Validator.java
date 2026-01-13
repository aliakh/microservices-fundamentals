package com.example.resourceservice.service.validation;

import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class Mp3Validator {

    public boolean valid(byte[] audio) {
        var tika = new Tika();
        try (InputStream is = new ByteArrayInputStream(audio)) {
            var mimeType = tika.detect(is);
            return "audio/mpeg".equalsIgnoreCase(mimeType);
        } catch (IOException e) {
            return false;
        }
    }
}

package com.example.resourceservice.service;

import com.example.resourceservice.exception.InvalidMp3FileException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class MetadataParser {

    public Metadata parse(byte[] audio) {
        try (InputStream is = new ByteArrayInputStream(audio)) {
            var parser = new Mp3Parser();
            var handler = new DefaultHandler();
            var metadata = new Metadata();
            var context = new ParseContext();

            parser.parse(is, handler, metadata, context);

            return metadata;
        } catch (IOException | SAXException | TikaException e) {
            throw new InvalidMp3FileException("Failed to extract metadata from MP3 file");
        }
    }
}

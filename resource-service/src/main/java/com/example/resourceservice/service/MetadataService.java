package com.example.resourceservice.service;

import com.example.resourceservice.dto.CreateSongDto;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.XMPDM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetadataService {

    @Autowired
    private MetadataParser metadataParser;

    public CreateSongDto extractSongMetadata(byte[] audio, Long id) {
        var metadata = metadataParser.parse(audio);

        var title = metadata.get(TikaCoreProperties.TITLE);
        var artist = metadata.get(TikaCoreProperties.CREATOR);
        var album = metadata.get(XMPDM.ALBUM);
        var duration = formatDuration(metadata.get(XMPDM.DURATION));
        var year = extractYear(metadata.get(XMPDM.RELEASE_DATE));

        return new CreateSongDto(
            id,
            title,
            artist,
            album,
            duration,
            year != null ? year.toString() : null
        );
    }

    private String formatDuration(String duration) {
        if (duration == null || duration.trim().isEmpty()) {
            return null;
        }

        try {
            int totalSeconds = (int) Math.round(Double.parseDouble(duration));
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;

            return String.format("%02d:%02d", minutes, seconds);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer extractYear(String releaseDate) {
        if (releaseDate == null || releaseDate.trim().isEmpty()) {
            return null;
        }

        try {
            var year = releaseDate.replaceAll("[^0-9]", "");
            if (year.length() >= 4) {
                return Integer.parseInt(year.substring(0, 4));
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

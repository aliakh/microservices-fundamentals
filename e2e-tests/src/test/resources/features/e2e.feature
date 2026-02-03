Feature: check upload services possibilities

  Scenario: file uploaded, metadata parsed and persisted
    When upload the audio file '/audio/audio2.mp3' to the resource service
    Then wait for the resource processor to consume the resource
    And I get the song metadata from the song service
      """
      {
        "name": "Impact Moderato",
        "artist": "Kevin MacLeod",
        "album": "Impact",
        "duration": "01:16",
        "year": "2014"
      }
      """
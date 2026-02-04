Feature: Communication between the resource service, the resource processor, and the song service

  Scenario: Uploading a resource, parsing the resource, and retrieving song metadata
    When the user upload the resource '/audio/audio2.mp3' to the resource service
    Then the user wait for the resource processor to consume the resource
    And the user get the song metadata from the song service
      """
      {
        "name": "Impact Moderato",
        "artist": "Kevin MacLeod",
        "album": "Impact",
        "duration": "01:16",
        "year": "2014"
      }
      """
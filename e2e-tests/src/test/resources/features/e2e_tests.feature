Feature: Communication between the resource service, the resource processor, and the song service

  Scenario: Uploading a resource, parsing the resource, and retrieving song metadata
    When user uploads the resource '/audio/audio2.mp3' to the resource service
    Then user waits for the resource processor to parse the resource
    And user retrieves the song metadata from the song service
      """
      {
        "name": "Impact Moderato",
        "artist": "Kevin MacLeod",
        "album": "Impact",
        "duration": "01:16",
        "year": "2014"
      }
      """
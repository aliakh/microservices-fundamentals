Feature: End-to-End Song Upload and Processing
  As a user of the microservices system
  I want to upload MP3 files and have them processed
  So that song metadata is extracted and stored

  Scenario: Upload song and verify end-to-end processing
    Given all microservices are running
    When I upload a valid MP3 file to the resource service
    Then the upload should be successful
    And I should receive a resource ID
    And the resource should trigger processing
    And the song metadata should be extracted and stored in song service
    And I should be able to retrieve both the resource and song data

  Scenario: Upload invalid file format
    Given all microservices are running
    When I upload an invalid file format
    Then the upload should be rejected
    And I should receive an appropriate error message

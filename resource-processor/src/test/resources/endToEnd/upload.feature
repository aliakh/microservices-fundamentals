Feature: check upload services possibilities

  Scenario: file uploaded, metadata parsed and persisted
    When upload file with name 'Test.mp3' to the resource service
    Then wait for the resource processor to consume the resource
    And And I get the song metadata from the song service
Feature: check upload services possibilities

  Scenario: file uploaded, metadata parsed and persisted
    When upload file with name 'Test.mp3' to the resource service
    Then wait for processor service to parse data
    And check data is saved via GET call to the song service
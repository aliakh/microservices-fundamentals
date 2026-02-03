Feature: Create a new resource - happy path

  Scenario: Successfully create a new resource
    Given The API Gateway is up
    When I send a "audio/mpeg" request to "/resources" with the binary body from file: "valid-sample-with-required-tags.mp3"
    Then the "resources" response status should be 200
    And the response body should be a json with the resource id
    When I send a GET song request with the returned id
    Then the "songs" response status should be 200
    And returned values should be: "Test Title", "Test Artist", "Test Album", "00:08", "2025"

Feature: Uploading, getting, and deleting a resource

  Scenario: Upload a resource
    When user makes POST request to upload file "audio1.mp3"
    Then the resource creation response code is 200
    And the resource creation content type is "application/json"
    And the resource creation response body is
      """
      {"id": 1}
      """
    Then the resources are saved to the database
      | id |
      | 1  |

  Scenario: Get a resource
    When user gets resource by id=1
    Then the resource retrieval response code is 200
    And the resource retrieval response content type is "audio/mpeg"
    And the resource retrieval body has size 31808

  Scenario: Delete a resource
    When user deletes the resource by id=1
    Then the resource deletion response code is 200
    And the resource deletion response content type is "application/json"
    And the resource deleting response body is
      """
      {"ids": [1]}
      """

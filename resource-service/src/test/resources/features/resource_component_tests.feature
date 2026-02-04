#@transaction
Feature: Uploading, getting, and deleting a resource

  Scenario: Upload a resource
    When user makes POST request to upload file "audio1.mp3"
    Then response code is 200
    And response content type is "application/json"
    And resource uploaded response is
      """
      {"id": 1}
      """
    Then the following resources are saved
      | id |
      | 1  |

  Scenario: Get a resource
    When user gets resource by id=1
    Then response code is 200
    And response content type is "audio/mpeg"
    And response body has size 31808

  Scenario: Delete a resource
    When user deletes the resource by id=1
    Then response code is 200
    And response content type is "application/json"
    And resources deleted response is
      """
      {"ids": [1]}
      """

#@transaction
Feature: Upload, get and delete resources

  Scenario: Upload resource
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

  Scenario: Get resource
#    Given the following resources uploaded
#      | id | key        |
#      | 2  | audio2.mp3 |
    When user gets resource with id=1
    Then response code is 200
    And response content type is "audio/mpeg"
    And response body has size 31808

  Scenario: Delete resource
#    Given the following resources uploaded
#      | id | key        |
#      | 3  | audio3.mp3 |
    When user deletes resource with id=1
    Then response code is 200
    And response content type is "application/json"
    And resources deleted response is
      """
      {"ids": [1]}
      """

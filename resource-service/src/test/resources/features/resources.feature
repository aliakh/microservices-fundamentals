@transaction
Feature: Upload, download and delete resources

  The resource service allows to upload, download, and delete resources

  Scenario: Upload resource
    When user uploads file "audio1.mp3"
    Then response code is 200
    And response content type is "application/json"
    And resource uploaded response is
      """
      {"id": 1}
      """
    Then the following resources are saved
      | id |
      | 1  |

  Scenario: Download resource
    Given the following resources uploaded
      | id | key        |
      | 2  | audio2.mp3 |
    When user downloads resource with id=2
    Then response code is 200
    And response content type is "audio/mpeg"
    And response body has size 29010


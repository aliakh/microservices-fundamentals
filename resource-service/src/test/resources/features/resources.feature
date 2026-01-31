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
      | id | bucket    | name       | size  |
      | 1  | resources | audio1.mp3 | 31808 |


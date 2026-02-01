#@transaction TODO
Feature: Upload, download and delete resources

  The resource service allows to upload, get, and delete resources

  Scenario: Create song
    When user makes POST request to create song
      | id | name                 | artist | album             | duration | year |
      | 1  | We are the champions | Queen  | News of the world | 02:59    | 1977 |
    Then response code is 200
    And response content type is "application/json"
    And resource uploaded response is
      """
      {"id": 1}
      """
    Then the following resources are saved
      | id | name                 | artist | album             | duration | year |
      | 1  | We are the champions | Queen  | News of the world | 02:59    | 1977 |

  Scenario: Get resource
    When user gets resource with id=1
    Then response2 code is 200
    And response2 content type is "application/json"
    And resource uploaded response2 is
      """
        {
    "id": 1,
    "name": "We are the champions",
    "artist": "Queen",
    "album": "News of the world",
    "duration": "02:59",
    "year": 1977
  }
      """

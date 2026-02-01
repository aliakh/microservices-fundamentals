#@transaction TODO
Feature: Create, get, and delete songs

  Scenario: Create a song
    When the user sends a POST request to the /songs endpoint
      | id | name                 | artist | album             | duration | year |
      | 1  | We are the champions | Queen  | News of the world | 02:59    | 1977 |
    Then the song creation response code is 200
    And the song creation content type is "application/json"
    And the song creation response is
      """
      {
        "id": 1
      }
      """
    Then the songs are saved to the database
      | id | name                 | artist | album             | duration | year |
      | 1  | We are the champions | Queen  | News of the world | 02:59    | 1977 |

  Scenario: Get a song
    When the user sends a GET request to the /songs/1 endpoint
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

  Scenario: Delete a song
    When the user sends a DELETE request to the /songs?id=1 endpoint
    Then response3 code is 200
    And response3 content type is "application/json"
    And resource uploaded response3 is
      """
      {
        "ids": [1]
      }
      """

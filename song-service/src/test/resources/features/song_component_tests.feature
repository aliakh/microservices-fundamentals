Feature: Create, get and delete a song

  Scenario: Create a song
    When user sends a POST request to create song
      | id | name     | artist   | album | duration | year |
      | 1  | The song | John Doe | Songs | 12:34    | 2020 |
    Then the song creation response code is 200
    And the song creation content type is "application/json"
    And the song creation response is
      """
      {
        "id": 1
      }
      """
    Then the songs are saved to the database
      | id | name     | artist   | album | duration | year |
      | 1  | The song | John Doe | Songs | 12:34    | 2020 |

  Scenario: Get a song
    When user sends a GET request to get song by id=1
    Then the song retrieval response code is 200
    And the song retrieval response content type is "application/json"
    And the song retrieval response is
      """
      {
        "id": 1,
        "name": "The song",
        "artist": "John Doe",
        "album": "Songs",
        "duration": "12:34",
        "year": 2020
      }
      """

  Scenario: Delete a song
    When user sends a DELETE request to delete song by id=1
    Then the song deletion response code is 200
    And the song deletion response content type is "application/json"
    And the song deleting response is
      """
      {
        "ids": [1]
      }
      """

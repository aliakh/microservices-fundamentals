Feature: song service possibilities

  Scenario: Create song
    When song's data saved
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

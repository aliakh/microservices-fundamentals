Feature: song service possibilities

  Scenario: save song's data
    When song's data saved
      | name                 | artist  | album             | length | year | resourceId |
      | We are the champions | Queen   | News of the world | 02:59  | 1977 | 1          |
    Then POST response code is 200
Feature: userLogin

  Scenario Outline: User log in with correct username and password
    Given user <username> is logging to RSS
    When user enter <password>
    Then login succeeds

    Examples: Users
      | username | password |
      | "hyi25"  | "abc123" |


  Scenario Outline: User log in with incorrect username or password
    Given user <username> is logging to RSS
    When user enter <password>
    Then login fails

    Examples: Users
      | username | password  |
      | "hyi25"  | "abc1234" |
      | "blah"   | "abc123"  |
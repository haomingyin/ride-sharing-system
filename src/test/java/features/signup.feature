Feature: sign up users

  Scenario Outline: create a user
    Given user <username> with password <password>
    When sign up <email> <fname> <lname> <address> <mphone> <licence> <licenceType> <issue> <expire>
    Then user data should be recorded
    And licence remind date should be one month before expiration date

    Examples:
      | username | password | email                | fname | lname | address | mphone       | licence    | licenceType         | issue        | expire       |
      | "test2"  | "abc123" | "test1@uclive.ac.nz" | "T"   | "est" | "where" | "0204266464" | "DR223501" | "Full for 2 years+" | "2016-10-20" | "2020-10-18" |
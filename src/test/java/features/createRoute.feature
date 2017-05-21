Feature: add a route

  Scenario: add a new route
    Given user "hyi25"
    When user create route with alias "test route"
    Then route will be created

  Scenario: add a stop point into route
    Given user "hyi25"
    When user create route with alias "test route"
    And user search and add "16 vicki street"
    Then stop point will be added into route
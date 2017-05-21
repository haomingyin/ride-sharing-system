Feature: RegisterCar

  Scenario Outline: A user register a new car
    Given <username> input car <plate>, <manufacturer>, <model>, <color>, <year>, <seatNo>, <wof>, <rego> ,<performance>
    When the user register the car
    Then the car should be added into DB
    And wof remind date should be one month before
    And reg remind date should be one month before

    Examples: new car
      | username | plate    | manufacturer | model | color   | year | seatNo | wof          | rego         | performance |
      | "hyi25"  | "ABC123" | "HONDA"      | "FIT" | "WHITE" | 2006 | 4      | "2017-08-10" | "2017-11-05" | 8           |


  Scenario Outline: A user register a existed car
    Given <username> input car <plate>, <manufacturer>, <model>, <color>, <year>, <seatNo>, <wof>, <rego> ,<performance>
    When the user register the car
    Then the car should not be added into DB

    Examples: existed car
      | username | plate    | manufacturer | model | color   | year | seatNo | wof          | rego         | performance |
      | "hyi25"  | "JDZ519" | "HONDA"      | "FIT" | "WHITE" | 2006 | 4      | "2017-08-10" | "2017-11-05" | 8           |
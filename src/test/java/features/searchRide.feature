Feature: passengers search rides

  Scenario: passengers can search rides
    When passenger "test1" search rides
    Then passenger can view all active rides

  Scenario: passengers filter search condition
    When passenger "test1" search rides
    And  select "From UC"
    Then only ride from UC will be displayed

  Scenario: user cannot search their own rides
    When passenger "hyi25" search rides
    Then no his rides will be displayes
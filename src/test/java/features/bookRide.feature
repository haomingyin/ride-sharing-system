Feature: passenger book rides
  Scenario: passenger book a ride
    Given passenger "test1"
    When searches a ride "From UC" to "16 vicki street"
    And there are available seats
    Then user can book seat
    And seat no will decrement by one
    And passenger can view the booked ride
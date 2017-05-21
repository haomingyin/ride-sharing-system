Feature: cancel ride and send notifications
  Scenario: passenger cancel a booked ride
    Given passenger "test1" books ride "From UC" to "16 vicki street"
    When passenger cancelled the ride
    Then the driver should receive a notification


Feature: Microservices Integration Test

  Scenario: Full flow from training creation to workload update
    Given all microservices and ActiveMQ are running
    When I create a new training in "Gym-Service" for trainer "John.Doe" with 90 minutes
    And I wait for the message to be processed
    Then I request the summary for "John.Doe" from "Workload-Service"
    And the total duration for current month should be 90 minutes
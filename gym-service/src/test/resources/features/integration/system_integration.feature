@integration
Feature: Microservices Integration Test

  Scenario: Full flow from training creation to workload update
    Given all microservices and ActiveMQ are running
    When I create a new training in "Gym-Service" for trainer "John.Doe" with 90 minutes
    And I wait for the message to be processed
    Then I request the summary for "John.Doe" from "Workload-Service"
    And the total duration for current month should be 90 minutes

  Scenario: Multiple trainings should be aggregated
    Given all microservices and ActiveMQ are running
    When I create trainings for trainer "John.Doe" with durations 30, 60
    And I wait for the message to be processed
    Then the total duration for "John.Doe" should be 90 minutes

  Scenario: Trainings across different days are aggregated in same month
    Given all microservices and ActiveMQ are running
    When I create a training for "John.Doe" with 40 minutes today
    And I create a training for "John.Doe" with 20 minutes today
    And I wait for the message to be processed
    Then the total duration for "John.Doe" should be 60 minutes

  Scenario: Different trainers have isolated workloads
    Given all microservices and ActiveMQ are running
    When I create a training for "John.Doe" with 40 minutes
    And I create a training for "Jane.Doe" with 50 minutes
    And I wait for the message to be processed
    Then the total duration for "John.Doe" should be 40 minutes
    And the total duration for "Jane.Doe" should be 50 minutes

  Scenario: Training for non-existent trainer should fail
    Given all microservices and ActiveMQ are running
    When I create a training for "Ghost.User" with 60 minutes
    Then the response status should be 403

  Scenario: Invalid training duration should not be processed
    Given all microservices and ActiveMQ are running
    When I create a training for "John.Doe" with -10 minutes
    Then the response status should be 403

  Scenario: System processes messages asynchronously
    Given all microservices and ActiveMQ are running
    When I create a training for "John.Doe" with 25 minutes
    Then eventually the total duration for "John.Doe" should be 25 minutes

  Scenario: Workload matches gym-service data
    Given all microservices and ActiveMQ are running
    When I create trainings for trainer "John.Doe" with durations 10, 20, 30
    And I wait for the message to be processed
    Then workload-service total should match gym-service data

  Scenario: System handles multiple training events
    Given all microservices and ActiveMQ are running
    When I create 5 trainings for "John.Doe" each with 10 minutes
    And I wait for the message to be processed
    Then the total duration for "John.Doe" should be 50 minutes
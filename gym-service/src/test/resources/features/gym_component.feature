Feature: Gym Service Component Test

  Scenario: [Positive] Create training and trigger workload update
    Given the user is authenticated
    When I send a POST request to "/api/trainings" with name "Yoga" and duration 60
    Then the training should be saved in the database
    And a message should be sent to the "trainer-workload-queue"
@component @training
Feature: Gym Service Component Test

  @positive
  Scenario: Create training successfully
    Given the user is authenticated
    When I send a POST request to "/api/trainings" with valid data
    Then the response status should be 201
    And the training should be saved in the database
    And a message should be sent to the "trainer-workload-queue"

  @negative
  Scenario: Create training with invalid data
    Given the user is authenticated
    When I send a POST request to "/api/trainings" with invalid data
    Then the response status should be 400

  @negative @security
  Scenario: Create training without authentication
    When I send a POST request to "/api/trainings" without token
    Then the response status should be 403

  @negative
  Scenario: Create training with non-existent trainee
    Given the user is authenticated
    When I send a POST request to "/api/trainings" with non-existent trainee
    Then the response status should be 404

  @negative
  Scenario: Create training with non-existent trainer
    Given the user is authenticated
    When I send a POST request to "/api/trainings" with non-existent trainer
    Then the response status should be 404

  @positive
  Scenario: Get training types
    Given the user is authenticated
    When I send a GET request to "/api/trainings/types"
    Then the response status should be 200
    And the response should contain training types


  @positive
  Scenario: Create trainee successfully
    Given the user is not authenticated
    When I send a POST request to "/api/trainees" with valid data
    Then the response status should be 201
    And the trainee should be saved in the database

  @negative
  Scenario: Create trainee with invalid data
    Given the user is not authenticated
    When I send a POST request to "/api/trainees" with invalid data
    Then the response status should be 400

  @positive
  Scenario: Get trainee profile
    Given the user is authenticated
    And trainee "Alice.Smith" exists
    When I send a GET request to "/api/trainees/Alice.Smith"
    Then the response status should be 200

  @negative
  Scenario: Get trainee profile not found
    Given the user is authenticated
    When I send a GET request to "/api/trainees/Unknown.User"
    Then the response status should be 404

  @positive
  Scenario: Update trainee profile
    Given the user is authenticated
    And trainee "Alice.Smith" exists
    When I send a PUT request to "/api/trainees/Alice.Smith" with valid data
    Then the response status should be 200
    And the trainee should be updated in the database

  @positive
  Scenario: Delete trainee
    Given the user is authenticated
    And trainee "Alice.Smith" exists
    When I send a DELETE request to "/api/trainees/Alice.Smith"
    Then the response status should be 200
    And the trainee should be removed from the database

  @positive
  Scenario: Change trainee status
    Given the user is authenticated
    And trainee "Alice.Smith" exists
    When I send a PATCH request to "/api/trainees/Alice.Smith?isActive=false"
    Then the response status should be 200

  @positive
  Scenario: Get trainee trainings list
    Given the user is authenticated
    And trainee "Alice.Smith" exists
    When I send a GET request to "/api/trainees/Alice.Smith/trainings"
    Then the response status should be 200

  @positive
  Scenario: Update trainee trainers list
    Given the user is authenticated
    And trainee "Alice.Smith" exists
    When I send a PUT request to "/api/trainees/Alice.Smith/trainers" with trainer list
    Then the response status should be 200

  @positive
  Scenario: Create trainer successfully
    Given the user is not authenticated
    When I send a POST request to "/api/trainers" with valid data
    Then the response status should be 201
    And the trainer should be saved in the database

  @positive
  Scenario: Get trainer profile
    Given the user is authenticated
    And trainer "John.Doe" exists
    When I send a GET request to "/api/trainers/John.Doe"
    Then the response status should be 200

  @positive
  Scenario: Update trainer profile
    Given the user is authenticated
    And trainer "John.Doe" exists
    When I send a PUT request to "/api/trainers/John.Doe" with valid data
    Then the response status should be 200

  @positive
  Scenario: Change trainer status
    Given the user is authenticated
    And trainer "John.Doe" exists
    When I send a PATCH request to "/api/trainers/John.Doe?isActive=false"
    Then the response status should be 200

  @positive
  Scenario: Get trainer trainings list
    Given the user is authenticated
    And trainer "John.Doe" exists
    When I send a GET request to "/api/trainers/John.Doe/trainings"
    Then the response status should be 200

  @negative
  Scenario: Get trainer profile not found
    Given the user is authenticated
    When I send a GET request to "/api/trainers/Unknown.User"
    Then the response status should be 404

  @negative @security
  Scenario: Access trainer endpoint without authentication
    When I send a GET request to "/api/trainers/John.Doe" without token
    Then the response status should be 403
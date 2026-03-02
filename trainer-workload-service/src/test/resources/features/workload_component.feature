Feature: Trainer Workload Component Test

  Scenario: [Positive] Successfully add training duration
    Given a trainer "John.Doe" exists in MongoDB with 100 minutes in "MARCH" 2026
    When the service processes an "ADD" request for "John.Doe" with 60 minutes for date "2026-03-01"
    Then the MongoDB summary for "John.Doe" should have 160 minutes in "MARCH" 2026

  Scenario: [Negative] Training duration should not be negative after delete
    Given a trainer "John.Doe" exists in MongoDB with 10 minutes in "MARCH" 2026
    When the service processes a "DELETE" request for "John.Doe" with 50 minutes for date "2026-03-01"
    Then the MongoDB summary for "John.Doe" should have 0 minutes in "MARCH" 2026
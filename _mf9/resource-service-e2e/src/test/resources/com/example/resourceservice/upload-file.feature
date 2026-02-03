Feature: Uploaded file should be stored on s3
    One file is uploaded to the service, it should be stored on s3 and link to the file should be stored in the internal DB

    Scenario: Uploading new file
        Given new mp3 file
        When I upload file to the service
        Then I receive ID of new uploaded file
        And I can get file metadata by the given ID
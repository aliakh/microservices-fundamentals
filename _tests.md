Test plan for the project is the following:

1. Unit tests coverage 80%
   - example: ResourceServiceTest
2. Integration tests
   - API tests using MockMVC: covering all endpoints
     - example: ResourcesControllerIT
   - Database integration tests using TestContainers: covering all repositories
3. Component tests: covering main business scenarios of each microservice
   - in our case resource-processor.process and resrouce-service.upload functionality could be covered. 
      - example:
4. Contract tests: this kind of tests overlaps with API integration tests. So one of them could be kept.
5. End-to-end test: covering interaction of all microservices end-to-end.
   - happy-path of uploading new file.
     - example: e2e folder

../mvnw clean test
../mvnw surefire-report:report-only
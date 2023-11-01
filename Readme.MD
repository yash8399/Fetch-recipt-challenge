- first step would be to clone the project in your IDE(Intellij preferred)
- Second step would be to setup JAVA 17 and MAVEN in your IDE. 
- Third Step would be to Clean and install the in the MAVEN settings. on the right hand side. 
- Fourth step would be to build the project. Once the application starts running on your local host Open Localhost
- Try this curl command for a test case on the terminal written below
- curl -X POST http://localhost:8080/receipts/process -H "Content-Type: application/json" -d '{
  "retailer": "Target",
  "purchaseDate": "2022-01-01",
  "purchaseTime": "13:01",
  "items": [
  {
  "shortDescription": "Mountain Dew 12PK",
  "price": "6.49"
  }
  ],
  "total": "6.49"
  }'
- it should generate an id , copy that and replace {id} with the copied value, and open that link in google chrome. It should return the points. 
http://localhost:8080/receipts/{id}/points
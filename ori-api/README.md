# REST API 

The swagger documentation is automatically generated and served at http://localhost:8080/swagger. 
That said, it is recommended to use the Swagger-ui (http://localhost:8080/q/swagger-ui) for 
a better experience when interacting with the REST API.

## Endpoints

### Account resources

| Endpoint | Http method | 
| --- | --- | 
| /account/{*tokenSymbol*} | GET | 
| /account/{*tokenSymbol*} | POST | 
| /account/{*tokenSymbol*}/hash/{*hash*} | GET | 
| /account/{*tokenSymbol*}/hash/{*hash*} | DELETE |

### Block resources

| Endpoint | Http method | 
| --- | --- | 
| /block/{*tokenSymbol*} | GET | 
| /block/{*tokenSymbol*} | POST |
| /block/{*tokenSymbol*}/hash/{*hash*} | GET | 
| /block/{*tokenSymbol*}/hash/{*hash*} | DELETE |
| /block/{*tokenSymbol*}/last | GET | 
| /block/{*tokenSymbol*}/multiple | POST | 


### Token resources

| Endpoint | Http method | 
| --- | --- | 
| /token | GET | 
| /token | POST |
| /token/{*tokenSymbol*} | GET | 
| /token/{*tokenSymbol*} | DELETE |

### Transaction resources

| Endpoint | Http method |
| --- | --- | 
| /transaction/{*tokenSymbol*} | GET |
| /transaction/{*tokenSymbol*} | POST | 
| /transaction/{*tokenSymbol*}/account/{*account*} | GET | 
| /transaction/{*tokenSymbol*}/hash/{*transactionHash*} | GET | 
| /transaction/{*tokenSymbol*}/hash/{*transactionHash*} | DELETE |
| /transaction/{*tokenSymbol*}/incoming/account/{*account*} | GET |
| /transaction/{*tokenSymbol*}/outgoing/account/{*account*} | GET | 
| /transaction/{*tokenSymbol*}/multiple | POST | 

### Transaction resources

| Endpoint | Http method | 
| --- | --- | 
| /monitor/{*tokenSymbol*}/score/{account} | GET | 
| /monitor/{*tokenSymbol*}/traceCoin/back/{account} | GET | 
| /monitor/{*tokenSymbol*}/traceCoin/forward/{account} | GET | 
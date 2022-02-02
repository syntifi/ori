# REST API 

The swagger documentation is automatically generated and served at http://localhost:8080/swagger. 
That said, it is recommended to use the Swagger-ui (http://localhost:8080/q/swagger-ui) for 
a better experience when interacting with the REST API.

## Endpoints

### Block resources

| Endpoint | Http method | Description | Path Parameters | Path parameters description | Query Parameters | Query parameters description |
| --- | --- | --- | --- | --- | --- | --- |
| /block | GET | Get all blocks in descending chronological order | - | - | - | - |
| /block | POST | Add and index a new block in the database | - | - | - | -| 
| /block | DELETE | Remove all blocks stored in the database | - | - | - | - |
| /block/{hash} | GET | Get a block given the hash | *hash*: String | Block hash in Hex | - | - | 
| /block/{hash} | DELETE | Remove a block given a hash | *hash*: String | Block hash in Hex | - | - | 

### Transaction resources

| Endpoint | Http method | Description | Path Parameters | Path parameters description | Query Parameters | Query parameters description |
| --- | --- | --- | --- | --- | --- | --- |
| /transaction | GET | Get all transactions in descending chronological order | - | - | *fromAccount*: String / *toAccount*: String | Optional parameters to filter transactions outgoing *fromAccount* or incomig *toAccount*  |
| /transaction | POST | Add and index a new transaction in the database | - | - | - | -| 
| /transaction | DELETE | Remove all transactions stored in the database | - | - | - | - |
| /transaction/{hash} | GET | Get a transaction given the hash | *hash*: String | Transaction hash in Hex | - | - | 
| /transaction/{hash} | DELETE | Remove a transaction given a hash | *hash*: String | Transaction hash in Hex | - | - | 
| /transaction/{tokenSymbol}/account/{account} | GET | Get all incoming and outgoing transaction for an account | *account*: String | Account hash in Hex preceded by *account-hash* string | - | - | 

### Transaction monitor resources
| Endpoint | Http method | Description | Path Parameters | Path parameters description | Query Parameters | Query parameters description |
| --- | --- | --- | --- | --- | --- | --- |
| /score/{account} | GET | Calculate and return AML scores for a given account  | *account*: String | Account hash in Hex preceded by *account-hash* string | *date*: date format yyyy-MM-dd'T'HH:mm:ss.SSS | Optional date parameter specifying an asof date for the analysis |
| /traceCoin/back/{account} | GET | Trace back the coin origin before reaching the account | *account*: String | Account hash in Hex preceded by *account-hash* string | *fromDate*/*toDate*: date format yyyy-MM-dd'T'HH:mm:ss.SSS | Optional date parameters specifying the time period to run the analysis |
| /traceCoin/forward/{account} | GET | Trace forward the coin destination after leaving the account | *account*: String | Account hash in Hex preceded by *account-hash* string | *fromDate*/*toDate*: date format yyyy-MM-dd'T'HH:mm:ss.SSS | Optional date parameters specifying the time period to run the analysis |

## Examples

### Block resources

| Endpoint | Http method | Curl example |
| --- | --- | --- |
| /block/{tokenSymbol} | GET | ```curl 'http://localhost:8080/block/[tokenSymbol]' -H 'accept: application/json'``` |
| /block/{tokenSymbol} | POST | ```curl -X 'POST' 'http://localhost:8080/block/[tokenSymbol]' -H 'accept: */*' -H 'Content-Type: application/json' -d '{ "hash": "string", "timeStamp": "2022-02-02T16:57:57.454Z", "height": 0, "era": 0, "root": "string", "validator": "string", "parent": "string", "tokenSymbol": "string" }' ``` |
| /block/{tokenSymbol} | DELETE | ```curl -X 'DELETE' 'http://localhost:8080/block/[tokenSymbol]' -H 'accept: */*' ```| 
| /block/{tokenSymbol}/hash/{hash} | GET | ```curl -X 'GET' 'http://localhost:8080/block/[tokenSymbol]/hash/[hash]' -H 'accept: application/json'``` |
| /block/{tokenSymbol}/hash/{hash} | DELETE | ``` curl -X 'DELETE' 'http://localhost:8080/[tokenSymbol]/hash/[hash]' -H 'accept: */*' ```|

### Transaction resources

| Endpoint | Http method | Curl example |
| --- | --- | --- |
| /transaction/{tokenSymbol} | GET | ```curl -X 'GET' 'http://localhost:8080/transaction/<tokenSymbol>' -H 'accept: application/json' ``` |
| /transaction/{tokenSymbol} | POST | ``` curl -X 'POST' 'http://localhost:8080/transaction/<tokenSymbol>' -H 'accept: */*' -H 'Content-Type: application/json'  -d '{ "hash": "string", "timeStamp": "2022-02-02T16:58:56.218Z", "amount": 0, "fromHash": "string", "toHash": "string", "blockHash": "string", "tokenSymbol": "string" }' ```|
| /transaction/{tokenSymbol} | DELETE | ```curl -X 'DELETE' 'http://localhost:8080/transaction/<tokenSymbol>' -H 'accept: */*' ```| 
| /transaction/{tokenSymbol}/hash/{hash} | GET | ```curl -X 'GET' 'http://localhost:8080/transaction/<tokenSymbol>/hash/<HERE>' -H 'accept: application/json'``` |
| /transaction/{tokenSymbol}/hash/{hash} | DELETE | ``` curl -X 'DELETE' 'http://localhost:8080/transaction/<tokenSymbol>/hash/string' -H 'accept: */*' ```|
| /transaction/{tokenSymbol}/account/{account} | GET | ```curl -X 'GET' 'http://localhost:8080/transaction/<tokenSymbol>/account/<account>' -H 'accept: application/json'```  |

### Transaction monitor resources

| Endpoint | Http method | Curl example| 
| --- | --- | --- | 
| /score/{account} | GET | ```curl -X 'GET' 'http://localhost:8080/score/<HERE>' -H 'accept: application/json'```| 
| /traceCoin/back/{account} | GET |  ```curl -X 'GET' 'http://localhost:8080/traceCoin/back/<HERE>' -H 'accept: application/json' ```|
| /traceCoin/forward/{account} | GET |  ```curl -X 'GET' 'http://localhost:8080/traceCoin/forward/<HERE>' -H 'accept: application/json' ```|

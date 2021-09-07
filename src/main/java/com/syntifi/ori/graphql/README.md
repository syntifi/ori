# GraphQL API 

The GraphQL schema is automatically generated and served at http://localhost:8080/graphql/schema.graphql. 
That said, it is recommended to use the GraphQL-UI (http://localhost:8080/q/graphql-ui) for 
a better experience when interacting with the API.

## Queries/Mutations

| Query| Type | Description | Parameters | Parameters description |
| --- | --- | --- | --- | --- | 
| account | Query | Return all transactions for a given account | *account*: String | Account hash in Hex preceded by *account-hash* string |
| transaction | Query | Get a transaction given its hash | *hash*: String | Transaction hash in Hex |
| transaction | Query | Return the transaction from (optional), to (optional) an account. Return all transactions if neither from nor to is specified | *from*/*to*: String | Account hash in Hex preceded by *account-hash* string |
| pauseCasperUpdateJob | Query | Pause Casper update job running repeatedly | - | - | 
| startCasperCrawlJob | Query | Start Casper batch job running in smaller parts | - | - | 
| startCasperUpdateJob | Query | Start Casper update job running repeatedly | - | - |
| stopCasperCrawlJob | Query | Pause Casper batch job | - | - | 
| casperBlockByHash | Query | Get Casper block by Hash | *blockHash*: String | Block hash in Hex | 
| casperBlockByHeight | Query | Get Casper block by height | *blockHeight*: BigInteger | Block height |
| casperNodes | Query | Get Casper node | - | - |
| casperTransfers | Query | Get block transfers | *blockHash*: String  | Block hash in Hex |
| addTransaction | Mutation | Add a new transaction | *transaction*: Transaction | Transaction json |
| deleteTransaction | Mutation | Remove a transaction | *hash* : String | Transaction hash in Hex |

## Examples
| Query | Curl request |
| --- | --- |
| account |  | 
| transaction |  |
| transaction |  |
| pauseCasperUpdateJob | ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query {pauseCasperCrawlJob}"}' http://localhost:8080/graphql``` |
| startCasperCrawlJob | ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query {startCasperCrawlJob}"}' http://localhost:8080/graphql``` | 
| startCasperUpdateJob | ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query {startCasperUpdateJob}"}' http://localhost:8080/graphql``` |
| stopCasperCrawlJob | ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query {stopCasperCrawlJob}"}' http://localhost:8080/graphql``` |
| casperBlockByHash |  |
| casperBlockByHeight |  |
| casperNodes |  |
| casperTransfers |  |
| addTransaction |  |
| deleteTransaction |  |

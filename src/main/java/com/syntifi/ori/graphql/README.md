# GraphQL API 

The GraphQL schema is automatically generated and served at http://localhost:8080/graphql/schema.graphql. 
That said, it is recommended to use the GraphQL-UI (http://localhost:8080/q/graphql-ui) for 
a better experience when interacting with the API.

## Queries/Mutations

| Query| Type | Description | Parameters | Parameters description |
| --- | --- | --- | --- | --- | 
| account | Query | Return all transactions for a given account | *account*: String | Account hash in Hex preceded by *account-hash* string |
| transaction | Query | Get a transaction given its hash | *hash*: String | Transaction hash in Hex |
| transactions | Query | Return all transactions from (optional), to (optional) an account. Return all transactions if neither from nor to is specified | *from*/*to*: String | Account hash in Hex preceded by *account-hash* string |
| addTransaction | Mutation | Add a new transaction | *transaction*: Transaction | Transaction json |
| pauseCasperUpdateJob | Query | Pause Casper update job running repeatedly | - | - | 
| startCasperCrawlJob | Query | Start Casper batch job running in smaller parts | - | - | 
| startCasperUpdateJob | Query | Start Casper update job running repeatedly | - | - |
| stopCasperCrawlJob | Query | Pause Casper batch job | - | - | 
| casperBlockByHash | Query | Get Casper block by Hash | *blockHash*: String | Block hash in Hex | 
| casperBlockByHeight | Query | Get Casper block by height | *blockHeight*: BigInteger | Block height |
| casperNodes | Query | Get Casper node | - | - |
| casperTransfers | Query | Get block transfers | *blockHash*: String  | Block hash in Hex |

## Examples
| Query | Curl request |
| --- | --- |
| account | ```curl -g -X POST -H "Content-Type: application/json" -d '{"query": "query{account (account:\"<HERE>\") {from to timeStamp blockHash amount hash}}"}' http://localhost:8080/graphql``` | 
| transaction |  ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query{ transaction (hash:\"<HERE>\") {from to amount }}"}' http://localhost:8080/graphql``` |
| transactions |  ```curl -g -X POST -H "Content-Type: application/json" -d '{"query": "query{transactions(from: \"<HERE>\" to:\"<HERE>\") { from to amount timeStamp} }"}' http://localhost:8080/graphql``` |
| addTransaction |  ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"mutation {addTransaction(transaction: {timeStamp: \"2099-01-01T00:00:00.000\", blockHash: \"string\", amount: 0, from: \"string\", to: \"string\", hash: \"string"}) {from to }}"}' http://localhost:8080/graphql``` |
| pauseCasperUpdateJob | ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query {pauseCasperCrawlJob}"}' http://localhost:8080/graphql``` |
| startCasperCrawlJob | ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query {startCasperCrawlJob}"}' http://localhost:8080/graphql``` | 
| startCasperUpdateJob | ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query {startCasperUpdateJob}"}' http://localhost:8080/graphql``` |
| stopCasperCrawlJob | ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query {stopCasperCrawlJob}"}' http://localhost:8080/graphql``` |
| casperBlockByHash | ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query{casperBlockByHash (blockHash:\"3bf2647edb5743ef449c3523b37c0124f61093222d0efcfeeca0a70255aec038\") {hash proofs { signature   publicKey } header { timeStamp  bodyHash  accumulatedSeed protocolVersion parentHash  stateRootHash } body {  proposer }}}"}' http://localhost:8080/graphql``` |
| casperBlockByHeight | ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query{casperBlockByHeight (blockHeight:0) {hash proofs { signature   publicKey } header { timeStamp  bodyHash  accumulatedSeed protocolVersion parentHash  stateRootHash } body {  proposer }}}"}' http://localhost:8080/graphql``` |
| casperNodes |  ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query {casperNodes {address nodeId} }"}' http://localhost:8080/graphql``` |
| casperTransfers |  ```curl -g -X POST -H "Content-Type: application/json"  -d '{"query":"query {casperTransfers (blockHash: \"3bf2647edb5743ef449c3523b37c0124f61093222d0efcfeeca0a70255aec038\") { amount gas from id source to deployHash target }}"}' http://localhost:8080/graphql``` |

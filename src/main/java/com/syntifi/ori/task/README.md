# Casper

## Crawl

Call ```{startCasperCrawlJob}``` using the GraphQL API to add the crawler job to the quartz schedule. This kicks off a quartz job that runs every ```CASPER_CRAWL_FREQ``` seconds. The job stablishes ```ORI_CASPER_THREADS``` concurrent http connections to the nodes listed at```ORI_CASPER_NODE_LIST```. It also makes use of our own ```casper-sdk``` to make the RPC calls using the port ```ORI_CASPER_PORT```. The request will timeout in ```ORI_CASPER_TIMEOUT``` seconds.

The crawl job checks the last block height stored in the system and asks the nodes for the next one.

To remove the job from the schedule, use the ```{stopCasperCrawlJob}``` method.

## Update

Similar to the crawl job, call ```{startCasperUpdateJob}``` to using the GraphQL API to add the updater job to the quartz schedule. This job runs every ```CASPER_UPDATE_FREQ``` and as opposed to the crawl job, the update job just asks for the last block and sends it to the elastic search database.

To remove the job from the schedule, use the ```{stopCasperUpdateJob}``` method.

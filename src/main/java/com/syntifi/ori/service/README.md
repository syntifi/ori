# Risk metrics

## Trace the coin
To check the origin of the money, it is is possible to trace back the coins to a given account prior to the given date. The back trace feature returns the graph with all accounts that are either directly or indirectly linked to the given account. The maximum number of nodes in the graph is limited by ```ORI_AML_MAX_TRACE_LENGTH```.

It is also possible to forward trace the coin. The forward trade returns the graph with all accounts that are either directly or indirectly linked to the given account through transactions that happened after the given date.

## AML scores
All scores are normalized: they are double numbers taking value between 0 and 1. The higher the worst.

### Structuring over time
This score calculates the proportion of transactions falling in the interval between 90% and the reporting threshold ```ORI_AML_REPORTING_THRESHOLD``` during the period between ```ORI_AML_LONG_WINDOW_DAYS``` days before the date specified in the request and the specified date.

### Unusual outgoing volume
This score calculates the percentual increase in outgoing transactions over a short window moving average with window size ```ORI_AML_SHORT_WINDOW```. It is meant to spot an unusual increase in outgoing transactions over the moving average window. The score is set to 0.0 if the number of outgoing transactions is low.

### Unusual behavior score
This score calculates how much does the last moving average transaction value (with window size ```ORI_AML_SHORT_WINDOW``` deviates from the average value of transactions.

### Flow through
This score calculates the ratio of total outgoing value and total incoming value over the window ```ORI_AML_SHORT_WINDOW``` This is supposed to spot accounts used to increase the difficulty of tracing a coin.
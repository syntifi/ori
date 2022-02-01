import React, { ReactElement, FC, useState } from "react";
import VisGraph from './VisGraph';
import {
    Box,
    Card,
    CardContent,
    CardHeader,
    Grid,
    Paper,
    FormControlLabel,
    Switch
} from "@material-ui/core";
import AccountInfoForm from "./AccountInfoForm";
import axios from "axios";

/**
 * Component that renders the AccounInfo form, queries the backend for the data
 * and displays the result in the Visgraph for the end user
 *  
 * @returns AccountInfoForm and Graph
 */
const AccountTrace: FC<any> = ({ date, submit, direction }): ReactElement => {

    const [graph, setGraph] = useState<any>({
        nodes: [],
        edges: []
    })

    const [hierarchical, setHierarchical] = useState<boolean>(false)

    const loadData = (values: any) => {
        const params = { [direction === "forward" ? "fromDate" : "toDate"]: values.dateTime.toISOString().replace('Z', '') }
        axios({
            method: "GET",
            url: `${process.env.REACT_APP_API_URL}/traceCoin/` + direction + '/' + values.account,
            params: params
        }).then(response => {
            const uniqueIds: any[] = [];
            const accountIdMap: any = {};
            var i = 0;
            response.data.forEach((item: any) => {
                if (uniqueIds.indexOf(item.to) === -1) {
                    uniqueIds.push(item.to)
                    accountIdMap[item.to] = i
                    i = i + 1
                }
                if (uniqueIds.indexOf(item.from) === -1) {
                    uniqueIds.push(item.from)
                    accountIdMap[item.from] = i
                    i = i + 1
                }
                
            })
            const newEdges: any[] = response.data.map((item: any) =>
            ({
                from: accountIdMap[item.from],
                to: accountIdMap[item.to],
                value: item.amount,
                title: item.amount / 1000000
            }))
            const newNodes: any[] = uniqueIds.map((val: string) =>
            ({
                id: accountIdMap[val],
                color: val === values.account ? "#e53e3e" : "#fff",
                title: val
            }))
            console.log("============")
            console.log({ nodes: newNodes, edges: newEdges })
            console.log("============")
            setGraph({ nodes: newNodes, edges: newEdges })
        }).catch((e) => {
            console.log("Error when retrieving data from backend application")
            console.log(e.message);
            setGraph({
                nodes: [],
                edges: []
            })
        })
    }

    return (
        <Grid container spacing={2}>
            <Grid item xs={2} md={1} />
            <Grid item xs={2} md={2}>
                <Box>
                    <Card variant="outlined">
                        <CardHeader title="Parameters" />
                        <CardContent>
                            <AccountInfoForm
                                date={date}
                                submit={submit}
                                onSubmit={loadData}
                                showHierarchicalSwitch={true} />
                            <FormControlLabel
                                control={
                                    <Switch
                                        checked={hierarchical}
                                        onChange={() => setHierarchical(!hierarchical)}
                                        name="hierarchical"
                                        value="hierarchical"
                                        color="primary"
                                    />
                                }
                                labelPlacement="end"
                                label="Hierarchical"
                            />
                        </CardContent>
                    </Card>
                </Box>
            </Grid>
            <Grid item xs={6} md={8}>
                <Box
                    sx={{
                        display: 'flex',
                        flexWrap: 'wrap',
                        height: "800px",
                    }}
                >
                    <Paper>
                        <VisGraph graph={graph} hierarchical={hierarchical} height="800px" />
                    </Paper>
                </Box >
            </Grid>
            <Grid item xs={2} md={1} />
        </Grid>
    );
}

export default AccountTrace;
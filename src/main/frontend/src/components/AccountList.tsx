import React, { ReactElement, FC, useState } from "react";
import { Box, Grid, Card, CardHeader, CardContent } from "@material-ui/core";
import AccountInfoForm from "./AccountInfoForm";
import { DataGrid } from '@material-ui/data-grid';
import axios from "axios";

/**
 * Component that renders the AccounInfo form, queries the backend for the data
 * and displays the result in table format to the user 
 *  
 * @returns AccountInfoForm and Table
 */
const AccountList: FC<any> = ({ date, submit }): ReactElement => {
    const cols = [
        { field: 'dateTime', headerName: 'DateTime', width: 200 },
        { field: 'id', headerName: 'Hash', width: 300 },
        { field: 'from', headerName: 'From', width: 300 },
        { field: 'to', headerName: 'To', width: 300 },
        { field: 'amount', headerName: 'Amount', width: 200 }
    ]

    const [rows, setRows] = useState([])

    const loadData = (values: any) => {
        axios({
            method: "GET",
            url: `${process.env.REACT_APP_API_URL}/transaction/account/` + values.account,
        }).then(response => {
            console.log(response)
            setRows(response.data.map((item: any) =>
            ({
                dateTime: item.timeStamp,
                from: item.from,
                to: item.to,
                amount: item.amount,
                id: item.hash
            })))
        }).catch(e => {
            console.log("Error when retrieving data from backend application")
            console.log(e.message)
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
                        height: "650px",
                    }}
                >
                    <DataGrid
                        rows={rows}
                        columns={cols}
                        pageSize={10}
                    />
                </Box>
            </Grid>
            <Grid item xs={2} md={1} />
        </Grid>
    );
}

export default AccountList;
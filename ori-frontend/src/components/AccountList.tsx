import React, { ReactElement, FC, useState } from "react";
import { Box, Grid, Card, CardHeader, CardContent } from "@mui/material";
import AccountInfoForm from "./AccountInfoForm";
import { DataGrid } from '@mui/x-data-grid';
import axios from "axios";

/**
 * Component that renders the AccounInfo form, queries the backend for the data
 * and displays the result in table format to the user 
 *  
 * @returns AccountInfoForm and Table
 */
const AccountList: FC<any> = ({ date, submit }): ReactElement => {
    const cols = [
        { field: 'timeStamp', headerName: 'Date', width: 200 },
        { field: 'id', headerName: 'Hash', width: 300 },
        { field: 'fromHash', headerName: 'From', width: 300 },
        { field: 'toHash', headerName: 'To', width: 300 },
        { field: 'amount', headerName: 'Amount', width: 200 }
    ]

    const [rows, setRows] = useState([])

    const loadData = (values: any) => {
        axios({
            method: "GET",
            url: `${process.env.REACT_APP_API_URL}/transaction/ETH/account/` + values.account,
        }).then(response => {
            console.log(response)
            setRows(response.data.map((item: any) =>
            ({
                timeStamp: item.timeStamp,
                fromHash: item.fromHash,
                toHash: item.toHash,
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
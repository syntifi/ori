import React from 'react'
import { render, waitFor, cleanup } from '@testing-library/react';
import MockAdapter from 'axios-mock-adapter';
import userEvent from '@testing-library/user-event';
import AccountList from '../components/AccountList';
import { OriAPI } from '../OriAPI';
import { act } from 'react-dom/test-utils';

/**
 * AccountList axios tests
 */
describe('<AccountList />', () => {
    let mock;

    beforeAll(() => {
        mock = new MockAdapter(OriAPI);
        const tokens = [{ symbol: "ABC" }, { symbol: "EFG" }]
        mock.onGet(`${process.env.REACT_APP_API_URL}/token`).reply(200, tokens);
        const mockData =
            [
                {
                    "timeStamp": "2021-09-09T00:48:27.136+0000",
                    "hash": "3f6c30e1137bd7b931bdce7048d416e598ca41aa77e504c66a12563e811e4911",
                    "from": "account-hash-496d542527e1a29f576ab7c3f4c947bfcdc9b4145f75f6ec40e36089432d7351",
                    "to": "account-hash-f6cf6ccfb97baa8dcf3232819c40b8df1961defa28daa115a19f2274527dc628",
                    "amount": 57200152080000,
                    "blockHash": "8148432106294ed069c97bd5ca5dd405e4c10220add718130ac12c8c33b2f033"
                },
                {
                    "timeStamp": "2021-09-09T00:46:16.064+0000",
                    "hash": "2f5e9da07f89cf197c1e7baa0165664f3d49451933af02cf36ab93b5364cfeff",
                    "from": "account-hash-496d542527e1a29f576ab7c3f4c947bfcdc9b4145f75f6ec40e36089432d7351",
                    "to": "account-hash-94664ce59fa2e6eb0cc69d270fc91dd9dd2ba02a1f7964c69f036ef4a68bb96f",
                    "amount": 679338000000,
                    "blockHash": "ec762bdedc44f528e832d49b2b0bfa0f8a5f803b18b960ebde54980fff52bece"
                }]
        mock.onGet(`${process.env.REACT_APP_API_URL}/traceCoin/ABC/forward/1234`).reply(200, mockData);
        mock.onGet(`${process.env.REACT_APP_API_URL}/traceCoin/EFG/forward/1234`).reply(404,
            { 'message': 'Axios fail' })
    })

    it('good backend response', async () => {
        act(async () => {
            const form = render(<AccountList date="dateTime" submit="submit" />);
            const account = form.getByLabelText(/account/i)
            const token = form.getByLabelText(/token/i)
            userEvent.type(account, '1234');
            userEvent.selectOptions(token, 'ABC')
            const submit = form.getAllByRole(/button/i)[0]
            await waitFor(() => {
                userEvent.click(submit)
                expect(form.getByText(/account-hash-496d542527e1a29f576ab7c3f4c947bfcdc9b4145f75f6ec40e36089432d7351/i)).toHaveLength(1)
                expect(form.getByText(/account-hash-a91f350faee0fc17404320180a47394e40b26c567470b9822e9f41a969c4409f/i)).toHaveLength(1)
            })
        })
    })

    it('bad backend response', async () => {
        act(async () => {
            const form = render(<AccountList date="dateTime" submit="submit" />);
            const account = form.getByLabelText(/account/i)
            const token = form.getByLabelText(/token/i)
            userEvent.type(account, '1234');
            userEvent.selectOptions(token, 'ABC')
            const submit = form.getAllByRole(/button/i)[1]
            await waitFor(() => {
                userEvent.click(submit)
            })
            expect(form.getByText(/no rows/i)).toBeDefined()
        })
    })
})
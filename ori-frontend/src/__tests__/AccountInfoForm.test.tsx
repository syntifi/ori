import React from 'react'
import { render, fireEvent, waitFor } from '@testing-library/react';
import MockAdapter from 'axios-mock-adapter';
import userEvent from '@testing-library/user-event';
import AccountInfoForm from '../components/AccountInfoForm';
import { OriAPI } from "../OriAPI";
import { act } from 'react-dom/test-utils';

/**
 * AccountInfo form tests
 */
describe('<AccountInfoForm />', () => {
    let mock;

    beforeAll(() => {
        mock = new MockAdapter(OriAPI);
        const tokens = [{ symbol: "ABC" }, { symbol: "EFG" }];
        mock.onGet(`${process.env.REACT_APP_API_URL}/token`).reply(200, tokens);
    })

    it('should setup up empty account in AccountInfoForm', async () => {
        const handleSubmit = jest.fn()
        const form = render(<AccountInfoForm date="dateTime" submit="submit" onSubmit={handleSubmit} />);
        const account = form.getByLabelText(/account/i)
        expect(account.getAttribute("value")).toBe('')
    })

    it('should render an error, missing account', async () => {
        const handleSubmit = jest.fn()
        const form = render(<AccountInfoForm date="dateTime" submit="submit" onSubmit={handleSubmit} />);
        const submit = form.getAllByRole(/button/i)[1]
        await waitFor(() => {
            userEvent.click(submit)
            expect(form.getByText("Please enter an account hash")).toBeInTheDocument()
        })
    })

    it('should update account correctly in AccountInfoForm', async () => {
        act(async () => {
            const handleSubmit = jest.fn()
            const form = render(<AccountInfoForm date="testdatetime" submit="submit" onSubmit={handleSubmit} />);
            const account = form.getByLabelText(/account/i)
            const token = form.getByTestId(/select/i)
            const dateTime = form.getByLabelText(/testdatetime/i)
            const submit = form.getAllByRole(/button/i)[1]
            const myDate = new Date("2099-12-31 23:59:59.000")
            userEvent.type(account, '1234')
            await waitFor(() =>
                fireEvent.change(dateTime, { target: { value: myDate } }))
            await waitFor(() =>
                fireEvent.change(token, { target: { value: "ABC" } }))
            userEvent.click(submit)
            await waitFor(() =>
                expect(handleSubmit).toHaveBeenCalledWith({
                    account: '1234',
                    token: 'ABC',
                    timeStamp: myDate
                }))
        })
    })
})

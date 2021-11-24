import React from 'react'
import { render, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import AccountInfoForm from '../components/AccountInfoForm';

/**
 * AccountInfo form tests
 */

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
        expect(form.getByText("The account is required")).toBeInTheDocument()
    })
})

it('should update account correctly in AccountInfoForm', async () => {
    const handleSubmit = jest.fn()
    const form = render(<AccountInfoForm date="dateTime" submit="submit" onSubmit={handleSubmit} />);
    const account = form.getByLabelText(/account/i)
    const dateTime = form.getByLabelText(/datetime/i)
    const submit = form.getAllByRole(/button/i)[1]
    const myDate = new Date("2099-12-31 23:59:59.000")
    userEvent.type(account, '1234');
    await waitFor(() =>
        fireEvent.change(dateTime, { target: { value: "2099-12-31 23:59:59.000" } }))
    userEvent.click(submit)
    await waitFor(() =>
        expect(handleSubmit).toHaveBeenCalledWith({
            account: '1234',
            dateTime: myDate
        }))
})


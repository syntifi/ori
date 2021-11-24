import React from 'react'
import { render, waitFor, fireEvent } from '@testing-library/react';
import MockAdapter from 'axios-mock-adapter';
import userEvent from '@testing-library/user-event';
import AccountTrace from '../components/AccountTrace';
import axios from "axios";
import Adapter from '@wojtekmaj/enzyme-adapter-react-17';
import { mount, configure } from 'enzyme';

configure({adapter: new Adapter()});

const crypto = require('crypto');

Object.defineProperty(global.self, 'crypto', {
    value: {
        getRandomValues: (arr: any[]) => crypto.randomBytes(arr.length)
    }
});

/**
 * AccountTrace axios tests
 */
it('good backend response', async () => {
    var mock = new MockAdapter(axios);
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
    mock.onGet(`${process.env.REACT_APP_API_URL}/traceCoin/forward/1234`).reply(200, mockData);
    const form = mount(<AccountTrace date="mydate" submit="mybutton" direction="forward" />);
    expect(document.querySelector("canvas")).toBeDefined()
    form.find("input#account").simulate('change', {target: {value: "1234"}})
    form.find("button").forEach((x) => x.simulate('click'))
    expect(form.render().find("canvas")).toBeDefined()
})

it('bad backend response', async () => {
    var mock = new MockAdapter(axios);
    mock.onGet(`${process.env.REACT_APP_API_URL}/traceCoin/forward/1234`).reply(404,
        { 'message': 'Axios fail' })
    const form = mount(<AccountTrace date="mydate" submit="mybutton" direction="forward" />);
    expect(document.querySelector("canvas")).toBeDefined()
    form.find("input#account").simulate('change', {target: {value: "1234"}})
    form.find("button").forEach((x) => x.simulate('click'))
    expect(form.render().find("canvas")).toBeDefined()
})
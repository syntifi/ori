import axios from 'axios';

export const OriAPI = axios.create({
    baseURL:`${process.env.REACT_APP_API_URL}`
}); 
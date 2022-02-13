import React, { ReactElement, FC } from "react";
import { useFormik, FormikProvider, Form } from 'formik';
import * as yup from 'yup';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import AdapterDateFns from '@mui/lab/AdapterDateFns';
import LocalizationProvider from '@mui/lab/LocalizationProvider';
import DateTimePicker from '@mui/lab/DateTimePicker';
import MenuItem from '@mui/material/MenuItem';
import Select from '@mui/material/Select';
import InputLabel from '@mui/material/InputLabel';
import { FormControl } from "@mui/material";

/**
 * Yup's validation scheme used by formik
 */
const validationSchema = yup.object({
    token: yup
        .string()
        .required('Please select a token'),
    account: yup
        .string()
        .required('Please enter an account hash'),
    timeStamp: yup
        .string()
});

/**
 * Formik form to input account and date.
 *  
 * @param onSubmit function 
 * @returns Formik form
 */
const AccountInfoForm: FC<any> = ({ onSubmit, submit, date }): ReactElement => {
    const formik = useFormik({
        initialValues: {
            token: null,
            account: '',
            timeStamp: new Date(),
        },
        validationSchema: validationSchema,
        onSubmit: (values) => {
            onSubmit(values);
        },
    });

    return (
        <FormikProvider value={formik}>
            <Form onSubmit={formik.handleSubmit}>
                <FormControl variant="standard" fullWidth>
                    <InputLabel id="token">Token</InputLabel>
                    <Select
                        id="token"
                        labelId="token"
                        value={formik.values.token}
                        onChange={formik.handleChange}
                    >
                        <MenuItem value={10}>Ten</MenuItem>
                        <MenuItem value={20}>Twenty</MenuItem>
                        <MenuItem value={30}>Thirty</MenuItem>
                    </Select>
                </FormControl>

                <TextField
                    id="account"
                    name="account"
                    label="Account"
                    value={formik.values.account}
                    onChange={formik.handleChange}
                    error={formik.touched.account && Boolean(formik.errors.account)}
                    helperText={formik.touched.account && formik.errors.account}
                    margin="dense"
                    variant="standard"
                    fullWidth
                />

                <LocalizationProvider dateAdapter={AdapterDateFns}>
                    <DateTimePicker
                        renderInput={(props) => <TextField
                            variant="standard"
                            margin="dense"
                            helperText={formik.touched.timeStamp && formik.errors.timeStamp}
                            error={formik.touched.timeStamp && Boolean(formik.errors.timeStamp)}
                            fullWidth
                            {...props} />}
                        label={date}
                        value={formik.values.timeStamp}
                        inputFormat="yyyy-MM-dd HH:mm:ss"
                        onChange={(dt) => {
                            formik.setFieldValue("timeStamp", dt);
                        }}
                    />
                </LocalizationProvider>
                <div> </div>
                <Button color="primary" variant="contained" fullWidth type="submit">
                    {submit}
                </Button>
            </Form>
        </FormikProvider>
    );
}

export default AccountInfoForm;
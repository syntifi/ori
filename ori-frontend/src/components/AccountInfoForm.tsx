import React, { ReactElement, FC } from "react";
import { useFormik, FormikProvider, Form } from 'formik';
import * as yup from 'yup';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import DateFnsUtils from '@date-io/date-fns';
import { MuiPickersUtilsProvider, KeyboardDateTimePicker } from "@material-ui/pickers";

/**
 * Yup's validation scheme used by formik
 */
const validationSchema = yup.object({
    account: yup
        .string()
        .required('The account is required'),
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
                <TextField
                    id="account"
                    name="account"
                    label="Account"
                    value={formik.values.account}
                    onChange={formik.handleChange}
                    error={formik.touched.account && Boolean(formik.errors.account)}
                    helperText={formik.touched.account && formik.errors.account}
                    margin="dense"
                    fullWidth
                />
                <MuiPickersUtilsProvider utils={DateFnsUtils}>
                    <KeyboardDateTimePicker
                        id="timeStamp"
                        name="timeStamp"
                        label={date}
                        value={formik.values.timeStamp}
                        inputVariant="standard"
                        format="yyyy-MM-dd HH:mm:ss"
                        onChange={(date) => {
                            formik.setFieldValue("timeStamp", date);
                        }}
                        error={formik.touched.timeStamp && Boolean(formik.errors.timeStamp)}
                        helperText={formik.touched.timeStamp && formik.errors.timeStamp}
                        margin="dense"
                        fullWidth
                    />
                </MuiPickersUtilsProvider>
                <div> </div>
                <Button color="primary" variant="contained" fullWidth type="submit">
                    {submit}
                </Button>
            </Form>
        </FormikProvider>
    );
}

export default AccountInfoForm;
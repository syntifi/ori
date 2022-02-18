import React, { ReactElement, FC, useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
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
import { OriAPI } from "../OriAPI"

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
  timeStamp: yup.date()
});

/**
 * Formik form to input account and date.
 *  
 * @param onSubmit function 
 * @returns Formik form
 */
const AccountInfoForm: FC<any> = ({ onSubmit, submit, date }): ReactElement => {
  const { register, handleSubmit, setValue, getValues, formState: { errors } } = useForm({
    resolver: yupResolver(validationSchema)
  });
  const [tokens, setTokens] = useState([]);
  const timeStamp = getValues('timeStamp') as Date;
  const [dateTime, setDateTime] = useState<any>(null);

  useEffect(() => {
    register('timeStamp');
  }, [register]);

  useEffect(() => {
    setDateTime(timeStamp || null);
  }, [setDateTime, timeStamp]);


  useEffect(() => {
    setDateTime(new Date())
    OriAPI.get('token').then(response => {
      const symbols = response.data.map((obj: any) => obj.symbol);
      setTokens(symbols);
    }).catch((e) => {
      console.log("Error when retrieving data from backend application")
      console.log(e.message);
      setTokens([]);
    })
  }, []);


  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <FormControl variant="standard" fullWidth>
        <InputLabel id="tokenInputLabel">Token</InputLabel>
        <Select
          key="token"
          id="token"
          label="token"
          labelId="tokenInputLabel"
          inputProps={{ "data-testid": "select" }}
          value={getValues("token")}
          error={!!errors.token}
          onChange={(event) => setValue("token", event.target.value)}
        >
          {console.log(tokens)}
          {tokens.map((token, i) => <MenuItem key={token} value={token}>{token}</MenuItem>
          )}
        </Select>
      </FormControl>

      <TextField
        id="account"
        key="account"
        label="Account"
        error={!!errors.account}
        helperText={errors.account?.message}
        margin="dense"
        variant="standard"
        fullWidth
        {...register("account")}
      />

      <LocalizationProvider dateAdapter={AdapterDateFns}>
        <DateTimePicker
          renderInput={(props) => <TextField
            key="dateTime"
            variant="standard"
            margin="dense"
            error={errors.dateTime?.message}
            fullWidth
            {...props} />}
          label={date}
          value={dateTime}
          inputFormat="yyyy-MM-dd HH:mm:ss"
          onChange={(dt) => {
            setValue("timeStamp", dt, { shouldValidate: true, shouldDirty: true });
          }}
        />
      </LocalizationProvider>
      <div> </div>
      <Button color="primary" variant="contained" fullWidth type="submit">
        {submit}
      </Button>
    </form>
  );
}

export default AccountInfoForm;
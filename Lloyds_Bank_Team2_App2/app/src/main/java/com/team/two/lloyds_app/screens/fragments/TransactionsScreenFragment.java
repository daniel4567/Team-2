package com.team.two.lloyds_app.screens.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.team.two.lloyds_app.Constants;
import com.team.two.lloyds_app.exceptions.EmptyMandatoryFieldException;
import com.team.two.lloyds_app.R;
import com.team.two.lloyds_app.exceptions.InvalidAccountNumberFormatException;
import com.team.two.lloyds_app.exceptions.InvalidSortCodeFormatException;
import com.team.two.lloyds_app.objects.Account;
import com.team.two.lloyds_app.objects.Customer;
import com.team.two.lloyds_app.objects.Recipient;
import com.team.two.lloyds_app.screens.activities.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Daniel Baranowski
 * Date: 22/03/2015
 * Purpose: Code for transactions screen
 */


public class TransactionsScreenFragment extends android.support.v4.app.Fragment {
    public static final String TITLE = "Transfers";
    private View root;

    //UI references transfers
    private Spinner transferFrom;
    private Spinner transferTo;
    private EditText transferAmountText;
    private Button transferButton;

    //UI references payments
    private Spinner paymentFrom;
    private Spinner paymentRecipient;
    private EditText paymentAmountText;
    private EditText descriptionText;
    private Button paymentButton;
    private Button addRecipientButton;

    private ArrayList<Account> accounts;
    private List<String> accountNames;
    private HashMap<String, Account> mapAccounts;
    private ArrayList<Recipient> recipients;
    private List<String> recipientsNames;
    private HashMap<String, Recipient> mapRecipients;

    public TransactionsScreenFragment() {
        // Required empty public constructor
    }

    @Override//onCreate - creates activity.
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().setTitle(TITLE);
    }

    @Override//onCreateView - Initialises the view and inflates the layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        accounts = ((MainActivity) getActivity()).getAccounts();

        root = inflater.inflate(R.layout.fragment_transfer, container, false);

        //Transfers
        mapAccounts();
        transferFrom = (Spinner) root.findViewById(R.id.spinner_transfer_from);
        transferTo = (Spinner) root.findViewById(R.id.spinner_transfer_recipient);
        transferAmountText = (EditText) root.findViewById(R.id.transfer_amount_text);
        transferButton = (Button) root.findViewById(R.id.button_transfer);

        transferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordValidationDialog("Transfer");
            }
        });

        ArrayAdapter<String> fromAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getListExcept(""));
        ArrayAdapter<String> toAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getListExcept(""));

        transferFrom.setAdapter(fromAdapter);
        transferFrom.setOnItemSelectedListener(new CustomOnItemSelectedListener(toAdapter));

        transferTo.setAdapter(toAdapter);
        transferTo.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        //Payments
        paymentFrom = (Spinner) root.findViewById(R.id.spinner_payment_from);
        paymentRecipient = (Spinner) root.findViewById(R.id.spinner_payment_recipient);
        paymentAmountText = (EditText) root.findViewById(R.id.payment_amount_text);
        descriptionText = (EditText) root.findViewById(R.id.description_text);
        paymentButton = (Button) root.findViewById(R.id.button_payment);
        addRecipientButton = (Button) root.findViewById(R.id.button_new_recipient);

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordValidationDialog("Payment");
            }
        });

        addRecipientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipientDialog();
            }
        });

        recipients = ((MainActivity) getActivity()).getRecipients();
        mapRecipients();

        ArrayAdapter<String> paymentFromAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountNames);
        paymentFrom.setAdapter(paymentFromAdapter);
        paymentFrom.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        ArrayAdapter<String> paymentRecipientAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, recipientsNames);
        paymentRecipient.setAdapter(paymentRecipientAdapter);
        paymentRecipient.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        return root;
    }

    //transfer()- The function for transferring Money between personal accounts
    public void transfer() {
        Account destination = mapAccounts.get((String) transferTo.getSelectedItem());
        Account source = mapAccounts.get((String) transferFrom.getSelectedItem());
        Double balance;

        try {
            ((MainActivity) getActivity()).checkEmptyField(transferAmountText, "Balance");
            balance = Double.parseDouble(transferAmountText.getText().toString());

            CharSequence result;

            if (balance > 0) {
                if (balance <= source.getAvailableBalance()) {
                    updateTransaction(balance);
                    checkAchievement1(balance);
                    checkAchievement3();
                    checkAchievement4(balance);

                    result = "Successful Transfer";
                    ((MainActivity) getActivity()).getAdapter().transfer(source, destination, balance);

                    // get the main account
                    String accountName = "Account";
                    if(accountName.equals(destination.getAccountName()))
                    {
                        checkAchievement4(destination.getAccountBalance()+balance);
                    }

                    ((MainActivity) getActivity()).openHome();
                } else {
                    result = Constants.TOAST_NO_FUNDS;
                }

            } else {
                result = Constants.TOAST_BELOW_ZERO;
            }

            Context context = getActivity().getApplicationContext();

            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, result, duration);
            toast.show();

        } catch (NumberFormatException e) {
            balance = 0.0;
            Context context = getActivity().getApplicationContext();

            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, Constants.TOAST_NO_VALUE, duration);
            toast.show();

        } catch (EmptyMandatoryFieldException e) {
            balance = 0.0;
            Context context = getActivity().getApplicationContext();

            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, Constants.TOAST_NO_VALUE, duration);
            toast.show();
        }


    }

    //payment()- The function for transferring a payment to an external account
    public void payment() {
        Account source = mapAccounts.get((String) paymentFrom.getSelectedItem());
        Recipient destination = mapRecipients.get((String) paymentRecipient.getSelectedItem());
        Double balance;

        try {
            balance = Double.parseDouble(paymentAmountText.getText().toString());
            ((MainActivity) getActivity()).checkEmptyField(descriptionText, "Description");
            String description = descriptionText.getText().toString();
            CharSequence result;

            if (balance > 0) {
                if (balance <= source.getAvailableBalance()) {
                    updateTransaction(balance);
                    checkAchievement2(balance);
                    checkAchievement3();

                    result = "Successful Payment";
                    ((MainActivity) getActivity()).getAdapter().payment(source, destination, balance, description);
                    ((MainActivity) getActivity()).openHome();

                } else {
                    result = Constants.TOAST_NO_FUNDS;
                }

            } else {
                result = Constants.TOAST_BELOW_ZERO;
            }

            Context context = getActivity().getApplicationContext();

            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, result, duration);
            toast.show();

        } catch (NumberFormatException e) {
            balance = 0.0;
            Context context = getActivity().getApplicationContext();

            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, Constants.TOAST_NO_VALUE, duration);
            toast.show();
        } catch (EmptyMandatoryFieldException e) {
            Context context = getActivity().getApplicationContext();

            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, Constants.TOAST_MANDATORY_DATA, duration);
            toast.show();
        }
    }

    public void addRecipientDialog() {
        // Create custom dialog object
        final Dialog dialog = new Dialog(getActivity());
        // Include dialog.xml file
        dialog.setContentView(R.layout.add_recipment_dialog);
        // Set dialog title
        dialog.setTitle("Add Recipient");

        //UI References
        final EditText name;
        final EditText accountNumber;
        final EditText sortCode;
        final Button addRecipient;

        name = (EditText) dialog.findViewById(R.id.description_text);
        accountNumber = (EditText) dialog.findViewById(R.id.account_number_text);
        sortCode = (EditText) dialog.findViewById(R.id.sort_code_text);
        addRecipient = (Button) dialog.findViewById(R.id.add_recipient_button);

        addRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int accountNumberText;
                try {
                    ((MainActivity) getActivity()).checkEmptyField(name, "Name");
                    ((MainActivity) getActivity()).checkEmptyField(sortCode, "Sort Code");
                    String nameText = name.getText().toString();
                    validateSortCode(sortCode.getText().toString());
                    String sortCodeText = sortCode.getText().toString();
                    validateAccountNumber(accountNumber.getText().toString());
                    accountNumberText = Integer.parseInt(accountNumber.getText().toString());
                    ((MainActivity) getActivity()).addRecipient(nameText, sortCodeText, accountNumberText);
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity(), "Recipient Added", duration);
                    toast.show();
                    dialog.dismiss();
                    ((MainActivity) getActivity()).openTransfers();
                    dialog.dismiss();

                } catch (NumberFormatException e) {
                    accountNumberText = 0;
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity(), "Please enter account number", duration);
                    toast.show();
                } catch (EmptyMandatoryFieldException e) {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity(), Constants.TOAST_MANDATORY_DATA, duration);
                    toast.show();
                } catch (InvalidSortCodeFormatException e) {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity(), Constants.TOAST_WRONG_SORT_CODE, duration);
                    toast.show();
                } catch (InvalidAccountNumberFormatException e) {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity(), Constants.TOAST_WRONG_ACCOUNT_NUMBER, duration);
                    toast.show();
                }
            }
        });

        dialog.show();
    }

    public void validateSortCode(String sortCode) throws InvalidSortCodeFormatException {
        if (!sortCode.matches("^(\\d){2}-(\\d){2}-(\\d){2}$")) {
            throw new InvalidSortCodeFormatException();
        }
    }

    public void validateAccountNumber(String number) throws InvalidAccountNumberFormatException {
        if (!number.matches("^(\\d){7,8}$")) {
            throw new InvalidAccountNumberFormatException();
        }
    }


    public void passwordValidationDialog(final String caller) {
        // Create custom dialog object
        final Dialog dialog = new Dialog(getActivity());
        // Include dialog.xml file
        dialog.setContentView(R.layout.password_validation_dialog);
        // Set dialog title
        dialog.setTitle("Enter Password");

        //UI References
        final EditText password = (EditText) dialog.findViewById(R.id.dialog_password);
        Button loginButton = (Button) dialog.findViewById(R.id.dialog_sign_in_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = ((MainActivity) getActivity()).getCustomer().getUserId();

                if (((MainActivity) getActivity()).getAdapter().login(id, password.getText().toString())) {
                    if (caller.equals("Transfer")) {
                        transfer();
                    }

                    if (caller.equals("Payment")) {
                        payment();
                    }
                    dialog.dismiss();
                } else {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getActivity(), "Wrong Password", duration);
                    toast.show();
                }
            }
        });

        dialog.show();
    }

    private void mapAccounts() {
        accountNames = new ArrayList<>();
        mapAccounts = new HashMap<>();
        for (Account a : accounts) {
            String name = a.getAccountName();
            accountNames.add(name);
            mapAccounts.put(a.getAccountName(), a);
        }
    }

    private void mapRecipients() {
        recipientsNames = new ArrayList<>();
        mapRecipients = new HashMap<>();
        for (Recipient r : recipients) {
            String name = r.getName();
            recipientsNames.add(name);
            mapRecipients.put(name, r);
        }
    }

    private List<String> getListExcept(String except) {
        List<String> list = new ArrayList<String>();
        for (Account a : accounts) {
            if (!a.getAccountName().equals(except)) {
                list.add(a.getAccountName());
            }
        }
        return list;
    }

    class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        private ArrayAdapter<String> dependent;

        public CustomOnItemSelectedListener(ArrayAdapter<String> dependent) {
            this.dependent = dependent;
        }

        public CustomOnItemSelectedListener() {
            dependent = null;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selected = (String) parent.getSelectedItem();
            if (dependent != null) {
                dependent.clear();
                dependent.addAll(getListExcept(selected));
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    @Override
    public void onResume() {
        accounts = ((MainActivity) getActivity()).getAccounts();
        recipients = ((MainActivity) getActivity()).getRecipients();
        mapAccounts();
        mapRecipients();

        ArrayAdapter<String> fromAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accountNames);
        paymentFrom.setAdapter(fromAdapter);
        paymentFrom.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        ArrayAdapter<String> recipientAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, recipientsNames);
        paymentRecipient.setAdapter(recipientAdapter);
        paymentRecipient.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        super.onResume();
    }

    public void updateTransaction(double amount)
    {
        MainActivity context = (MainActivity)getActivity();
        Customer customer = context.getCustomer();
        int customerId = customer.getId();

        context.updateTransactionsTotal(customerId, amount);
    }

    // Check if £200 or more is transferred to a personal account
    public void checkAchievement1(double balance)
    {
        if(balance>=200)
        {
            // Add the achievement to the database
            MainActivity context = (MainActivity)getActivity();
            Customer customer = context.getCustomer();
            int customerId = customer.getId();

            // Check that achievement has not already been achieved
            if(!context.achievementIsComplete(1, customerId))
            {
                context.addCompletedAchievement(1, customer.getId());
                context.gainOffersPoints(50);

                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getActivity(), "You completed the £200 Personal transaction achievement!", duration);
                toast.show();
            }
        }
    }

    // Check if £200 or more is transferred to an external account
    public void checkAchievement2(double balance)
    {
        if(balance>=200)
        {
            // Add the achievement to the database
            MainActivity context = (MainActivity)getActivity();
            Customer customer = context.getCustomer();
            int customerId = customer.getId();

            // Check that achievement has not already been achieved
            if(!context.achievementIsComplete(2, customerId))
            {
                context.addCompletedAchievement(2, customer.getId());
                context.gainOffersPoints(50);

                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity(), "You completed the £200 Outgoing transaction achievement!", duration);
                toast.show();
            }
        }
    }

    // Check if at least £10,000 has been transferred until now
    public void checkAchievement3()
    {
        MainActivity context = (MainActivity)getActivity();
        Customer customer = context.getCustomer();
        int customerId = customer.getId();
        double transactionTotal = context.getTransactionsTotal(customerId);

        if(transactionTotal>=10000 && !context.achievementIsComplete(3, customerId))
        {
            context.addCompletedAchievement(3, customer.getId());
            context.gainOffersPoints(500);

            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getActivity(), "You completed the £10,000 total transactions achievement!", duration);
            toast.show();
        }
    }

    // Check if current balance is 5000 or more
    public void checkAchievement4(double balance)
    {
        if(balance>=5000)
        {
            // Add the achievement to the database
            MainActivity context = (MainActivity)getActivity();
            Customer customer = context.getCustomer();
            int customerId = customer.getId();

            // Check that achievement has not already been achieved
            if(!context.achievementIsComplete(4, customerId))
            {
                context.addCompletedAchievement(4, customer.getId());
                context.gainOffersPoints(100);

                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity(), "You completed the £5000 balance achievement!", duration);
                toast.show();
            }
        }
    }
}

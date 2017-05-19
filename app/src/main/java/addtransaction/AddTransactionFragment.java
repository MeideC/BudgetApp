package addtransaction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ToggleButton;

import com.example.mikko.budgetapplication.R;

import java.util.ArrayList;
import java.util.Date;

import data.Transaction;
import data.TransactionType;
import data.TransactionTypeData;
import datahandler.BackendlessDataSaver;
import datahandler.BackendlessDataSaverInterface;
import payment.history.MonthlyPagerAdapter;
import payment.history.ShowHistoryActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Mikko on 8.5.2017.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class AddTransactionFragment extends Fragment implements View.OnClickListener {
    private static final int NEW_TRANSACTION_TYPE_CODE = 1;
    public static final String NEW_TRANSACTION_TYPE_SEND_CODE = "2";
    public static final String TRANSACTION_TYPE_CHECKER = "3";

    private boolean isShared;
    // the new transaction is either payment or an income
    // this boolean is a quick way to check which we are creating at the moment
    // (= which tab the user has open)
    private boolean isPayment;

    private ToggleButton buttonPersonal;
    private ToggleButton buttonShared;
    private EditText expenseAmountEditText;
    private EditText locationText;


    private ArrayList<TransactionType> transactionTypes;

    private ViewPager viewPager;
    private MonthlyPagerAdapter monthlyPagerAdapter;

    // these are for picking the transaction type
    private TransactionTypePickAdapter transactionTypePickAdapter;
    private GridView transactionTypeGridView;
    private TransactionType contextMenyTransactionType;


    // newInstance constructor for creating fragment with arguments
    public static AddTransactionFragment newInstance(boolean isPayment, TransactionTypeData transactionTypeData) {
        AddTransactionFragment tabFragment = new AddTransactionFragment();
        Bundle args = new Bundle();
        args.putBoolean("isPayment", isPayment);
        args.putSerializable("transactionTypeData", transactionTypeData);
        tabFragment.setArguments(args);
        return tabFragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isPayment = getArguments().getBoolean("isPayment");
        transactionTypes = ((TransactionTypeData) getArguments().getSerializable("transactionTypeData")).getTransactionTypeList();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);



        // find the fields and buttons
        expenseAmountEditText = (EditText) view.findViewById(R.id.add_transaction_expense_field);
        locationText = (EditText) view.findViewById(R.id.add_transaction_location_field);
        buttonPersonal = (ToggleButton) view.findViewById(R.id.add_transaction_toggle_personal);
        buttonShared = (ToggleButton) view.findViewById(R.id.add_transaction_toggle_shared);

        transactionTypeGridView = (GridView) view.findViewById(R.id.add_transaction_type_picker_grid_layout);
        transactionTypeGridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        refreshTransactionTypes();
        registerForContextMenu(transactionTypeGridView);

        // set the transaction visibility to shared as default
        setVisibility("shared");

        // set onClicklistener to the buttons
        buttonPersonal.setOnClickListener(this);
        buttonShared.setOnClickListener(this);

        view.findViewById(R.id.button_save_transaction).setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        System.out.println("RESUMED TABFRAGMENT");
    }


    /*****************************************************************************
     *
     * personal/shared stuff
     */

    public void setVisibility(String visibility) {
        if (visibility.equals("shared")) {
            buttonPersonal.setChecked(false);
            buttonPersonal.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            buttonPersonal.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
            buttonShared.setChecked(true);
            buttonShared.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            buttonShared.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            isShared = true;

        } else if (visibility.equals("personal")){
            buttonPersonal.setChecked(true);
            buttonPersonal.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            buttonPersonal.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            buttonShared.setChecked(false);
            buttonShared.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
            buttonShared.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBlack));
            isShared = false;

        } else {
            Log.d("debug", "bug in set visibility method in AddPaymentActivity");
        }
    }


    /***********************************************
     *  On Click
     *  what to do when buttons are pressed inside the fragment
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_transaction_toggle_personal:
                setVisibility("personal");
                break;
            case R.id.add_transaction_toggle_shared:
                setVisibility("shared");
                break;
            case R.id.add_transaction_type_button:
                addTransactionType();
                break;
            case R.id.button_save_transaction:
                saveTransaction();
                break;
        }
    }

    /**********************************************
     *  Transaction type stuff
     */

   // used if a new payment type is added
    public void refreshTransactionTypes() {
        transactionTypePickAdapter = new TransactionTypePickAdapter(
                getContext(), this, R.layout.transaction_type, transactionTypes, transactionTypes.size() - 1, true
        );
        transactionTypeGridView.setAdapter(transactionTypePickAdapter);
    }

    public void addTransactionType() {
        Intent addTransactionTypeIntent = new Intent(getContext(), AddTransactionTypeActivity.class);
        // inform the type creator activity which kind of transaction type we are about to create (payment/income)
        addTransactionTypeIntent.putExtra(TRANSACTION_TYPE_CHECKER, isPayment);
        startActivityForResult(addTransactionTypeIntent, NEW_TRANSACTION_TYPE_CODE);

    }
     @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_TRANSACTION_TYPE_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                System.out.println("sorry it not show ur new type, please reopen this activity");
            }
        }
    }

    /***********************************************
     *  Saving the transaction
     */
    public void saveTransaction() {
        System.out.println("helo");
        System.out.println("expense amount " + expenseAmountEditText.getText());

        // check if the user hasn't entered any expense amount to the form
        if (TextUtils.isEmpty(expenseAmountEditText.getText())) {
            expenseAmountEditText.requestFocus();
            expenseAmountEditText.setError("The transaction must have a value!");
            System.out.println("rip");
        } else {

            // create new transaction object
            Transaction newTransaction = new Transaction();

            // set the transaction amount
            newTransaction.setAmount(Double.parseDouble(expenseAmountEditText.getText().toString()));

            // set ispayment
            newTransaction.setPayment(isPayment);

            // set the transactionType
            newTransaction.setTransactionType(transactionTypePickAdapter.getCurrentType());

            // set the date of transaction
            newTransaction.setDateInMilliseconds(new Date().getTime());

            // save the new transaction in to the backendless
            BackendlessDataSaver.saveTransaction(getContext(), newTransaction);
            System.out.println("checked item positions: " + transactionTypeGridView.getCheckedItemPosition());
            System.out.println("focused child: " + transactionTypeGridView.getFocusedChild());
            System.out.println("selector states: " + transactionTypeGridView.getSelector().getState());

            // then just wait for the response
            // the save successful goes back to AddTransactionActivity
            // because the BackendlessDataSaver uses the context to reach back to the app
        }
    }


}
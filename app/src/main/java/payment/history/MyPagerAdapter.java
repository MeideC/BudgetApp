package payment.history;

/**
 * Created by Mikko on 1.8.2016.
 *
 * This is the pagerAdapter for the three different tabs: Personal, shared and both
 *
 */
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.mikko.budgetapplication.ConstantVariableSettings;

import java.util.ArrayList;

import data.Transaction;
import data.TransactionData;

public class MyPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private int startingMonth;

    private ArrayList<Transaction> payments;
    private ArrayList<Transaction> incomes;

    private Context context;


    public MyPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<Transaction> payments, ArrayList<Transaction> incomes, Context context, int startingMonth) {
        super(fm);
        this.context = context;
        this.mNumOfTabs = NumOfTabs;
        this.startingMonth = startingMonth;
        this.payments = new ArrayList<>();
        for (Transaction payment : payments) {
            this.payments.add(payment);
        }

        this.incomes = new ArrayList<>();
        for (Transaction income : incomes) {
            this.incomes.add(income);
        }
    }

    @Override
    public Fragment getItem(int position) {

        // for now just a hard number of months
        int numberOfMonths = ConstantVariableSettings.NUMBER_OF_MONTHS;
        boolean isShared;

        TransactionData paymentData = new TransactionData();
        TransactionData incomeData = new TransactionData();

        switch (position) {
            case 0:
                paymentData.setTransactionList(payments);
                incomeData.setTransactionList(incomes);
                return TabFragment.newInstance("first tab", numberOfMonths, paymentData, incomeData, startingMonth);
            case 1:
                isShared = false;
                paymentData.setTransactionList(sortTransactions(payments, isShared));
                incomeData.setTransactionList(sortTransactions(incomes, isShared));
                return TabFragment.newInstance("second tab", numberOfMonths, paymentData, incomeData, startingMonth);
            case 2:
                isShared = true;
                paymentData.setTransactionList(sortTransactions(payments, isShared));
                incomeData.setTransactionList(sortTransactions(incomes, isShared));
                return TabFragment.newInstance("third tab", numberOfMonths, paymentData, incomeData, startingMonth);
            default:
                return TabFragment.newInstance("manyth tab", numberOfMonths, paymentData, incomeData, startingMonth);
        }
    }

    private ArrayList<Transaction> sortTransactions(ArrayList<Transaction> transactions, boolean isShared) {
        ArrayList<Transaction> sortedList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.isShared() == isShared) {
                sortedList.add(transaction);
            }
        }
        return sortedList;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public void setCurrentMonthPosition(int tabPosition) {
        //((TabFragment) getItem(tabPosition)).setMonthPosition();
    }
}
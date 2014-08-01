package com.code44.finance.data.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.code44.finance.App;
import com.code44.finance.common.model.AccountOwner;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.utils.IOUtils;

public class Account extends BaseModel {
    public static final Parcelable.Creator<Account> CREATOR = new Parcelable.Creator<Account>() {
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    private static Account systemAccount;

    private Currency currency;
    private String title;
    private String note;
    private long balance;
    private AccountOwner accountOwner;

    public Account() {
        super();
        setCurrency(Currency.getDefault());
        setTitle(null);
        setNote(null);
        setBalance(0);
        setAccountOwner(AccountOwner.USER);
    }

    public Account(Parcel in) {
        super(in);
    }

    public static Account getSystem() {
        if (systemAccount == null) {
            final Cursor cursor = Query.create()
                    .projectionId(Tables.Accounts.ID)
                    .projection(Tables.Accounts.PROJECTION)
                    .selection(Tables.Accounts.OWNER.getName() + "=?", String.valueOf(AccountOwner.SYSTEM.asInt()))
                    .from(App.getAppContext(), AccountsProvider.uriAccounts())
                    .execute();

            systemAccount = Account.from(cursor);
            IOUtils.closeQuietly(cursor);
        }
        return systemAccount;
    }

    public static Account from(Cursor cursor) {
        final Account account = new Account();
        if (cursor.getCount() > 0) {
            account.updateFrom(cursor, null);
        }
        return account;
    }

    public static Account fromAccountFrom(Cursor cursor) {
        final Account account = new Account();
        if (cursor.getCount() > 0) {
            account.updateFrom(cursor, Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT);
        }
        return account;
    }

    public static Account fromAccountTo(Cursor cursor) {
        final Account account = new Account();
        if (cursor.getCount() > 0) {
            account.updateFrom(cursor, Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT);
        }
        return account;
    }

    @Override
    protected Column getIdColumn() {
        return Tables.Accounts.ID;
    }

    @Override
    protected Column getServerIdColumn() {
        return Tables.Accounts.SERVER_ID;
    }

    @Override
    protected Column getModelStateColumn() {
        return Tables.Accounts.MODEL_STATE;
    }

    @Override
    protected Column getSyncStateColumn() {
        return Tables.Accounts.SYNC_STATE;
    }

    @Override
    protected void fromParcel(Parcel parcel) {
        setCurrency((Currency) parcel.readParcelable(Currency.class.getClassLoader()));
        setTitle(parcel.readString());
        setNote(parcel.readString());
        setBalance(parcel.readLong());
        setAccountOwner(AccountOwner.fromInt(parcel.readInt()));
    }

    @Override
    protected void toParcel(Parcel parcel) {
        parcel.writeParcelable(getCurrency(), 0);
        parcel.writeString(getTitle());
        parcel.writeString(getNote());
        parcel.writeLong(getBalance());
        parcel.writeInt(getAccountOwner().asInt());
    }

    @Override
    protected void toValues(ContentValues values) {
        values.put(Tables.Accounts.CURRENCY_ID.getName(), currency.getId());
        values.put(Tables.Accounts.TITLE.getName(), title);
        values.put(Tables.Accounts.NOTE.getName(), note);
        values.put(Tables.Accounts.BALANCE.getName(), balance);
        values.put(Tables.Accounts.OWNER.getName(), accountOwner.asInt());
    }

    @Override
    protected void fromCursor(Cursor cursor, String columnPrefixTable) {
        int index;

        // Currency
        Currency currency;
        if (TextUtils.isEmpty(columnPrefixTable)) {
            currency = Currency.from(cursor);
        } else if (columnPrefixTable.equals(Tables.Accounts.TEMP_TABLE_NAME_FROM_ACCOUNT)) {
            currency = Currency.fromCurrencyFrom(cursor);
        } else if (columnPrefixTable.equals(Tables.Accounts.TEMP_TABLE_NAME_TO_ACCOUNT)) {
            currency = Currency.fromCurrencyTo(cursor);
        } else {
            throw new IllegalArgumentException("Table prefix " + columnPrefixTable + " is not supported.");
        }
        index = cursor.getColumnIndex(Tables.Accounts.CURRENCY_ID.getName(columnPrefixTable));
        if (index >= 0) {
            currency.setId(cursor.getLong(index));
        } else {
            currency.setId(0);
        }
        setCurrency(currency);

        // Title
        index = cursor.getColumnIndex(Tables.Accounts.TITLE.getName(columnPrefixTable));
        if (index >= 0) {
            setTitle(cursor.getString(index));
        }

        // Note
        index = cursor.getColumnIndex(Tables.Accounts.NOTE.getName(columnPrefixTable));
        if (index >= 0) {
            setNote(cursor.getString(index));
        }

        // Balance
        index = cursor.getColumnIndex(Tables.Accounts.BALANCE.getName(columnPrefixTable));
        if (index >= 0) {
            setBalance(cursor.getInt(index));
        }

        // Owner
        index = cursor.getColumnIndex(Tables.Accounts.OWNER.getName(columnPrefixTable));
        if (index >= 0) {
            setAccountOwner(AccountOwner.fromInt(cursor.getInt(index)));
        }
    }

    @Override
    public void checkValues() throws IllegalStateException {
        super.checkValues();

        if (currency == null) {
            throw new IllegalStateException("Currency cannot be null.");
        }

        if (accountOwner == null) {
            throw new IllegalStateException("Owner cannot be null.");
        }
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public AccountOwner getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(AccountOwner accountOwner) {
        this.accountOwner = accountOwner;
    }
}
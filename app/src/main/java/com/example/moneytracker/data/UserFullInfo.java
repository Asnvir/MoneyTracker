package com.example.moneytracker.data;

public class UserFullInfo {
    private final UserInfoModel userInfoModel;
    private final BalanceModel balanceModel;

    public UserFullInfo(UserInfoModel userInfoModel, BalanceModel balanceModel) {
        this.userInfoModel = userInfoModel;
        this.balanceModel = balanceModel;
    }

    public UserInfoModel getUserInfo() {
        return userInfoModel;
    }

    public BalanceModel getBalance() {
        return balanceModel;
    }
}

package com.example.moneytracker.data;

public class UserModel {
    private UserInfoModel userInfoModel;
    private BalanceModel balanceModel;

    public UserModel() {
        // Required empty public constructor
    }

    public UserModel(UserInfoModel userInfoModel, BalanceModel balanceModel) {
        this.userInfoModel = userInfoModel;
        this.balanceModel = balanceModel;
    }

    public UserInfoModel getUserInfoModel() {
        return userInfoModel;
    }

    public void setUserInfoModel(UserInfoModel userInfoModel) {
        this.userInfoModel = userInfoModel;
    }

    public BalanceModel getBalanceModel() {
        return balanceModel;
    }

    public void setBalanceModel(BalanceModel balanceModel) {
        this.balanceModel = balanceModel;
    }
}

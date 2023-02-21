//package com.example.moneytracker.model;
//
//
//
//import com.example.moneytracker.data.TransactionModel;
//import com.example.moneytracker.mainScreen.AddTransactionViewModel;
//import com.example.moneytracker.mainScreen.DashboardViewModel;
//import com.example.moneytracker.util.Constants;
//
//import io.reactivex.rxjava3.disposables.CompositeDisposable;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class UserDataService {
//    private DashboardViewModel.UserDataUpdatedCallback downloadUserDataCallback;
//    private AddTransactionViewModel.CategoriesUpdatedCallback downloadCategoriesCallback;
//    private boolean categoriesLoaded = false;
//    private List<TransactionModel> transactionModels = new ArrayList<>();
//    private final DatabaseHandler databaseHandler;
//    private boolean userDataLoaded = false;
//    private boolean transactionsUploaded = false;
//    private CompositeDisposable compositeDisposable;
//
//    private static final String TAG = Constants.UserDataService_TAG;
//
//    public UserDataService() {
//        databaseHandler = new DatabaseHandler();
//        compositeDisposable = new CompositeDisposable();
//    }
//
//    public void addDownloadTransactionsListener(DashboardViewModel.UserDataUpdatedCallback downloadUserDataListener) {
//        downloadUserDataCallback = downloadUserDataListener;
//    }
//
//    public void removeDownloadTransactionsListener(DashboardViewModel.UserDataUpdatedCallback listener) {
//        downloadUserDataCallback = null;
//    }
//
////    public void downloadUserData() {
////        userDataLoaded = false;
////        Disposable disposable = databaseHandler.getUserDataUpdates()
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(updatedData -> {
////                    userDataLoaded = true;
////                    if (downloadUserDataCallback != null) {
////                        downloadUserDataCallback.onTransactionsUpdated(updatedData);
////                    }
////                }, throwable -> {
////                    MySignal.getInstance().toast("Error: " + throwable.getMessage());
////                    Log.e(TAG, "Error downloading user data", throwable);
////                });
////        compositeDisposable.add(disposable);
////    }
//
//    public void addDownloadCategoriesListener(AddTransactionViewModel.CategoriesUpdatedCallback downloadCategoriesListener) {
//        downloadCategoriesCallback = downloadCategoriesListener;
//    }
//
////    public void downloadCategories() {
////        Disposable disposable = databaseHandler.downloadCategories()
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(categories -> {
////                    categoriesLoaded = true;
////                    if (downloadCategoriesCallback != null) {
////                        downloadCategoriesCallback.onCategoriesUpdated(categories);
////                    }
////                }, throwable -> {
////                    MySignal.getInstance().toast("Error: " + throwable.getMessage());
////                    Log.e(TAG, "Error downloading categories", throwable);
////                });
////        compositeDisposable.add(disposable);
////    }
//
//    public void removeDownloadCategoriesListener(AddTransactionViewModel.CategoriesUpdatedCallback downloadCategoriesListener) {
//        downloadCategoriesCallback = null;
//    }
//
////    public void uploadTransaction(TransactionModel transactionModel) {
////        Disposable disposable = databaseHandler.uploadTransaction(transactionModel)
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(transactionUploaded -> {
////                    // Transaction uploaded successfully
////                    uploadBalance(transactionUploaded.getType(), transactionModel.getAmount());
////                    MySignal.getInstance().toast("Transaction uploaded successfully");
////                }, throwable -> {
////                    // Error uploading transaction
////                    MySignal.getInstance().toast("Error uploading transaction: " + throwable.getMessage());
////                    Log.e(TAG, "Error uploading transaction", throwable);
////                });
////        compositeDisposable.add(disposable);
////    }
//
////    private void uploadBalance(String type, double amount) {
////        Disposable disposable = databaseHandler.updateBalance(type, amount)
////                .subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(
////                        result -> {
////                            MySignal.getInstance().toast("Balance updated successfully");
////                        },
////                        error -> {
////                            MySignal.getInstance().toast("Error updating balance: " + error.getMessage());
////                            Log.e(TAG, "Error updating balance", error);
////                        }
////                );
////        compositeDisposable.add(disposable);
////    }
//
//    public void disposeAll() {
//        if (compositeDisposable != null) {
//            compositeDisposable.dispose();
//        }
//    }
//
//
//}
//
